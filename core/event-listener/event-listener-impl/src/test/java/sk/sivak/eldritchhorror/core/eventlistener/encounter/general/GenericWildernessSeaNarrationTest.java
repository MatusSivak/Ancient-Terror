package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class GenericWildernessSeaNarrationTest {
    private static final int[] WILDERNESS_TESTS = {4, 6, 7, 9, 10, 12, 13, 14, 16};
    private static final int[] SEA_TESTS = {1, 3, 4, 8, 9, 10, 11, 13, 15};

    private final Properties text = new Properties();
    private final List<String> flavors = new ArrayList<>();
    private final List<AssetInfo> inventory = new ArrayList<>();
    private final Deque<Integer> answers = new ArrayDeque<>();
    private boolean passed;
    private boolean delayed;
    private boolean madness;
    private boolean clue = true;
    private int statBonus;
    private int finishes;

    @Before public void setup() throws Exception {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/encounter/general.properties"), StandardCharsets.UTF_8)) {
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
        platform.setInvestigators(stub(InvestigatorsRead.class));
        platform.setAssetDeck(stub(AssetDeckRead.class));
        platform.setArtifactsDeck(stub(ArtifactsDeckRead.class));
        platform.setConditionsDeck(stub(ConditionsDeckRead.class));
        platform.setLocationMap(stub(LocationMapRead.class));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void everyTestOutcomeNarratesBeforeThePaperCloses() throws Exception {
        inventory.add(new AxeAsset());
        inventory.add(new LuckyCigaretteCaseAsset());
        for (int page : WILDERNESS_TESTS) {
            play("wilderness", page, true, "pass.flavor");
            play("wilderness", page, false, "fail.flavor");
        }
        for (int page : SEA_TESTS) {
            play("sea", page, true, "pass.flavor");
            play("sea", page, false, "fail.flavor");
        }
        play("wilderness", 15, true, "pass.flavor");
        play("wilderness", 15, false, "fail.flavor");
        play("sea", 12, true, "pass.flavor");
        play("sea", 12, false, "fail.flavor");
        play("sea", 14, true, "pass.flavor");
        play("sea", 14, false, "fail.flavor");
    }

    @Test public void encountersWithoutTestNarrateTheirScene() throws Exception {
        play("wilderness", 1, true);
        play("sea", 2, true);
        play("sea", 7, true);
    }

    @Test public void rewardChoiceNarratesTheSelectedReward() throws Exception {
        for (String encounter : new String[]{"wilderness:2", "wilderness:3", "wilderness:8", "sea:6"}) {
            String kind = encounter.split(":")[0];
            int page = Integer.parseInt(encounter.split(":")[1]);
            play(kind, page, false, "fail.flavor");
            for (int option = 0; option < 2; option++) {
                answers.add(option);
                play(kind, page, true, "pass.flavor", "pass.option." + (option + 1) + ".flavor");
            }
        }
        statBonus = 2;
        play("wilderness", 2, true, "pass.flavor", "pass.option.1.flavor");
        play("wilderness", 8, true, "pass.flavor", "pass.option.1.flavor");
        play("sea", 6, true, "pass.flavor", "pass.option.1.flavor");
        answers.add(1);
        play("wilderness", 3, true, "pass.flavor", "pass.option.2.flavor");
    }

    @Test public void everyQuestionAnswerIsNarrated() throws Exception {
        for (String encounter : new String[]{"wilderness:5", "wilderness:11", "sea:5", "sea:16"}) {
            String kind = encounter.split(":")[0];
            int page = Integer.parseInt(encounter.split(":")[1]);
            answers.add(0);
            play(kind, page, true, "fail.flavor");
        }
        answers.add(1);
        play("wilderness", 5, true, "pass.flavor");
        clue = false;
        answers.add(1);
        play("wilderness", 5, true, "pass.flavor.2");

        answers.add(1);
        play("wilderness", 11, true, "pass.flavor", "option.1.flavor");
        madness = true;
        for (int option = 0; option < 2; option++) {
            answers.add(1);
            answers.add(option);
            play("wilderness", 11, true, "pass.flavor", "option." + (option + 1) + ".flavor");
        }

        answers.add(1);
        play("sea", 5, true, "pass.flavor");
        answers.add(1);
        play("sea", 16, true, "pass.flavor");

        delayed = true;
        answers.add(1);
        play("wilderness", 11, true, "pass.flavor.2");
        answers.add(1);
        play("sea", 5, true, "pass.flavor.2");
        answers.add(1);
        play("sea", 16, true, "pass.flavor.2");
    }

    @Test public void lossNarrationMatchesThePossessionsAtRisk() throws Exception {
        play("wilderness", 15, false, "fail.flavor.2");
        play("sea", 12, false, "fail.flavor.2");
        play("sea", 14, false, "fail.flavor.3");
        inventory.add(new LuckyCigaretteCaseAsset());
        play("wilderness", 15, false, "fail.flavor");
        play("sea", 12, false, "fail.flavor.2");
        play("sea", 14, false, "fail.flavor.2");
        inventory.add(new AxeAsset());
        play("sea", 12, false, "fail.flavor");
        play("sea", 14, false, "fail.flavor");
    }

    private void play(String kind, int page, boolean success, String... endings) throws Exception {
        flavors.clear();
        finishes = 0;
        passed = success;
        String type = kind.substring(0, 1).toUpperCase() + kind.substring(1);
        String name = getClass().getPackage().getName() + "." + type + page + "Encounter";
        ((AbstractGeneralEncounter) Class.forName(name).getConstructor().newInstance()).executeWhole();
        String prefix = page + "." + kind + ".";
        List<String> expected = new ArrayList<>();
        expected.add(text.getProperty(prefix + "flavor"));
        for (String ending : endings) expected.add(text.getProperty(prefix + ending));
        for (String line : expected) assertTrue(prefix, line != null && !line.trim().isEmpty());
        assertEquals(prefix + Arrays.toString(endings), expected, flavors);
        assertEquals(prefix, 1, finishes);
        assertTrue(prefix + " unused answers", answers.isEmpty());
    }

    private <T> T stub(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "typeFlavor":
                    assertEquals("Narration after the encounter closed", 0, finishes);
                    assertNotNull("Missing flavor text", args[0]);
                    flavors.add(((String) args[0]).trim());
                    return null;
                case "typeInfo":
                    assertNotNull("Missing info text", args[0]);
                    return null;
                case "displayButtons":
                    for (Object button : (Object[]) args[0]) assertNotNull("Missing button text", button);
                    return Single.just(((Object[]) args[0]).length == 2 ? answers.remove() : 0);
                case "test":
                    TestData result = new TestData();
                    result.setDiceRolls(Collections.emptyList());
                    result.setMinScoreToBeSuccessful(1);
                    result.setScoreBonus(passed ? 1 : 0);
                    return Single.just(result);
                case "spend":
                    SpendData spendData = new SpendData();
                    spendData.setHasEnough(clue);
                    return Single.just(spendData);
                case "getActiveInvestigatorId": return InvestigatorId.values()[0];
                case "getActiveInvestigator": return stub(InvestigatorRead.class);
                case "getStatBonus": return statBonus;
                case "isDelayed": return delayed;
                case "getCurrentLocationId": return null;
                case "getLocationInfo": return stub(LocationInfo.class);
                case "getShipConnections": return Collections.singletonList(stub(LocationInfo.Connection.class));
                case "hasTrait": return madness;
                case "getCondition": return Collections.singletonList(stub(ConditionInfo.class));
                case "getAssets": return inventory;
                case "getArtifacts": return Collections.emptyList();
                case "findFirst": return new AxeAsset();
                case "selectSingleCard": return Single.just(((SelectCardData) args[0]).getAvailableCards().get(0));
                case "spawnClues": return Single.just(Collections.emptyList());
                case "showCard": return Single.just(null);
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
