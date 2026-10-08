package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import java8.features.function.Consumer;
import org.junit.*;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class YigAutomaticSelectionTest {
    private ServicePlatform platform;
    private final List<InvestigatorRead> investigators = new ArrayList<>();
    private final Set<InvestigatorId> cursed = EnumSet.noneOf(InvestigatorId.class);
    private final List<InvestigatorId> cured = new ArrayList<>();
    private final List<AssetInfo> assets = new ArrayList<>(), discarded = new ArrayList<>();
    private final List<Runnable> pending = new ArrayList<>();
    private List<Runnable> inserted;
    private int selections;
    private InvestigatorRestriction restriction;
    private final PublishSubject<InvestigatorId> choice = PublishSubject.create();

    @Before public void setup() {
        ServicePlatform.nullifyInstance(); platform = ServicePlatform.get();
        platform.setService(mock(Service.class, (name, args) -> {
            if (name.equals("addEventCommand")) queue(() -> ((Consumer<Object>) args[0]).accept(null));
            if (name.equals("discardConditionFromInvestigator")) cured.add((InvestigatorId) args[0]);
            if (name.equals("discardAssetFromInvestigator")) queue(() -> {
                assertEquals(InvestigatorId.THE_SAILOR, args[0]);
                assertEquals(true, args[2]);
                assertTrue("Each discard must use a remaining possession", assets.remove(args[1]));
                discarded.add((AssetInfo) args[1]);
            });
            return null;
        }));
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> {
            if (name.equals("getOnBoardInvestigators")) return investigators;
            if (name.equals("getActiveInvestigatorId")) return InvestigatorId.THE_SAILOR;
            return investigator(InvestigatorId.THE_SAILOR);
        }));
        platform.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) -> {
            assertEquals(ConditionId.CURSED, args[1]);
            if (name.equals("hasCondition")) return cursed.contains(args[0]);
            return mock(ConditionInfo.class, (n, a) -> ConditionId.CURSED);
        }));
        platform.setInvestigatorService(mock(InvestigatorService.class, (name, args) -> {
            assertEquals("selectInvestigator", name); selections++;
            restriction = (InvestigatorRestriction) args[0]; return choice.toSingle();
        }));
        platform.setAssetDeck(mock(AssetDeckRead.class, (name, args) -> assets));
        platform.setArtifactsDeck(mock(ArtifactsDeckRead.class, (name, args) -> Collections.emptyList()));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("No selection question should be displayed", "showCard", name);
            ShowCardRequest request = (ShowCardRequest) args[0];
            assertTrue(assets.contains(request.getAssetInfo()));
            return Single.<ShowCardResponse>create(sub -> queue(() -> sub.onSuccess(ShowCardResponse.OK)));
        }));
    }
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void noCursedInvestigatorDoesNothing() {
        investigators.add(investigator(InvestigatorId.THE_SAILOR));
        YigEffects.cureCurse();
        assertTrue(cured.isEmpty()); assertEquals(0, selections);
    }
    @Test public void singleCursedInvestigatorIsCuredAutomatically() {
        addCursed(InvestigatorId.THE_SAILOR);
        investigators.add(investigator(InvestigatorId.THE_ASTRONOMER));
        YigEffects.cureCurse();
        assertEquals(Collections.singletonList(InvestigatorId.THE_SAILOR), cured);
        assertEquals(0, selections);
    }
    @Test public void multipleCursedInvestigatorsUseRestrictedInvestigatorPicker() {
        addCursed(InvestigatorId.THE_SAILOR); addCursed(InvestigatorId.THE_ASTRONOMER);
        investigators.add(investigator(InvestigatorId.THE_ACTRESS));
        YigEffects.cureCurse();
        assertTrue(cured.isEmpty()); assertEquals(1, selections);
        assertArrayEquals(new InvestigatorId[]{InvestigatorId.THE_SAILOR, InvestigatorId.THE_ASTRONOMER},
                restriction.getAllowedInvestigators());
        assertTrue(restriction.canBeDelayedOrDetained());
        choice.onNext(InvestigatorId.THE_ASTRONOMER); choice.onCompleted();
        assertEquals(Collections.singletonList(InvestigatorId.THE_ASTRONOMER), cured);
    }
    @Test public void multipleAlliesAreDiscardedWithoutSelectionOrRepeatingAPossession() {
        assets.add(asset("First ally", AssetTrait.ALLY));
        assets.add(asset("Second ally", AssetTrait.ALLY));
        AssetInfo item = asset("Item", AssetTrait.ITEM); assets.add(item);
        int[] done = {0};
        YigEffects.discardPossessions(AssetTrait.ALLY, 2, true, () -> done[0]++);
        assertTrue(discarded.isEmpty()); assertEquals(0, done[0]);
        drain();
        assertEquals(2, discarded.size()); assertNotSame(discarded.get(0), discarded.get(1));
        assertEquals(Collections.singletonList(item), assets); assertEquals(1, done[0]);
    }
    @Test public void fewerAlliesThanRequestedDiscardsOnlyAvailableAlliesAndCompletes() {
        assets.add(asset("Only ally", AssetTrait.ALLY));
        int[] done = {0};
        YigEffects.discardPossessions(AssetTrait.ALLY, 2, true, () -> done[0]++); drain();
        assertEquals(1, discarded.size()); assertEquals(1, done[0]);
    }
    @Test public void clueAutomaticallyMovesToANearestExpeditionEvenWhenTied() {
        LocationMap map = new LocationMap(); map.initLocations(); platform.setLocationMap(map);
        int[] ties = {0}, moves = {0};
        for (LocationId current : LocationId.values()) {
            List<LocationId> nearest = YigEffects.nearest(current, YigEffects.EXPEDITIONS);
            if (nearest.size() > 1) ties[0]++;
            platform.setTokenService(mock(TokenService.class, (name, args) -> {
                assertEquals("moveClue", name); assertEquals(LocationId.LONDON, args[0]);
                assertTrue(nearest.contains(args[1])); moves[0]++; return null;
            }));
            YigEffects.moveClueToExpedition(LocationId.LONDON, current);
        }
        assertTrue("Exercise a tied destination", ties[0] > 0);
        assertEquals(LocationId.values().length, moves[0]);
    }
    private void addCursed(InvestigatorId id) { investigators.add(investigator(id)); cursed.add(id); }
    private InvestigatorRead investigator(InvestigatorId id) {
        return mock(InvestigatorRead.class, (name, args) -> mock(InvestigatorInfo.class, (n, a) -> id));
    }
    private AssetInfo asset(String label, AssetTrait trait) {
        return mock(AssetInfo.class, (name, args) -> name.equals("getTraits") ? EnumSet.of(trait) : label);
    }
    private void queue(Runnable action) { (inserted == null ? pending : inserted).add(action); }
    private void drain() {
        int limit = 100;
        while (!pending.isEmpty()) {
            assertTrue("Command queue must finish", --limit > 0);
            inserted = new ArrayList<>(); pending.remove(0).run();
            pending.addAll(0, inserted); inserted = null;
        }
    }
    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> method.getName().equals("equals") ? proxy == args[0]
                        : handler.apply(method.getName(), args)));
    }
}
