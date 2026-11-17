package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import org.junit.*;
import rx.Single;
import java.lang.reflect.Proxy;
import java.util.*;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.*;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardConditionFromInvestigatorData;
import sk.sivak.eldritchhorror.core.eventtype.data.GainConditionFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class SecondWaveInvestigatorAbilitiesTest {
    private Investigators investigators;
    private ConditionsDeck conditions;
    private EventQueueImpl events;
    private TestFlavorType flavorType;
    private int questions, improvements;
    private final Map<InvestigatorId, List<AssetId>> assets = new HashMap<>();
    private final List<InvestigatorId> clues = new ArrayList<>();
    private final List<ConditionId> gainedConditions = new ArrayList<>();
    private final List<InvestigatorId> discardedConditionOwners = new ArrayList<>();

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        investigators = new Investigators();
        investigators.initAvailableInvestigators(true);
        InvestigatorId[] ids = {InvestigatorId.THE_REPORTER, InvestigatorId.THE_PROFESSOR, InvestigatorId.THE_DILETTANTE, InvestigatorId.THE_SALESMAN};
        investigators.initWithPlayers(ids.length);
        InvestigatorInfo[] selected = new InvestigatorInfo[ids.length];
        for (int i = 0; i < ids.length; i++)
            for (InvestigatorInfo info : investigators.getAvailableInvestigators())
                if (info.getInvestigatorId() == ids[i]) selected[i] = info;
        investigators.initSelectedInvestigators(selected);
        investigators.changeActiveInvestigator(InvestigatorId.THE_SALESMAN);
        for (InvestigatorWrite investigator : investigators.getOnBoardInvestigators()) investigator.setCurrentLocationId(LocationId.LONDON);
        conditions = new ConditionsDeck();
        conditions.createDeck();
        events = new EventQueueImpl();
        ServicePlatform.get().setInvestigators(investigators);
        ServicePlatform.get().setConditionsDeck(conditions);
        ServicePlatform.get().setEventQueue(events);
        ServicePlatform.get().setTestFlavor(proxy(TestFlavorRead.class, (method, args) ->
                method.equals("getFlavorType") ? flavorType : null));
        ServicePlatform.get().setService(proxy(Service.class, (method, args) -> {
            if (method.equals("gainAssetFromDeck"))
                assets.computeIfAbsent((InvestigatorId) args[0], id -> new ArrayList<>()).add((AssetId) args[1]);
            if (method.equals("gainConditionFromDeck")) gainedConditions.add((ConditionId) args[1]);
            if (method.equals("discardConditionFromInvestigator")) {
                conditions.discard((InvestigatorId) args[0], (ConditionInfo) args[1]);
                discardedConditionOwners.add((InvestigatorId) args[0]);
            }
            if (method.equals("addEventCommand")) ((java8.features.function.Consumer<Object>) args[0]).accept(null);
            return null;
        }));
        ServicePlatform.get().setTokenService(proxy(TokenService.class, (method, args) -> {
            if (method.equals("gainClueFromPool")) clues.add((InvestigatorId) args[0]);
            return null;
        }));
        ServicePlatform.get().setInvestigatorService(proxy(InvestigatorService.class, (method, args) -> {
            if (method.equals("improveSkill")) improvements++;
            return null;
        }));
        ServicePlatform.get().setGameService(proxy(GameService.class, (method, args) -> {
            if (method.equals("ask")) {
                questions++;
                Question<?> question = (Question<?>) args[0];
                Answer<Object, Object> answer = new Answer<>();
                answer.setResponseData(question.getOptions().get(question.getOptions().size() - 1).getValue());
                return Single.just(answer);
            }
            return null;
        }));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void startingPossessionsMatchTheInvestigatorSheets() {
        new TheReporterInitListener().onNotify(InvestigatorId.THE_REPORTER);
        new TheProfessorInitListener().onNotify(InvestigatorId.THE_PROFESSOR);
        new TheDilettanteInitListener().onNotify(InvestigatorId.THE_DILETTANTE);
        new TheSalesmanInitListener().onNotify(InvestigatorId.THE_SALESMAN);
        assertEquals(Collections.singletonList(AssetId.LUCKY_TALISMAN), assets.get(InvestigatorId.THE_REPORTER));
        assertEquals(Collections.singletonList(AssetId.ANCIENT_TOME), assets.get(InvestigatorId.THE_PROFESSOR));
        assertEquals(Arrays.asList(AssetId.DOT_25_AUTOMATIC, AssetId.FINE_JEWELRY), assets.get(InvestigatorId.THE_DILETTANTE));
        assertEquals(Collections.singletonList(AssetId.WINCHESTER_RIFLE), assets.get(InvestigatorId.THE_SALESMAN));
        assertEquals(Collections.singletonList(ConditionId.CURSED), gainedConditions);
        assertEquals(Collections.singletonList(InvestigatorId.THE_SALESMAN), clues);
    }

    @Test public void bobReducesReserveCostOnlyForHisAcquireAssetsTest() {
        new TheSalesmanInitListener().justRegisterListeners();
        assertEquals(1, reserveCostReductionAfterReading(Stat.INFLUENCE, TestFlavorType.ACQUIRE_ASSETS));
        assertEquals(0, reserveCostReductionAfterReading(Stat.WILL, TestFlavorType.ACQUIRE_ASSETS));
        investigators.changeActiveInvestigator(InvestigatorId.THE_PROFESSOR);
        assertEquals(0, reserveCostReductionAfterReading(Stat.INFLUENCE, TestFlavorType.ACQUIRE_ASSETS));
    }

    @Test public void jennyLetsOneInvestigatorDiscardDebtOncePerRound() {
        new TheDilettanteInitListener().justRegisterListeners();
        conditions.gainCondition(ConditionId.DEBT, InvestigatorId.THE_PROFESSOR);
        events.fireAfterEvent(BeforeAfterEvent.GAIN_CONDITION_FROM_DECK, new GainConditionFromDeckData(InvestigatorId.THE_PROFESSOR, ConditionId.DEBT));
        assertEquals(Collections.singletonList(InvestigatorId.THE_PROFESSOR), discardedConditionOwners);
        assertFalse(conditions.hasCondition(InvestigatorId.THE_PROFESSOR, ConditionId.DEBT));
        conditions.gainCondition(ConditionId.DEBT, InvestigatorId.THE_SALESMAN);
        events.fireAfterEvent(BeforeAfterEvent.GAIN_CONDITION_FROM_DECK, new GainConditionFromDeckData(InvestigatorId.THE_SALESMAN, ConditionId.DEBT));
        assertEquals(1, discardedConditionOwners.size());
        events.fireDirectEvent(DirectEvent.REENABLE_DISABLED_ABILITIES, null);
        events.fireAfterEvent(BeforeAfterEvent.GAIN_CONDITION_FROM_DECK, new GainConditionFromDeckData(InvestigatorId.THE_SALESMAN, ConditionId.DEBT));
        assertEquals(Arrays.asList(InvestigatorId.THE_PROFESSOR, InvestigatorId.THE_SALESMAN), discardedConditionOwners);
    }

    @Test public void rexRegainsHisCurseWithClueAndImprovementAfterLosingIt() {
        new TheReporterInitListener().justRegisterListeners();
        ConditionInfo curse = conditions.gainCondition(ConditionId.CURSED, InvestigatorId.THE_REPORTER);
        conditions.discard(InvestigatorId.THE_REPORTER, curse);
        events.fireAfterEvent(BeforeAfterEvent.DISCARD_CONDITION_FROM_INVESTIGATOR, new DiscardConditionFromInvestigatorData(InvestigatorId.THE_REPORTER, curse));
        assertEquals(Collections.singletonList(InvestigatorId.THE_REPORTER), clues);
        assertEquals(1, improvements);
        assertEquals(Collections.singletonList(ConditionId.CURSED), gainedConditions);
        ConditionInfo otherCurse = conditions.gainCondition(ConditionId.CURSED, InvestigatorId.THE_PROFESSOR);
        conditions.discard(InvestigatorId.THE_PROFESSOR, otherCurse);
        events.fireAfterEvent(BeforeAfterEvent.DISCARD_CONDITION_FROM_INVESTIGATOR, new DiscardConditionFromInvestigatorData(InvestigatorId.THE_PROFESSOR, otherCurse));
        assertEquals(1, improvements);
    }

    private int reserveCostReductionAfterReading(Stat stat, TestFlavorType type) {
        flavorType = type;
        TestData data = new TestData();
        data.setStat(stat);
        events.fireAfterEvent(BeforeAfterEvent.READ_STAT, data);
        ShowReserveDragAndDropInput input = new ShowReserveDragAndDropInput();
        events.fireBeforeEvent(BeforeAfterEvent.SHOW_RESERVE_DRAG_AND_DROP, input);
        return input.getCostReduction();
    }

    private interface Call { Object invoke(String method, Object[] args); }
    private <T> T proxy(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{type}, (p, m, a) -> call.invoke(m.getName(), a)));
    }
}
