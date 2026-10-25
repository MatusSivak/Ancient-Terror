package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import java8.features.function.Consumer;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.debt.DebtCondition;
import sk.sivak.eldritchhorror.core.constants.condition.paranoia.ParanoiaCondition;
import sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace.LostInTimeAndSpaceCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class YellowMythosCard13Test {
    private final InvestigatorId owner = InvestigatorId.THE_SAILOR, original = InvestigatorId.THE_SOLDIER;
    private InvestigatorId active = original;
    private final List<ConditionInfo> conditions = new ArrayList<>();
    private final List<String> effects = new ArrayList<>();
    private final Deque<Runnable> queue = new ArrayDeque<>();
    private final List<Runnable> held = new ArrayList<>();
    private int holds;
    private boolean accept = true;
    private ShowCardResponse confirmation = ShowCardResponse.YES;
    private ConditionInfo selected;
    private Question<?> offer;
    private ShowCardRequest shown;

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform p = ServicePlatform.get();
        InvestigatorInfo info = mock(InvestigatorInfo.class, (n,a) -> owner);
        InvestigatorRead investigator = mock(InvestigatorRead.class, (n,a) -> info);
        p.setInvestigators(mock(InvestigatorsRead.class, (n,a) -> n.equals("getActiveInvestigatorId") ? active : Collections.singletonList(investigator)));
        p.setConditionsDeck(mock(ConditionsDeckRead.class, (n,a) -> {
            if (n.equals("getConditions")) return conditions;
            if (n.equals("canGetConditionId")) return conditions.stream().noneMatch(c -> c.getId() == ConditionId.DEBT);
            throw new AssertionError(n);
        }));
        p.setService(mock(Service.class, (n,a) -> {
            switch (n) {
                case "hold": holds++; break;
                case "release":
                    if (--holds == 0) { for (int i=held.size()-1;i>=0;i--) queue.addFirst(held.get(i)); held.clear(); }
                    break;
                case "addEventCommand": enqueue(() -> ((Consumer<Object>)a[0]).accept(null)); break;
                case "discardConditionFromInvestigator": enqueue(() -> {
                    assertEquals(owner, a[0]); assertEquals(owner, active);
                    conditions.remove(a[1]); effects.add("discard:" + ((ConditionInfo)a[1]).getName());
                }); break;
                default: throw new AssertionError(n);
            }
            return null;
        }));
        p.setInvestigatorService(mock(InvestigatorService.class, (n,a) -> {
            if (n.equals("changeActiveInvestigator")) enqueue(() -> active = (InvestigatorId)a[0]);
            else if (n.equals("showActiveInvestigator")) enqueue(() -> {});
            else throw new AssertionError(n);
            return null;
        }));
        p.setGameService(mock(GameService.class, (n,a) -> {
            if (n.equals("ask")) return Single.create(sub -> enqueue(() -> {
                offer = (Question<?>)a[0]; Answer<Boolean,Object> answer = new Answer<>();
                answer.setResponseData(accept); sub.onSuccess(answer);
            }));
            if (n.equals("showCard")) return Single.create(sub -> enqueue(() -> {
                shown = (ShowCardRequest)a[0]; assertEquals(owner, active); sub.onSuccess(confirmation);
            }));
            if (n.equals("gainCondition")) { enqueue(() -> {
                assertEquals(owner, active); conditions.add(new DebtCondition()); effects.add("gain:Debt");
            }); return null; }
            throw new AssertionError(n);
        }));
        p.setCardService(mock(CardService.class, (n,a) -> Single.create(sub -> enqueue(() -> {
            assertEquals("selectSingleCard", n);
            assertTrue(((SelectCardData)a[0]).getAvailableCards().contains(selected)); sub.onSuccess(selected);
        }))));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }
    private void enqueue(Runnable task) { if (holds > 0) held.add(task); else queue.addFirst(task); }
    private void finish() { while (!queue.isEmpty()) queue.removeFirst().run(); assertEquals(0, holds); assertEquals(original, active); }
    private void resolve() { new YellowMythosCard13().execute(); finish(); }

    @Test public void noConditionProducesNoOfferOrDebt() { resolve(); assertNull(shown); assertNull(offer); assertTrue(effects.isEmpty()); }
    @Test public void existingDebtCannotBeExchanged() { conditions.add(new DebtCondition()); conditions.add(new BlessedCondition()); resolve(); assertNull(shown); assertNull(offer); assertTrue(effects.isEmpty()); }
    @Test public void lostInTimeAndSpaceCannotBeDiscarded() { conditions.add(new LostInTimeAndSpaceCondition()); resolve(); assertNull(shown); assertNull(offer); assertTrue(effects.isEmpty()); }
    @Test public void onlyBlessedIsShownByNameAndCanBeDeclined() {
        BlessedCondition blessed = new BlessedCondition(); conditions.add(blessed); confirmation = ShowCardResponse.NO;
        resolve(); assertSame(blessed, shown.getConditionInfo()); assertTrue(shown.getTitle().contains("Blessed"));
        assertEquals(DisplayAssetResponseType.YES_NO, shown.getDisplayAssetResponseType());
        assertEquals(HideType.DISCARD_ON_YES, shown.getHideType()); assertNull(offer); assertTrue(effects.isEmpty());
        assertTrue(conditions.contains(blessed));
    }
    @Test public void singleAcceptedConditionIsShownAndDiscardedAfterDebt() {
        conditions.add(new ParanoiaCondition()); resolve();
        assertEquals(Arrays.asList("gain:Debt", "discard:Paranoia"), effects);
        assertEquals(1, conditions.size()); assertEquals(ConditionId.DEBT, conditions.get(0).getId());
    }
    @Test public void multipleConditionsAreVisibleAndOnlyChosenCardIsDiscarded() {
        BlessedCondition blessed = new BlessedCondition(); selected = new ParanoiaCondition(); conditions.add(blessed); conditions.add(selected);
        resolve(); assertEquals(Question.SelectComponentsTableType.CARDS, offer.getSelectComponentsTableData().getType());
        assertEquals(Arrays.asList(blessed, selected), offer.getSelectComponentsTableData().getAvailableKeys());
        assertSame(selected, shown.getConditionInfo()); assertTrue(conditions.contains(blessed));
        assertEquals(Arrays.asList("gain:Debt", "discard:Paranoia"), effects);
    }
    @Test public void decliningMultipleConditionOfferChangesNothing() {
        conditions.add(new BlessedCondition()); conditions.add(new ParanoiaCondition()); accept = false;
        resolve(); assertNotNull(offer); assertNull(shown); assertTrue(effects.isEmpty());
    }
    @Test public void selectedConditionCanStillBeDeclined() {
        conditions.add(new BlessedCondition()); selected = new ParanoiaCondition(); conditions.add(selected); confirmation = ShowCardResponse.NO;
        resolve(); assertSame(selected, shown.getConditionInfo()); assertTrue(effects.isEmpty()); assertEquals(2, conditions.size());
    }
    @Test public void eligibilityIsCheckedWhenTheQueuedEffectRuns() {
        conditions.add(new BlessedCondition()); new YellowMythosCard13().execute(); conditions.clear(); finish();
        assertNull(shown); assertNull(offer); assertTrue(effects.isEmpty());
    }
    private <T> T mock(Class<T> type, BiFunction<String,Object[],Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},(p,m,a)->handler.apply(m.getName(),a)));
    }
}
