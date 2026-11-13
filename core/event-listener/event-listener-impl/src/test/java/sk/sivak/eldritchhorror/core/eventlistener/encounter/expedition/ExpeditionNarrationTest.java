package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import java8.features.function.Consumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class ExpeditionNarrationTest {
    private static final String[] LOCATIONS = {"Africa", "Pyramids", "Antarctica", "Amazon", "Himalayas", "Tunguska"};
    private static final String[] BOTH_TESTS = {"PP", "PF", "FP", "FF"};
    /** Sides whose first outcome confirms a reward or penalty and then sets up the second test with flavor.2. */
    private static final Set<String> SECOND_FLAVOR = new HashSet<>(Arrays.asList(
            "1.africa.pass", "1.africa.fail", "3.africa.pass", "3.africa.fail", "4.africa.fail",
            "1.pyramids.pass", "1.pyramids.fail", "3.pyramids.pass", "3.pyramids.fail",
            "1.antarctica.pass", "1.antarctica.fail", "3.antarctica.pass", "3.antarctica.fail", "4.antarctica.fail",
            "1.amazon.pass", "1.amazon.fail", "3.amazon.pass", "3.amazon.fail",
            "1.himalayas.pass", "3.himalayas.pass", "4.himalayas.fail",
            "1.tunguska.fail", "3.tunguska.pass", "3.tunguska.fail"));

    private final Properties text = new Properties();
    private final List<String> flavors = new ArrayList<>();
    private final List<String> unexpectedCalls = new ArrayList<>();
    private final List<AssetInfo> inventory = new ArrayList<>();
    private final Deque<Integer> answers = new ArrayDeque<>();
    private final Deque<Boolean> outcomes = new ArrayDeque<>();
    private boolean hasCondition;
    private boolean hasAilment = true;
    private int eventCommands;
    private int finishes;

    @Before public void setup() throws Exception {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/encounter/expedition.properties"), StandardCharsets.UTF_8)) {
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
        platform.setMonsterService(stub(MonsterService.class));
        platform.setInvestigators(stub(InvestigatorsRead.class));
        platform.setAssetDeck(stub(AssetDeckRead.class));
        platform.setArtifactsDeck(stub(ArtifactsDeckRead.class));
        platform.setConditionsDeck(stub(ConditionsDeckRead.class));
        platform.setLocationMap(stub(LocationMapRead.class));
        inventory.add(new AxeAsset());
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void everyTestOutcomeIsNarrated() throws Exception {
        for (String location : LOCATIONS) {
            for (int page = 1; page <= 4; page++) {
                for (String tests : BOTH_TESTS) {
                    if (location.equals("Pyramids") && page == 4 && tests.charAt(0) == 'F') continue;
                    play(location, page, tests, outcomeEndings(location, page, tests));
                }
            }
        }
    }

    @Test public void darkPactOfferNarratesEveryAnswer() throws Exception {
        answers.add(0);
        play("Pyramids", 4, "F", "fail.flavor", "fail.flavor.2", "fail.fail.flavor");
        answers.add(1);
        play("Pyramids", 4, "F", "fail.flavor", "fail.flavor.2", "fail.pass.flavor");
        hasCondition = true;
        answers.add(1);
        play("Pyramids", 4, "F", "fail.flavor", "fail.flavor.2", "fail.pass.flavor.2");
    }

    @Test public void paymentWithoutItemsHasItsOwnNarration() throws Exception {
        inventory.clear();
        play("Amazon", 4, "PF", "pass.flavor", "pass.fail.flavor.2");
        play("Amazon", 4, "PP", outcomeEndings("Amazon", 4, "PP"));
    }

    @Test public void feverBreaksWithoutAConditionToDiscard() throws Exception {
        hasAilment = false;
        play("Amazon", 4, "FP", "fail.flavor", "fail.pass.flavor.2");
    }

    private static String[] outcomeEndings(String location, int page, String tests) {
        String first = tests.charAt(0) == 'P' ? "pass" : "fail";
        String second = tests.charAt(1) == 'P' ? "pass" : "fail";
        boolean flavor2 = SECOND_FLAVOR.contains(page + "." + location.toLowerCase() + "." + first);
        return new String[]{first + ".flavor", flavor2 ? first + ".flavor.2" : null, first + "." + second + ".flavor"};
    }

    private void play(String location, int page, String tests, String... endings) throws Exception {
        flavors.clear();
        finishes = 0;
        eventCommands = 0;
        outcomes.clear();
        for (char outcome : tests.toCharArray()) outcomes.add(outcome == 'P');
        String name = getClass().getPackage().getName() + "." + location + page + "Encounter";
        ((AbstractExpeditionEncounter) Class.forName(name).getConstructor().newInstance()).executeWhole();
        String prefix = page + "." + location.toLowerCase() + ".";
        List<String> expected = new ArrayList<>();
        expected.add(text.getProperty(prefix + "flavor").trim());
        for (String ending : endings) {
            if (ending == null) continue;
            String line = text.getProperty(prefix + ending);
            assertTrue(prefix + ending, line != null && !line.trim().isEmpty());
            expected.add(line.trim());
        }
        String path = prefix + tests + Arrays.toString(endings);
        assertEquals(path + " unexpected calls", Collections.emptyList(), unexpectedCalls);
        assertEquals(path, expected, flavors);
        assertEquals(path, 1, finishes);
        assertTrue(path + " unused answers", answers.isEmpty());
        assertTrue(path + " unused test outcomes", outcomes.isEmpty());
    }

    private static void assertText(String what, Object value) {
        assertNotNull("Missing " + what, value);
        assertFalse("Missing " + what + ": " + value, value.toString().endsWith("null"));
    }

    @SuppressWarnings("unchecked")
    private Object runEventCommand(Object command) {
        MonsterInfo monster = stub(MonsterInfo.class);
        if (eventCommands++ == 0) {
            SpawnMonsterData spawned = new SpawnMonsterData();
            spawned.setMonsterInfo(monster);
            ((Consumer<SpawnMonsterData>) command).accept(spawned);
        } else {
            CombatData combat = new CombatData();
            combat.setMonsterInfo(monster);
            ((Consumer<CombatData>) command).accept(combat);
        }
        return null;
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
                case "addEventCommand": return runEventCommand(args[0]);
                case "isAlive": return !outcomes.remove();
                case "getActiveInvestigatorId": return InvestigatorId.values()[0];
                case "getActiveInvestigator": return stub(InvestigatorRead.class);
                case "getStatBonus": return 0;
                case "isDelayed": return false;
                case "getCurrentLocationId": return null;
                case "hasCondition": return hasCondition;
                case "getName": return "Card";
                case "getCondition": return hasAilment ? Collections.singletonList(stub(ConditionInfo.class)) : Collections.emptyList();
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
                    if (type == MonsterInfo.class && method.getReturnType() == Integer.class) return 1;
                    unexpectedCalls.add(method.toString());
                    throw new AssertionError(method);
            }
        }));
    }
}
