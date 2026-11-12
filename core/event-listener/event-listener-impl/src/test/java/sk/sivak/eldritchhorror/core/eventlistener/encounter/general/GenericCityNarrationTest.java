package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class GenericCityNarrationTest {
    private final Properties text = new Properties();
    private final List<String> flavors = new ArrayList<>();
    private final List<AssetInfo> inventory = new ArrayList<>();
    private List<AssetInfo> reserve = Collections.emptyList();
    private boolean passed;
    private int rewardButton;
    private int finishes;
    private int rewards;
    private int refills;

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
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void everySkillOutcomeNarratesBeforeThePaperCloses() throws Exception {
        inventory.add(new AxeAsset());
        inventory.add(new LuckyCigaretteCaseAsset());
        for (int page = 1; page <= 16; page++) {
            play(page, false, "fail.flavor");
            if (page == 1 || page == 3 || page == 12) {
                play(page, true, "pass.flavor", "pass.option.2.flavor");
            } else {
                play(page, true, "pass.flavor");
            }
        }
    }

    @Test public void rewardChoiceNarratesTheSelectedSourceAndAwardsOnce() throws Exception {
        for (int page : new int[]{1, 3, 12}) {
            reserve = Arrays.asList(new AxeAsset(), new DeliveryServiceAsset());
            for (rewardButton = 0; rewardButton < 2; rewardButton++) {
                play(page, true, "pass.flavor", "pass.option." + (rewardButton + 1) + ".flavor");
                assertEquals(1, rewards);
                assertEquals(rewardButton == 0 ? 1 : 0, refills);
            }
            reserve = Collections.emptyList();
            play(page, true, "pass.flavor", "pass.option.2.flavor");
            assertEquals(1, rewards);
            assertEquals(0, refills);
        }
    }

    @Test public void theftNarrationMatchesThePossessionsAtRisk() throws Exception {
        play(3, false, "fail.flavor.2");
        play(8, false, "fail.flavor.2");
        play(15, false, "fail.flavor.4");
        inventory.add(new LuckyCigaretteCaseAsset());
        play(15, false, "fail.flavor.3");
        inventory.clear();
        inventory.add(new AxeAsset());
        play(15, false, "fail.flavor.2");
    }

    private void play(int page, boolean success, String... endings) throws Exception {
        flavors.clear();
        finishes = rewards = refills = 0;
        passed = success;
        String name = getClass().getPackage().getName() + ".City" + page + "Encounter";
        ((AbstractGeneralEncounter) Class.forName(name).getConstructor().newInstance()).executeWhole();
        String prefix = page + ".city.";
        List<String> expected = new ArrayList<>();
        expected.add(text.getProperty(prefix + "flavor"));
        if (page == 8) expected.add(text.getProperty(prefix + "flavor.2"));
        for (String ending : endings) expected.add(text.getProperty(prefix + ending));
        for (String line : expected) assertTrue(prefix, line != null && !line.trim().isEmpty());
        assertEquals(prefix, expected, flavors);
        assertEquals(prefix, 1, finishes);
    }

    private <T> T stub(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "typeFlavor":
                    assertEquals("Narration after the encounter closed", 0, finishes);
                    flavors.add(((String) args[0]).trim());
                    return null;
                case "displayButtons":
                    return Single.just(((String[]) args[0]).length == 2 ? rewardButton : 0);
                case "test":
                    TestData result = new TestData();
                    result.setDiceRolls(Collections.emptyList());
                    result.setMinScoreToBeSuccessful(1);
                    result.setScoreBonus(passed ? 1 : 0);
                    return Single.just(result);
                case "getActiveInvestigatorId": return InvestigatorId.values()[0];
                case "getAssets": return inventory;
                case "getArtifacts": return Collections.emptyList();
                case "getReserve": return reserve;
                case "findFirst": return args[0] == AssetTrait.SERVICE ? new DeliveryServiceAsset() : new AxeAsset();
                case "selectSingleCard": return Single.just(((SelectCardData) args[0]).getAvailableCards().get(0));
                case "spawnClues": return Single.just(Collections.emptyList());
                case "showCard": return Single.just(null);
                case "finishTypewriterPaper": finishes++; return null;
                case "gainAsset": rewards++; return null;
                case "refillReserve": refills++; return null;
                default:
                    if (method.getReturnType() == void.class) return null;
                    throw new AssertionError(method);
            }
        }));
    }
}
