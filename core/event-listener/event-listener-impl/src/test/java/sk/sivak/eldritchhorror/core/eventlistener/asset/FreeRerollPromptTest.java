package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.artifact.*;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.test.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.*;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class FreeRerollPromptTest {
    private ShowCardRequest offered;
    private ShowCardResponse response = ShowCardResponse.NO;
    private TestFlavorType flavor = TestFlavorType.COMBAT;
    private TestData input;
    private final List<String> effects = new ArrayList<>();

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        platform.setEventQueue(new EventQueueImpl());
        platform.setAssetDeck(mock(AssetDeckRead.class, (name, args) -> Collections.emptyList()));
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> InvestigatorId.THE_SPY));
        ConditionInfo injury = mock(ConditionInfo.class, (name, args) -> Collections.singleton(ConditionTrait.INJURY));
        platform.setTestFlavor(mock(TestFlavorRead.class, (name, args) -> name.equals("getFlavorType") ? flavor : injury));
        platform.setService(mock(Service.class, (name, args) -> { effects.add(name); return null; }));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("showCard", name);
            offered = (ShowCardRequest) args[0];
            return Single.just(response);
        }));
        platform.setTestService(mock(TestService.class, (name, args) -> {
            assertEquals("rerollDie", name);
            assertSame(input, args[0]);
            effects.add(name);
            return null;
        }));
        platform.setTokenService(mock(TokenService.class, (name, args) -> {
            assertEquals("Declining must not spend anything", "canSpend", name);
            SpendData spend = new SpendData();
            spend.setHasEnough(true);
            return Single.just(spend);
        }));
        platform.setSpellsDeck(mock(SpellsDeckRead.class, (name, args) -> Collections.singletonList(null)));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void allFifteenFreeAssetsOptInAndYesOnlyRerolls() throws Exception {
        String[] names = {"AgentOfSecrets", "ArcaneScholar", "BullWhip", "ElderSign", "FishingNet",
                "HiredMuscle", "KingJamesBible", "LuckyRing", "MedicalJournal", "MuseumCurator",
                "PersonalAssistant", "PrivateInvestigator", "ProfaneTome", "SyndicateAgent", "VaticanMissionary"};
        Stat[] stats = {Stat.LORE, Stat.LORE, Stat.STRENGTH, Stat.WILL, Stat.STRENGTH, Stat.STRENGTH,
                Stat.WILL, Stat.INFLUENCE, Stat.STRENGTH, Stat.OBSERVATION, Stat.INFLUENCE,
                Stat.OBSERVATION, Stat.WILL, Stat.STRENGTH, Stat.WILL};
        response = ShowCardResponse.YES;
        for (int i = 0; i < names.length; i++) {
            flavor = names[i].equals("MedicalJournal") ? TestFlavorType.CONDITION
                    : names[i].equals("MuseumCurator") ? TestFlavorType.EXPEDITION : TestFlavorType.COMBAT;
            Object listener = asset(names[i]);
            input = data(stats[i], die(2, DiceRoll.Score.BAD), die(5, DiceRoll.Score.GOOD));
            offer(listener, "RerollUsingAssetsListener");
            assertNotNull(names[i], offered);
            assertTrue(names[i], offered.isAutoAcceptFreeReroll());
            assertEquals(names[i], Collections.singletonList("rerollDie"), effects);
        }
    }

    @Test public void sameCardCanHaveFreeAndExhaustingRerolls() throws Exception {
        TheSilverKeyListener listener = new TheSilverKeyListener();
        listener.registerWithOwner(InvestigatorId.THE_SPY, new TheSilverKeyArtifact());
        flavor = TestFlavorType.OTHER_WORLD;
        input = data(Stat.LORE, die(2, DiceRoll.Score.BAD));
        offer(listener, "RerollUsingAssetsListener");
        assertTrue(offered.isAutoAcceptFreeReroll());
        offer(listener, "UseKeyInsteadOfClueListener");
        assertFalse(offered.isAutoAcceptFreeReroll());
    }

    @Test public void exhaustedDiscardedAndSanityPaidRerollsStayManual() throws Exception {
        for (String name : new String[]{"LuckyRabbitsFoot", "MapOfTheLeyLines", "TearGas", "Axe"}) {
            flavor = name.equals("MapOfTheLeyLines") ? TestFlavorType.OTHER_WORLD : TestFlavorType.COMBAT;
            input = data(Stat.STRENGTH, die(2, DiceRoll.Score.BAD), die(2, DiceRoll.Score.BAD));
            offer(asset(name), "RerollUsingAssetsListener");
            assertNotNull(name, offered);
            assertFalse(name, offered.isAutoAcceptReroll());
        }
        for (String name : new String[]{"AlienDevice", "DragonIdol", "HyperboreanCrystal"}) {
            flavor = name.equals("AlienDevice") ? TestFlavorType.SPELL : TestFlavorType.COMBAT;
            input = data(name.equals("DragonIdol") ? Stat.STRENGTH : Stat.LORE, die(2, DiceRoll.Score.BAD));
            AbstractArtifactListener<?> listener = (AbstractArtifactListener<?>) Class.forName(
                    "sk.sivak.eldritchhorror.core.eventlistener.artifact." + name + "Listener").getDeclaredConstructor().newInstance();
            ArtifactInfo card = (ArtifactInfo) Class.forName(
                    "sk.sivak.eldritchhorror.core.constants.artifact." + name + "Artifact").getDeclaredConstructor().newInstance();
            listener.registerWithOwner(InvestigatorId.THE_SPY, card);
            offer(listener, "RerollUsingAssetsListener");
            assertNotNull(name, offered);
            assertFalse(name, offered.isAutoAcceptFreeReroll());
        }
    }

    @Test public void gamblersDiceAutoAcceptsOnlyItsMatchingFailedPairAndUsesTheCard() throws Exception {
        DiceRoll one = die(2, DiceRoll.Score.BAD), two = die(2, DiceRoll.Score.BAD);
        DiceRoll success = die(2, DiceRoll.Score.GOOD);
        one.setDiceNr(1); two.setDiceNr(2); success.setDiceNr(3);
        input = data(Stat.STRENGTH, one, two, success);
        Object listener = asset("GamblersDice");
        offer(listener, "RerollUsingAssetListener");
        assertTrue(offered.isAutoAcceptReroll());
        assertFalse(offered.isAutoAcceptFreeReroll());
        assertFalse(effects.contains("disableCard"));
        response = ShowCardResponse.YES;
        ServicePlatform.get().setTestService(mock(TestService.class, (name, args) -> {
            assertEquals("rerollDice", name);
            assertEquals(Arrays.asList(one, two), args[0]);
            effects.add(name);
            return null;
        }));
        offer(listener, "RerollUsingAssetListener");
        assertTrue(effects.contains("disableCard"));
        assertTrue(effects.contains("rerollDice"));
        offered.setHideType(HideType.DISCARD_ON_YES);
        assertFalse(offered.isAutoAcceptReroll());
    }

    @Test public void gamblersDiceDoesNotOfferMatchingSuccessesOrUnmatchedFailures() throws Exception {
        Object listener = asset("GamblersDice");
        input = data(Stat.STRENGTH, die(2, DiceRoll.Score.GOOD), die(2, DiceRoll.Score.BAD));
        offer(listener, "RerollUsingAssetListener");
        assertNull(offered);
        input = data(Stat.STRENGTH, die(2, DiceRoll.Score.BAD), die(3, DiceRoll.Score.BAD));
        offer(listener, "RerollUsingAssetListener");
        assertNull(offered);
    }

    @Test public void freePromptDoesNotAutomaticallyRerollAnExistingSuccess() throws Exception {
        input = data(Stat.INFLUENCE, die(2, DiceRoll.Score.GOOD), die(6, DiceRoll.Score.BAD));
        offer(asset("PersonalAssistant"), "RerollUsingAssetsListener");
        assertNotNull(offered);
        assertFalse(offered.isAutoAcceptFreeReroll());
    }

    @Test public void completedTestsAndAllSuccessfulDiceOfferNothing() throws Exception {
        Object listener = asset("PersonalAssistant");
        input = data(Stat.INFLUENCE, die(2, DiceRoll.Score.BAD), die(5, DiceRoll.Score.GOOD));
        input.setMinScoreToEndTest(1);
        offer(listener, "RerollUsingAssetsListener");
        assertNull(offered);
        input = data(Stat.INFLUENCE, die(5, DiceRoll.Score.GOOD));
        offer(listener, "RerollUsingAssetsListener");
        assertNull(offered);
    }

    @Test public void numericSelectionProtectsSuccessesAndDoesNotReorderDice() {
        DiceRoll high = die(6, DiceRoll.Score.VERY_GOOD), low = die(1, DiceRoll.Score.BAD);
        TestData data = data(Stat.STRENGTH, high, low, die(3, DiceRoll.Score.BAD));
        data.setScoreBonus(4);
        assertTrue(data.areLowestDiceFailures(1));
        assertTrue(data.areLowestDiceFailures(2));
        assertFalse(data.areLowestDiceFailures(3));
        assertFalse(data.areLowestDiceFailures(0));
        assertFalse(data.areLowestDiceFailures(4));
        assertSame(high, data.getDiceRolls().get(0));
        assertFalse(new TestData().areLowestDiceFailures(1));
    }

    @Test @SuppressWarnings("unchecked") public void spellBackRerollsOnlyOptInAfterTheirEarlierEffects() throws Exception {
        ServicePlatform platform = ServicePlatform.get();
        input = data(Stat.STRENGTH, die(2, DiceRoll.Score.BAD));
        List<ShowCardRequest> prompts = new ArrayList<>();
        platform.setService(mock(Service.class, (name, args) -> {
            if (name.equals("addEventCommand")) ((java8.features.function.Consumer<TestData>) args[0]).accept(input);
            return null;
        }));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("showCard", name);
            prompts.add((ShowCardRequest) args[0]);
            return Single.just(ShowCardResponse.NO);
        }));
        platform.setEncounterService(mock(EncounterService.class, (name, args) ->
                name.equals("displayButtons") ? Single.just(0) : null));
        Object wither = new sk.sivak.eldritchhorror.core.eventlistener.back.spell.wither.WitherSpellBackListener2(
                new sk.sivak.eldritchhorror.core.constants.spell.wither.WitherSpell());
        Method method = wither.getClass().getDeclaredMethod("on2");
        method.setAccessible(true);
        method.invoke(wither);
        assertEquals(1, prompts.size());
        assertTrue(prompts.get(0).isAutoAcceptFreeReroll());
        // Intervene's compulsory Sanity loss happens before this reroll listener is inserted.
        Object intervene = new sk.sivak.eldritchhorror.core.eventlistener.back.spell.intervene.InterveneSpellBackListener2(
                new sk.sivak.eldritchhorror.core.constants.spell.intervene.InterveneSpell());
        method = intervene.getClass().getDeclaredMethod("addListener");
        method.setAccessible(true);
        method.invoke(intervene);
        assertEquals(3, prompts.size());
        for (ShowCardRequest prompt : prompts) assertTrue(prompt.isAutoAcceptFreeReroll());
    }

    @Test public void exConvictOptsInOnlyTheYesOptionAndNeverPays() throws Exception {
        ServicePlatform platform = ServicePlatform.get();
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> InvestigatorId.THE_EX_CONVICT));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("ask", name);
            sk.sivak.eldritchhorror.core.constants.question.Question<?> question =
                    (sk.sivak.eldritchhorror.core.constants.question.Question<?>) args[0];
            assertFalse(question.getOptions().get(0).isAutoAcceptFreeReroll());
            assertTrue(question.getOptions().get(1).isAutoAcceptFreeReroll());
            effects.add("ask");
            return Single.create(subscriber -> { });
        }));
        Object owner = new sk.sivak.eldritchhorror.core.eventlistener.investigator.TheExConvictInitListener();
        Class<?> nested = Class.forName(owner.getClass().getName() + "$TheExConvictPassiveListener");
        Constructor<?> constructor = nested.getDeclaredConstructor(owner.getClass());
        constructor.setAccessible(true);
        Method method = nested.getDeclaredMethod("onNotify", TestData.class);
        method.setAccessible(true);
        method.invoke(constructor.newInstance(owner), data(Stat.STRENGTH, die(1, DiceRoll.Score.BAD)));
        assertEquals(Collections.singletonList("ask"), effects);
    }

    private Object asset(String name) throws Exception {
        AbstractAssetListener<?> listener = (AbstractAssetListener<?>) Class.forName(
                "sk.sivak.eldritchhorror.core.eventlistener.asset." + name + "Listener").getDeclaredConstructor().newInstance();
        AssetInfo card = (AssetInfo) Class.forName("sk.sivak.eldritchhorror.core.constants.asset." + name + "Asset")
                .getDeclaredConstructor().newInstance();
        listener.registerWithOwner(InvestigatorId.THE_SPY, card);
        return listener;
    }

    private void offer(Object owner, String nestedName) throws Exception {
        offered = null;
        effects.clear();
        Class<?> nested = Class.forName(owner.getClass().getName() + "$" + nestedName);
        Constructor<?> constructor = nested.getDeclaredConstructor(owner.getClass());
        constructor.setAccessible(true);
        Method method = nested.getDeclaredMethod("getEventAction", TestData.class);
        method.setAccessible(true);
        ((Runnable) method.invoke(constructor.newInstance(owner), input)).run();
    }

    private static TestData data(Stat stat, DiceRoll... dice) {
        TestData data = new TestData();
        data.setStat(stat);
        data.setMinSuccessDiceValue(5);
        data.setMinScoreToEndTest(20);
        data.setCalculatedDicePool(dice.length);
        data.setDiceRolls(Arrays.asList(dice));
        return data;
    }

    private static DiceRoll die(int value, DiceRoll.Score score) {
        DiceRoll die = new DiceRoll(score);
        die.setDiceValue(value);
        return die;
    }

    private static <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
