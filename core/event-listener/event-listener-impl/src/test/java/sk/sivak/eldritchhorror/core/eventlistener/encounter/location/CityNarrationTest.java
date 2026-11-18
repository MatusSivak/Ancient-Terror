package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.spell.healingwords.HealingWordsSpell;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.asset.LuckyCigaretteCaseAsset;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
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
    private final List<String> infos = new ArrayList<>();
    private final List<String> effects = new ArrayList<>();
    private boolean passed;
    private boolean enough = true;
    private boolean delayed;
    private int choice;
    private int finishes;
    private boolean targets = true;
    private boolean hasCondition;
    private boolean possessions = true;
    private boolean pendingDelay;
    private boolean epic;
    private int toughness = 1;
    private int monsterDamage;

    @Before public void setup() {
        ServicePlatform p = ServicePlatform.get();
        p.setService(stub(Service.class)); p.setEncounterService(stub(EncounterService.class));
        p.setTestService(stub(TestService.class)); p.setGameService(stub(GameService.class));
        p.setTokenService(stub(TokenService.class)); p.setInvestigatorService(stub(InvestigatorService.class));
        p.setInvestigators(stub(InvestigatorsRead.class)); p.setMonsterService(stub(MonsterService.class));
        p.setBasicActionService(stub(BasicActionService.class)); p.setDoomOmenService(stub(DoomOmenService.class));
        p.setConditionsDeck(stub(ConditionsDeckRead.class)); p.setGateStackRead(stub(GateStackRead.class));
        p.setCluePoolRead(stub(CluePoolRead.class)); p.setMonsterCup(stub(MonsterCupRead.class));
        p.setLocationMap(stub(LocationMapRead.class));
        p.setSpellsDeck(stub(SpellsDeckRead.class)); p.setCardService(stub(CardService.class));
        p.setAssetDeck(stub(AssetDeckRead.class)); p.setArtifactsDeck(stub(ArtifactsDeckRead.class));
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

    @Test public void londonEveryOutcomeAndChoiceHasAnEnding() throws Exception {
        for (int page = 1; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("London", "LONDON", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
    }

    @Test public void londonUnavailableOffersAndTargetsAreNarratedWithoutRewards() throws Exception {
        choice = 1; hasCondition = true;
        play("London", "LONDON", 1, "pass.flavor.2"); assertTrue(effects.isEmpty());
        delayed = true;
        play("London", "LONDON", 8, "pass.flavor.2"); assertFalse(effects.contains("spawnClues"));
        passed = true; targets = false;
        for (int page : new int[]{11, 14, 15}) {
            play("London", "LONDON", page, "pass.flavor.2"); assertTrue(effects.isEmpty());
        }
    }

    @Test public void buenosAiresEveryOutcomeAndChoiceHasAnEnding() throws Exception {
        for (int page = 1; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("BuenosAires", "BUENOS_AIRES", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
    }

    @Test public void buenosAiresMissingCostsAndSpellsHaveTruthfulEndings() throws Exception {
        choice = 1; enough = false;
        play("BuenosAires", "BUENOS_AIRES", 3, "fail.flavor.2"); assertTrue(effects.isEmpty());
        enough = true; delayed = true;
        play("BuenosAires", "BUENOS_AIRES", 7, "pass.flavor.2"); assertFalse(effects.contains("gainSpell"));
        hasCondition = true;
        play("BuenosAires", "BUENOS_AIRES", 16, "fail.flavor.2");
        assertEquals(Collections.singletonList("loseSanity"), effects);
        possessions = false; passed = false;
        play("BuenosAires", "BUENOS_AIRES", 10, "fail.flavor.2"); assertTrue(effects.isEmpty());
        play("BuenosAires", "BUENOS_AIRES", 14, "fail.flavor.2");
        assertEquals(Collections.singletonList("loseSanity"), effects);
        assertEquals(Arrays.asList("[#BAD]Test Failed.[]", "[#BAD]Lose one Sanity[]"), infos);
        possessions = true;
        play("BuenosAires", "BUENOS_AIRES", 14, "fail.flavor");
        assertEquals(Arrays.asList("loseSanity", "discardSpell"), effects);
        assertEquals(Arrays.asList("[#BAD]Test Failed.[]", "-[#BAD]Lose one Sanity[]\n-[#BAD]Discard one Spell[]"), infos);
    }

    @Test public void romeEveryOutcomeAndOfferHasAnEnding() throws Exception {
        for (int page = 1; page <= 16; page++) {
            if (page == 7) continue;
            hasCondition = page == 6;
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("Rome", "ROME", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
        choice = 0; play("Rome", "ROME", 7, "fail.flavor.2"); assertTrue(effects.isEmpty());
        choice = 1;
        for (boolean success : new boolean[]{true, false}) {
            passed = success;
            play("Rome", "ROME", 7, "pass.flavor", success ? "pass.flavor.2" : "fail.flavor");
        }
    }

    @Test public void romeUnavailableCostsAndBlessingHaveTheirOwnEndings() throws Exception {
        choice = 1; delayed = true;
        play("Rome", "ROME", 1, "pass.flavor.2"); assertFalse(effects.contains("gainCondition"));
        possessions = false;
        play("Rome", "ROME", 14, "fail.flavor.2"); assertTrue(effects.isEmpty());
        enough = false;
        play("Rome", "ROME", 15, "fail.flavor.2"); assertTrue(effects.isEmpty());
        hasCondition = false; passed = false;
        play("Rome", "ROME", 6, "fail.flavor.2"); assertEquals(Collections.singletonList("loseSanity"), effects);
    }

    @Test public void istanbulEveryOutcomeAndMissingCostIsNarrated() throws Exception {
        for (int page = 1; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("Istanbul", "ISTANBUL", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
        choice = 1; enough = false;
        for (int page : new int[]{2, 13}) {
            play("Istanbul", "ISTANBUL", page, "fail.flavor.2"); assertTrue(effects.isEmpty());
        }
        delayed = true;
        play("Istanbul", "ISTANBUL", 5, "pass.flavor.2"); assertFalse(effects.contains("improveSkill"));
        possessions = false; passed = false;
        play("Istanbul", "ISTANBUL", 16, "fail.flavor.2"); assertTrue(effects.isEmpty());
    }

    @Test public void shanghaiEveryOutcomeAndUnavailableCostHasAnEnding() throws Exception {
        for (int page = 1; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("Shanghai", "SHANGHAI", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
        choice = 1; enough = false;
        play("Shanghai", "SHANGHAI", 5, "fail.flavor.2"); assertTrue(effects.isEmpty());
        delayed = true;
        for (int page : new int[]{7, 12}) {
            play("Shanghai", "SHANGHAI", page, "pass.flavor.2"); assertFalse(effects.contains("improveSkill"));
        }
        hasCondition = true;
        play("Shanghai", "SHANGHAI", 13, "fail.flavor.2"); assertEquals(Collections.singletonList("gainCondition"), effects);
        possessions = false; passed = false;
        play("Shanghai", "SHANGHAI", 9, "fail.flavor.2"); assertTrue(effects.isEmpty());
        play("Shanghai", "SHANGHAI", 16, "fail.flavor.2");
        assertEquals(Arrays.asList("loseHealth", "gainCondition"), effects);
    }

    @Test public void sanFranciscoEveryOutcomeAndUnavailableOfferIsNarrated() throws Exception {
        for (int page = 1; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("SanFrancisco", "SAN_FRANCISCO", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
        choice = 1; delayed = true;
        play("SanFrancisco", "SAN_FRANCISCO", 4, "pass.flavor.2"); assertFalse(effects.contains("improveSkill"));
        enough = false;
        for (int page : new int[]{5, 10}) {
            play("SanFrancisco", "SAN_FRANCISCO", page, "fail.flavor.2"); assertTrue(effects.isEmpty());
        }
        hasCondition = true;
        play("SanFrancisco", "SAN_FRANCISCO", 11, "pass.flavor.2"); assertTrue(effects.isEmpty());
        hasCondition = false; enough = true;
        play("SanFrancisco", "SAN_FRANCISCO", 10, "pass.flavor");
        assertEquals(Arrays.asList("pay", "travelToLocation"), effects);
    }

    @Test public void sydneyEveryOutcomeAndUnavailableCostHasAnEnding() throws Exception {
        for (int page = 1; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("Sydney", "SYDNEY", page, success ? "pass.flavor" : "fail.flavor");
            }
        }
        choice = 1; enough = false;
        for (int page : new int[]{5, 13}) {
            play("Sydney", "SYDNEY", page, "fail.flavor.2"); assertTrue(effects.isEmpty());
        }
        delayed = true;
        for (int page : new int[]{8, 15}) {
            play("Sydney", "SYDNEY", page, "pass.flavor.2");
            assertFalse(effects.contains("improveSkill")); assertFalse(effects.contains("gainAsset"));
        }
        possessions = false; passed = false;
        play("Sydney", "SYDNEY", 10, "fail.flavor.2"); assertTrue(effects.isEmpty());
    }

    @Test public void tokyoEveryTestAndOfferNarratesAndPreservesDamage() throws Exception {
        passed = true;
        play("Tokyo", "TOKYO", 1, "pass.flavor");
        assertEquals(Arrays.asList("moveMonster", "combat"), effects);
        for (int page = 2; page <= 16; page++) {
            for (boolean success : new boolean[]{true, false}) {
                passed = success; choice = success ? 1 : 0;
                play("Tokyo", "TOKYO", page, success ? "pass.flavor" : "fail.flavor");
                int expectedDamage = !success ? 0 : page == 2 || page == 5 ? 3
                        : Arrays.asList(3, 6, 9, 11, 13, 14, 15).contains(page) ? 2 : 0;
                assertEquals("Tokyo " + page, expectedDamage, monsterDamage);
                if (success && Arrays.asList(7, 12, 16).contains(page)) assertTrue(effects.contains("discardMonster"));
                if (success && page == 8) assertTrue(effects.contains("moveMonster"));
            }
        }
    }

    @Test public void tokyoMissingTargetsAndUnpayableOffersDoNotOpenEmptySelectors() throws Exception {
        passed = true; choice = 1; targets = false;
        for (int page : new int[]{1, 2, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 16}) {
            play("Tokyo", "TOKYO", page, page == 2 ? "pass.flavor.3" : "pass.flavor.2");
            assertEquals(0, monsterDamage);
            assertFalse(effects.contains("moveMonster")); assertFalse(effects.contains("discardMonster"));
            assertFalse(effects.contains("combat"));
            if (page == 2) assertEquals(Collections.singletonList("becomeDelayed"), effects);
            else if (page == 7) assertEquals(Collections.singletonList("pay"), effects);
            else assertTrue(effects.isEmpty());
        }
        targets = true; epic = true;
        for (int page : new int[]{1, 7, 8, 12, 16}) play("Tokyo", "TOKYO", page, "pass.flavor.2");
        epic = false; toughness = 4;
        for (int page : new int[]{12, 16}) play("Tokyo", "TOKYO", page, "pass.flavor.2");
        delayed = true;
        play("Tokyo", "TOKYO", 2, "pass.flavor.2"); assertEquals(0, monsterDamage);
        enough = false;
        play("Tokyo", "TOKYO", 7, "fail.flavor.2"); assertTrue(effects.isEmpty());
    }

    private void play(String classCity, String resource, int page, String... endings) throws Exception {
        flavors.clear(); infos.clear(); effects.clear(); finishes = monsterDamage = 0; pendingDelay = false;
        boolean initiallyDelayed = delayed;
        Properties text = new Properties();
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/encounter/" + resource + ".properties"), StandardCharsets.UTF_8)) {
            text.load(reader);
        }
        ((AbstractLocationEncounter) Class.forName(getClass().getPackage().getName() + "." + classCity + page + "Encounter")
                .getConstructor().newInstance()).executeWhole();
        delayed = initiallyDelayed; pendingDelay = false;
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
                case "typeInfo":
                    assertNotNull("Missing mechanical text", args[0]);
                    assertFalse("Blank mechanical text", ((String) args[0]).trim().isEmpty());
                    infos.add(((String) args[0]).trim()); return null;
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
                case "showTypewriterPaper": if (pendingDelay) { delayed = true; pendingDelay = false; } return null;
                case "finishTypewriterPaper": finishes++; return null;
                case "gainSpell": case "gainCondition": case "loseHealth": case "loseSanity":
                    effects.add(method.getName()); return null;
                case "becomeDelayed": pendingDelay = true; effects.add("becomeDelayed"); return null;
                case "spawnClues": effects.add("spawnClues"); return Single.just(Collections.emptyList());
                case "ambush":
                    effects.add("ambush"); CombatData combat = new CombatData();
                    combat.setMonsterInfo(stub(MonsterInfo.class)); return Single.just(combat);
                case "isAlive": return !passed;
                case "getHorror": case "getDamage": return 1;
                case "getToughness": return toughness;
                case "isEpic": return epic;
                case "getCurrentLocation": return LocationId.LONDON;
                case "getCurrentLocationId": return LocationId.TOKYO;
                case "getMonsters": return targets ? Collections.singletonList(stub(MonsterInfo.class)) : Collections.emptyList();
                case "selectSingleMonster":
                    assertFalse("Empty monster selector", ((SelectMonsterData) args[0]).getAvailableMonsters().isEmpty());
                    return Single.just(((SelectMonsterData) args[0]).getAvailableMonsters().get(0));
                case "moveMonster": case "discardMonster": case "combat": effects.add(method.getName()); return null;
                case "dealDamageToMonster": monsterDamage += (Integer) args[1]; effects.add("dealDamageToMonster"); return null;
                case "getName": return "Attacker";
                case "getSpells": return possessions ? Collections.singletonList(new HealingWordsSpell()) : Collections.emptyList();
                case "getAssets": return possessions ? Arrays.asList(new AxeAsset(), new LuckyCigaretteCaseAsset()) : Collections.emptyList();
                case "getArtifacts": return Collections.emptyList();
                case "findFirst": return new AxeAsset();
                case "gainAsset": effects.add("gainAsset"); return null;
                case "getCondition": return stub(ConditionInfo.class);
                case "selectSingleCard":
                    assertFalse("Empty card selector", ((SelectCardData) args[0]).getAvailableCards().isEmpty());
                    return Single.just(((SelectCardData) args[0]).getAvailableCards().get(0));
                case "showCard": return Single.just(null);
                case "discardSpellFromInvestigator": effects.add("discardSpell"); return null;
                case "discardAssetFromInvestigator": case "discardConditionFromInvestigator":
                case "improveSkill": effects.add(method.getName()); return null;
                case "gainSanity": effects.add("gainSanity"); return null;
                case "travelToLocation": effects.add("travelToLocation"); return null;
                case "hasCondition": return hasCondition;
                case "selectLocation": return Single.just(LocationId.LONDON);
                case "getLocationInfo": return stub(LocationInfo.class);
                case "getLocationType": return LocationType.WILDERNESS;
                case "getClueLocations": return targets ? Collections.singleton(LocationId.LONDON) : Collections.emptySet();
                case "getSpawnedGatesLocations":
                    return targets ? Collections.singletonList(LocationId.LONDON) : Collections.emptyList();
                case "getSpawnedClues": return targets ? new ArrayList<>(Collections.singletonList(stub(ClueInfo.class))) : new ArrayList<>();
                case "getSpawnLocationId": return LocationId.ARKHAM;
                case "containsMonster": return targets;
                case "getMonstersAtLocation": return Collections.singletonList(stub(MonsterInfo.class));
                case "gainClueFromPool": case "spawnClueAt": case "gainTravelTicket":
                case "advanceDoom": case "moveClue": effects.add(method.getName()); return null;
                default:
                    if (method.getReturnType() == void.class) return null;
                    throw new AssertionError(method);
            }
        }));
    }
}
