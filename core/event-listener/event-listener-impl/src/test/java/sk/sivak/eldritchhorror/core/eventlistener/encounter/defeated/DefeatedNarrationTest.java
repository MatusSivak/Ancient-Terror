package sk.sivak.eldritchhorror.core.eventlistener.encounter.defeated;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.CarbineRifleAsset;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.gate.Gate;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DefeatedInvestigatorEncounterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class DefeatedNarrationTest {
    private final Properties text = new Properties();
    private final List<String> flavors = new ArrayList<>();
    private final List<String> rewards = new ArrayList<>();
    private boolean passed;
    private boolean accept;
    private int finishes;
    private boolean gatesAvailable = true;

    @Before public void setup() throws Exception {
        text.load(new InputStreamReader(getClass().getResourceAsStream("/encounter/defeated.properties"), StandardCharsets.UTF_8));
        ServicePlatform p = ServicePlatform.get();
        p.setService(stub(Service.class));
        p.setEncounterService(stub(EncounterService.class));
        p.setTestService(stub(TestService.class));
        p.setGameService(stub(GameService.class));
        p.setDoomOmenService(stub(DoomOmenService.class));
        p.setCardService(stub(CardService.class));
        p.setAssetDeck(stub(AssetDeckRead.class));
        p.setGateStackRead(stub(GateStackRead.class));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void everyDefeatAndTestResultHasAnEndingBeforeClosing() {
        for (InvestigatorId id : InvestigatorId.values()) {
            for (boolean health : new boolean[]{true, false}) {
                for (boolean success : new boolean[]{true, false}) {
                    play(id, health, success, false);
                    String prefix = id.name() + (health ? ".health." : ".sanity.");
                    List<String> expected = new ArrayList<>(Arrays.asList(text.getProperty(prefix + "flavor"),
                            text.getProperty(prefix + "flavor.2"), text.getProperty(prefix + (success ? "pass.flavor" : "fail.flavor"))));
                    if (id == InvestigatorId.THE_REPORTER && !health && success)
                        expected.add(text.getProperty(prefix + "pass.option.2.flavor"));
                    assertEquals(prefix, expected, flavors);
                    assertEquals(prefix, 1, finishes);
                    if (!success) assertTrue(rewards.isEmpty());
                    else assertEquals(prefix, expectedRewards(id, health), rewards);
                }
            }
        }
    }

    @Test public void reporterAcceptanceNarratesAndAppliesOnlyTheAgreedBargain() {
        play(InvestigatorId.THE_REPORTER, false, true, true);
        assertEquals(text.getProperty("THE_REPORTER.sanity.pass.option.1.flavor"), flavors.get(3));
        assertEquals(Arrays.asList("CURSED", "doom", "doom", "doom"), rewards);
        assertEquals(1, finishes);
    }

    @Test public void violinistWithoutAGateHasAnEndingWithoutOpeningAnEmptySelector() {
        gatesAvailable = false;
        play(InvestigatorId.THE_VIOLINIST, false, true, false);
        assertEquals(text.getProperty("THE_VIOLINIST.sanity.pass.flavor.2"), flavors.get(2));
        assertTrue(rewards.isEmpty());
        assertEquals(1, finishes);
    }

    private List<String> expectedRewards(InvestigatorId id, boolean health) {
        if (!health) {
            if (id == InvestigatorId.THE_REPORTER) return Collections.emptyList();
            if (id == InvestigatorId.THE_VIOLINIST) return Collections.singletonList("gate");
            if (id == InvestigatorId.THE_WAITRESS) return Arrays.asList("spell", "spell");
            if (id == InvestigatorId.THE_ROOKIE_COP) return Collections.singletonList("asset");
            if (id == InvestigatorId.THE_EXPLORER) return Collections.singletonList("artifact");
            if (id == InvestigatorId.THE_SALESMAN) return Collections.singletonList("reserve");
        } else {
            if (id == InvestigatorId.THE_PRIEST) return Collections.singletonList("BLESSED");
            if (id == InvestigatorId.THE_DILETTANTE) return Collections.singletonList("reserve");
        }
        return Collections.singletonList("doom");
    }

    private void play(InvestigatorId id, boolean health, boolean success, boolean bargain) {
        flavors.clear(); rewards.clear(); finishes = 0; passed = success; accept = bargain;
        DefeatedInvestigatorEncounterData data = new DefeatedInvestigatorEncounterData();
        data.setInvestigatorId(id); data.setHealth(health);
        data.setCrippledTestStat(Stat.INFLUENCE); data.setInsaneTestStat(Stat.WILL);
        data.setTransferItemsAction(() -> {});
        new DefeatedInvestigatorEncounterTemplate(data).execute();
    }

    private <T> T stub(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "typeFlavor":
                    assertEquals("Narration after closing", 0, finishes);
                    assertNotNull(args[0]); assertFalse(((String) args[0]).trim().isEmpty());
                    flavors.add((String) args[0]); return null;
                case "typeInfo": assertNotNull(args[0]); return null;
                case "displayButtons": return Single.just(((String[]) args[0]).length == 2 && accept ? 1 : 0);
                case "test":
                    TestData result = new TestData(); result.setDiceRolls(Collections.emptyList());
                    result.setMinScoreToBeSuccessful(1); result.setScoreBonus(passed ? 1 : 0);
                    return Single.just(result);
                case "finishTypewriterPaper": finishes++; return null;
                case "retreatDoom": rewards.add("doom"); return null;
                case "gainSpell": rewards.add("spell"); return null;
                case "gainArtifact": rewards.add("artifact"); return null;
                case "gainAsset": rewards.add("asset"); return null;
                case "acquireAssets": assertEquals(99, args[0]); rewards.add("reserve"); return null;
                case "gainCondition": rewards.add(((ConditionId) args[0]).name()); return null;
                case "closeGate": rewards.add("gate"); return null;
                case "find": return new CarbineRifleAsset();
                case "getSpawnedGates": return gatesAvailable ? Collections.singletonList(new Gate(null, LocationId.ARKHAM)) : Collections.emptyList();
                case "selectSingleGate": assertTrue("Empty gate selector", gatesAvailable); return Single.just(new Gate(null, LocationId.ARKHAM));
                default:
                    if (method.getReturnType() == void.class) return null;
                    throw new AssertionError(method);
            }
        }));
    }
}
