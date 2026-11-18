package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class CityNarrationTest {
    private final List<String> flavors = new ArrayList<>();
    private final List<String> effects = new ArrayList<>();
    private boolean passed;
    private boolean enough = true;
    private boolean delayed;
    private int choice;
    private int finishes;

    @Before public void setup() {
        ServicePlatform p = ServicePlatform.get();
        p.setService(stub(Service.class)); p.setEncounterService(stub(EncounterService.class));
        p.setTestService(stub(TestService.class)); p.setGameService(stub(GameService.class));
        p.setTokenService(stub(TokenService.class)); p.setInvestigatorService(stub(InvestigatorService.class));
        p.setInvestigators(stub(InvestigatorsRead.class)); p.setMonsterService(stub(MonsterService.class));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void arkhamEveryTestResultAndOfferHasAnEnding() throws Exception {
        for (int page = 1; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("Arkham", "ARKHAM", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
    }

    @Test public void arkhamUnpayableOffersExplainWhyAndGiveNothing() throws Exception {
        choice = 1; enough = false;
        play("Arkham", "ARKHAM", 3, "fail.flavor.2");
        assertTrue(effects.isEmpty());
        enough = true; delayed = true;
        play("Arkham", "ARKHAM", 9, "pass.flavor.2");
        assertFalse(effects.contains("gainSpell"));
    }

    @Test public void arkhamNoEffectTestsKeepOnlyTheirUpfrontRewards() throws Exception {
        passed = true;
        for (int page : new int[]{5, 6}) {
            play("Arkham", "ARKHAM", page, "pass.flavor");
            assertEquals(Collections.singletonList("gainSpell"), effects);
        }
        passed = false;
        for (int page : new int[]{1, 7, 14}) {
            play("Arkham", "ARKHAM", page, "fail.flavor");
            assertTrue(effects.isEmpty());
        }
    }

    private void play(String classCity, String resource, int page, String... endings) throws Exception {
        flavors.clear(); effects.clear(); finishes = 0;
        Properties text = new Properties();
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/encounter/" + resource + ".properties"), StandardCharsets.UTF_8)) {
            text.load(reader);
        }
        ((AbstractLocationEncounter) Class.forName(getClass().getPackage().getName() + "." + classCity + page + "Encounter")
                .getConstructor().newInstance()).executeWhole();
        List<String> expected = new ArrayList<>();
        expected.add(text.getProperty(page + ".flavor"));
        if (text.containsKey(page + ".flavor.2")) expected.add(text.getProperty(page + ".flavor.2"));
        for (String ending : endings) expected.add(text.getProperty(page + "." + ending));
        for (String line : expected) assertTrue(resource + page, line != null && !line.trim().isEmpty());
        assertEquals(resource + page, expected, flavors);
        assertEquals(resource + page, 1, finishes);
    }

    private <T> T stub(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "typeFlavor":
                    assertEquals("Narration after closing", 0, finishes);
                    assertNotNull(args[0]); assertFalse(((String) args[0]).trim().isEmpty());
                    flavors.add(((String) args[0]).trim()); return null;
                case "typeInfo": assertNotNull("Missing mechanical text", args[0]); return null;
                case "displayButtons": return Single.just(((String[]) args[0]).length == 2 ? choice : 0);
                case "test":
                    TestData result = new TestData(); result.setDiceRolls(Collections.emptyList());
                    result.setMinScoreToBeSuccessful(1); result.setScoreBonus(passed ? 1 : 0);
                    return Single.just(result);
                case "spend":
                    SpendData spend = new SpendData(); spend.setHasEnough(enough);
                    spend.setPayAction(() -> effects.add("pay")); return Single.just(spend);
                case "getActiveInvestigatorId": return InvestigatorId.THE_SPY;
                case "getActiveInvestigator": return stub(InvestigatorRead.class);
                case "isDelayed": return delayed;
                case "finishTypewriterPaper": finishes++; return null;
                case "gainSpell": case "gainCondition": case "loseHealth": case "loseSanity":
                case "becomeDelayed": effects.add(method.getName()); return null;
                case "spawnClues": effects.add("spawnClues"); return Single.just(Collections.emptyList());
                case "ambush": effects.add("ambush"); return Single.just(null);
                default:
                    if (method.getReturnType() == void.class) return null;
                    throw new AssertionError(method);
            }
        }));
    }
}
