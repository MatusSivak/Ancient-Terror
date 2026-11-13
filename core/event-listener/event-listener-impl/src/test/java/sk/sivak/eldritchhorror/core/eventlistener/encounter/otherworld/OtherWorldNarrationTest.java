package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.asset.PrivateInvestigatorAsset;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class OtherWorldNarrationTest {
    private static final int[] PLAIN_TWO_TESTS = {8, 13, 17, 26, 29, 30};
    private static final String[] BOTH_TESTS = {"PP", "PF", "FP", "FF"};

    private final Properties text = new Properties();
    private final List<String> flavors = new ArrayList<>();
    private final List<AssetInfo> inventory = new ArrayList<>();
    private final Deque<Integer> answers = new ArrayDeque<>();
    private final Deque<Boolean> outcomes = new ArrayDeque<>();
    private boolean delayed;
    private int delays;
    private int rewards;
    private boolean canPay = true;
    private boolean paid = true;
    private int finishes;

    @Before public void setup() throws Exception {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/encounter/otherworld.properties"), StandardCharsets.UTF_8)) {
            text.load(reader);
        }
        ServicePlatform platform = ServicePlatform.get();
        platform.setService(stub(Service.class));
        platform.setEncounterService(stub(EncounterService.class));
        platform.setTestService(stub(TestService.class));
        platform.setGameService(stub(GameService.class));
        platform.setBasicActionService(stub(BasicActionService.class));
        platform.setTokenService(stub(TokenService.class));
        platform.setInvestigatorService(stub(InvestigatorService.class));
        platform.setCardService(stub(CardService.class));
        platform.setDoomOmenService(stub(DoomOmenService.class));
        platform.setInvestigators(stub(InvestigatorsRead.class));
        platform.setAssetDeck(stub(AssetDeckRead.class));
        platform.setArtifactsDeck(stub(ArtifactsDeckRead.class));
        platform.setConditionsDeck(stub(ConditionsDeckRead.class));
        platform.setLocationMap(stub(LocationMapRead.class));
        platform.setCluePoolRead(stub(CluePoolRead.class));
        inventory.add(new AxeAsset());
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void nestedTestsNarrateEveryOutcome() throws Exception {
        for (int page : PLAIN_TWO_TESTS) {
            for (String tests : BOTH_TESTS) play(page, tests, outcomeEndings(tests, false, false));
        }
        for (int page : new int[]{2, 20, 23}) {
            for (String tests : BOTH_TESTS) play(page, tests, outcomeEndings(tests, true, page != 2));
        }
        for (int page : new int[]{22, 25, 27}) {
            play(page, "FP", outcomeEndings("FP", false, page == 27));
            play(page, "FF", outcomeEndings("FF", false, page == 27));
        }
        for (int page : new int[]{7, 22, 27}) {
            play(page, "PP", outcomeEndings("PP", page == 22, false));
            play(page, "PF", outcomeEndings("PF", page == 22, false));
        }
        play(7, "F", "fail.flavor");
        for (int page : new int[]{9, 10, 11, 12, 16, 18}) {
            play(page, "PP", outcomeEndings("PP", false, false));
            play(page, "PF", outcomeEndings("PF", false, false));
        }
        for (int page : new int[]{3, 19}) {
            play(page, "PP", outcomeEndings("PP", true, false));
            play(page, "PF", outcomeEndings("PF", true, false));
        }
        for (int page : new int[]{4, 5, 6}) {
            play(page, "FP", outcomeEndings("FP", false, false));
            play(page, "FF", outcomeEndings("FF", false, false));
        }
    }

    @Test public void rewardChoiceNarratesTheChosenReward() throws Exception {
        play(1, "PF", "pass.flavor", "pass.flavor.2", "pass.fail.flavor");
        for (int option = 0; option < 2; option++) {
            answers.add(option);
            play(1, "PP", "pass.flavor", "pass.flavor.2", "pass.pass.flavor", "pass.pass.option." + (option + 1) + ".flavor");
        }
    }

    @Test public void unlessSpendNarratesBothChoicesAndMissingTokens() throws Exception {
        for (int page : new int[]{1, 3, 9, 10, 11}) {
            answers.add(0);
            play(page, "F", "fail.flavor", "fail.option.1.flavor");
            answers.add(1);
            play(page, "F", "fail.flavor", "fail.option.2.flavor");
        }
        for (int page : new int[]{21, 24}) {
            answers.add(0);
            play(page, "P", "pass.flavor", "pass.flavor.2", "pass.option.1.flavor");
            answers.add(1);
            play(page, "P", "pass.flavor", "pass.flavor.2", "pass.option.2.flavor");
        }
        paid = false;
        for (int page : new int[]{1, 3, 9, 10, 11, 21, 24}) {
            boolean pass = page == 21 || page == 24;
            String[] fallback = pass ? new String[]{"pass.flavor", "pass.flavor.2", "pass.option.1.flavor.2"}
                    : new String[]{"fail.flavor", "fail.option.1.flavor.2"};
            canPay = true;
            answers.add(1);
            play(page, pass ? "P" : "F", fallback);
            canPay = false;
            play(page, pass ? "P" : "F", fallback);
        }
    }

    @Test public void delayedChoiceNarratesBothOptions() throws Exception {
        for (int page : new int[]{12, 19}) {
            delayed = false;
            for (int option = 0; option < 2; option++) {
                answers.add(option);
                play(page, "F", "fail.flavor", "fail.option." + (option + 1) + ".flavor");
            }
            play(page, "F", "fail.flavor", "fail.option.1.flavor.2");
        }
    }

    @Test public void becomeDelayedForRewardNarratesEveryAnswer() throws Exception {
        for (int page : new int[]{4, 5, 6, 25}) {
            delayed = false;
            delays = rewards = 0;
            answers.add(0);
            play(page, "P", "pass.flavor", "pass.flavor.2", "pass.fail.flavor");
            assertEquals(0, rewards);
            answers.add(1);
            play(page, "P", "pass.flavor", "pass.flavor.2", "pass.pass.flavor");
            assertEquals(1, delays);
            assertEquals(page == 4 || page == 5 ? 2 : 1, rewards);
        }
        delays = rewards = 0;
        delayed = true;
        for (int page : new int[]{4, 5, 6, 25}) {
            answers.add(1);
            play(page, "P", "pass.flavor", "pass.flavor.2", "pass.pass.flavor.2");
            assertEquals(0, delays);
            assertEquals(0, rewards);
        }
    }

    @Test public void spendClueQuestionsNarrateEveryAnswer() throws Exception {
        inventory.add(new PrivateInvestigatorAsset());
        for (int page : new int[]{14, 15, 28}) {
            String flavor2 = page == 28 ? "pass.flavor.2" : null;
            for (String test : new String[]{"P", "F"}) {
                String second = test.equals("P") ? "pass" : "fail";
                answers.add(1);
                play(page, test, "pass.flavor", flavor2, "pass." + second + ".flavor");
                answers.add(0);
                play(page, test, "fail.flavor", "fail." + second + ".flavor");
            }
        }
        inventory.clear();
        paid = false;
        for (int page : new int[]{14, 15, 28}) {
            canPay = true;
            answers.add(1);
            play(page, "P", "fail.flavor.2", "fail.pass.flavor");
            canPay = false;
            play(page, "F", "fail.flavor.2", page == 28 ? "fail.fail.flavor.2" : "fail.fail.flavor");
        }
    }

    @Test public void lastChanceQuestionsNarrateEveryAnswer() throws Exception {
        answers.add(0);
        play(16, "F", "fail.flavor", "fail.fail.flavor");
        answers.add(1);
        play(16, "F", "fail.flavor", "fail.pass.flavor");
        answers.add(0);
        play(18, "F", "fail.flavor", "fail.fail.flavor");
        answers.add(1);
        play(18, "F", "fail.flavor", "fail.pass.flavor");
        paid = false;
        answers.add(1);
        play(16, "F", "fail.flavor", "fail.fail.flavor.2");
        inventory.clear();
        answers.add(1);
        play(18, "F", "fail.flavor", "fail.fail.flavor.2");
    }

    @Test public void failedOpeningTestsNarrateEveryOutcome() throws Exception {
        for (int page : new int[]{21, 24}) {
            play(page, "FP", outcomeEndings("FP", false, true));
            play(page, "FF", outcomeEndings("FF", false, true));
        }
        inventory.clear();
        play(21, "FF", "fail.flavor", "fail.flavor.2", "fail.fail.flavor.2");
    }

    @Test public void everyEncounterHasTitleAndFlavor() {
        for (int page = 1; page <= 30; page++) {
            assertNotNull(page + ".title", text.getProperty(page + ".title"));
            assertNotNull(page + ".flavor", text.getProperty(page + ".flavor"));
        }
    }

    private static String[] outcomeEndings(String tests, boolean passFlavor2, boolean failFlavor2) {
        String first = tests.charAt(0) == 'P' ? "pass" : "fail";
        String second = tests.charAt(1) == 'P' ? "pass" : "fail";
        boolean flavor2 = first.equals("pass") ? passFlavor2 : failFlavor2;
        return new String[]{first + ".flavor", flavor2 ? first + ".flavor.2" : null, first + "." + second + ".flavor"};
    }

    private void play(int page, String tests, String... endings) throws Exception {
        flavors.clear();
        finishes = 0;
        outcomes.clear();
        for (char outcome : tests.toCharArray()) outcomes.add(outcome == 'P');
        String name = getClass().getPackage().getName() + ".OtherWorld" + page + "Encounter";
        ((AbstractOtherWorldEncounter) Class.forName(name).getConstructor().newInstance()).executeWhole(GateColor.values()[0]);
        String prefix = page + ".";
        List<String> expected = new ArrayList<>();
        expected.add(text.getProperty(prefix + "flavor"));
        for (String ending : endings) {
            if (ending == null) continue;
            String line = text.getProperty(prefix + ending);
            assertTrue(prefix + ending, line != null && !line.trim().isEmpty());
            expected.add(line.trim());
        }
        String path = prefix + tests + Arrays.toString(endings);
        assertEquals(path, expected, flavors);
        assertEquals(path, 1, finishes);
        assertTrue(path + " unused answers", answers.isEmpty());
        assertTrue(path + " unused test outcomes", outcomes.isEmpty());
    }

    private static void assertText(String what, Object value) {
        assertNotNull("Missing " + what, value);
        assertFalse("Missing " + what + ": " + value, value.toString().endsWith("null"));
    }

    private SpendData spendData(boolean hasEnough) {
        SpendData data = new SpendData();
        data.setHasEnough(hasEnough);
        return data;
    }

    private <T> T stub(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "typeFlavor":
                    assertEquals("Narration after the encounter closed", 0, finishes);
                    assertText("flavor text", args[0]);
                    flavors.add(((String) args[0]).trim());
                    return null;
                case "typeInfo":
                case "typeHeader":
                    assertText("info text", args[0]);
                    return null;
                case "displayButtons":
                    for (Object button : (Object[]) args[0]) assertText("button text", button);
                    return Single.just(((Object[]) args[0]).length == 2 ? answers.remove() : 0);
                case "test":
                    TestData result = new TestData();
                    result.setDiceRolls(Collections.emptyList());
                    result.setMinScoreToBeSuccessful(1);
                    result.setScoreBonus(outcomes.remove() ? 1 : 0);
                    return Single.just(result);
                case "canSpend": return Single.just(spendData(canPay));
                case "spend": return Single.just(spendData(paid));
                case "getActiveInvestigatorId": return InvestigatorId.values()[0];
                case "getActiveInvestigator": return stub(InvestigatorRead.class);
                case "getClueCount": return 0;
                case "getStatBonus": return 0;
                case "isDelayed": return delayed;
                case "becomeDelayed": delays++; delayed = true; return null;
                case "gainClueFromPool":
                case "gainSpell":
                case "improveSkill": rewards++; return null;
                case "getCurrentLocationId": return null;
                case "getCondition": return Collections.singletonList(stub(ConditionInfo.class));
                case "getAssets": return inventory;
                case "getArtifacts": return Collections.emptyList();
                case "findFirst": return new AxeAsset();
                case "selectSingleCard": return Single.just(((SelectCardData) args[0]).getAvailableCards().get(0));
                case "showCard": return Single.just(null);
                case "ask": return Single.just(null);
                case "finishTypewriterPaper": finishes++; return null;
                case "hashCode": return System.identityHashCode(proxy);
                case "equals": return proxy == args[0];
                default:
                    if (method.getReturnType() == void.class) return null;
                    throw new AssertionError(method);
            }
        }));
    }
}
