package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.test.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.TestFlavorRead;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData.CountRerollDiceType.OTHER;

public class AxeListenerTest {
    private final List<String> effects = new ArrayList<>();
    private AxeListener axe;
    private TestData input;
    private ShowCardResponse response = ShowCardResponse.YES;
    private int offers;

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        platform.setEventQueue(new EventQueueImpl());
        platform.setTestFlavor(mock(TestFlavorRead.class, (name, args) -> TestFlavorType.COMBAT));
        platform.setService(mock(Service.class, (name, args) -> { effects.add(name); return null; }));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("Only the Sanity confirmation may be shown", "showCard", name);
            offers++;
            return Single.just(response);
        }));
        platform.setTokenService(mock(TokenService.class, (name, args) -> {
            assertTrue(name.equals("canSpend") || name.equals("spend"));
            assertArrayEquals(new Object[]{0, 0, 0, 2}, args);
            SpendData spend = new SpendData();
            spend.setHasEnough(true);
            spend.setPayAction(() -> effects.add("pay"));
            return Single.just(spend);
        }));
        platform.setTestService(mock(TestService.class, (name, args) -> {
            if (name.equals("findCountRerollDice")) {
                assertEquals(OTHER, args[1]);
                assertSame(input, args[2]);
                effects.add("count:" + args[0]);
            } else {
                assertEquals("rerollDice", name);
                effects.add(name);
            }
            return null;
        }));
        axe = new AxeListener();
        axe.registerWithOwner(InvestigatorId.THE_SPY, new AxeAsset());
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void rerollsAllFailedDiceAndPaysOnce() {
        reroll(DiceRoll.Score.GOOD, DiceRoll.Score.BAD, DiceRoll.Score.BAD, DiceRoll.Score.BAD);
        assertEquals(Arrays.asList("hold", "pay", "count:3", "rerollDice", "release"), effects);
        assertEquals(1, offers);
    }

    @Test public void doubleSuccessAndBonusDoNotHideAFailedDie() {
        input = testData(DiceRoll.Score.VERY_GOOD, DiceRoll.Score.BAD);
        input.setScoreBonus(2);
        run();
        assertEquals(Arrays.asList("hold", "pay", "count:1", "rerollDice", "release"), effects);
    }

    @Test public void allSuccessfulDiceDoNotOfferOrSpendSanity() {
        reroll(DiceRoll.Score.GOOD, DiceRoll.Score.VERY_GOOD);
        assertEquals(0, offers);
        assertTrue(effects.isEmpty());
    }

    @Test public void decliningDoesNotSpendOrReroll() {
        response = ShowCardResponse.NO;
        reroll(DiceRoll.Score.BAD, DiceRoll.Score.BAD);
        assertEquals(Collections.singletonList("convertTo"), effects);
    }

    private void reroll(DiceRoll.Score... scores) { input = testData(scores); run(); }

    @SuppressWarnings("unchecked")
    private void run() {
        ((AbstractAssetEventListener<TestData>) axe.getEventListeners().get(1)).getEventAction(input).run();
    }

    private TestData testData(DiceRoll.Score... scores) {
        TestData data = new TestData();
        data.setStat(Stat.STRENGTH);
        data.setMinScoreToEndTest(10);
        data.setCalculatedDicePool(scores.length);
        List<DiceRoll> dice = new ArrayList<>();
        for (DiceRoll.Score score : scores) dice.add(new DiceRoll(score));
        data.setDiceRolls(dice);
        return data;
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
