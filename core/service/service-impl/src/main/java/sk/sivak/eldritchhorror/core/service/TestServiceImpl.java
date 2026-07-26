package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.provider.TestActionProvider;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventtype.data.PrimitiveWrapper;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.*;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

import java.util.List;

/**
 * @author msivak
 */
public class TestServiceImpl extends AbstractCommonService implements TestService {

    public TestServiceImpl() {
    }

    private TestActionProvider testActionProvider;

    private TokenService tokenService;

    private MonsterService monsterService;

    private EncounterService encounterService;

    private Service service;

    private GameService gameService;

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void setMonsterService(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setTestActionProvider(TestActionProvider testActionProvider) {
        this.testActionProvider = testActionProvider;
    }

    @Override
    public Single<TestData> test(Stat stat, int modifier, int minScoreToEndTest) {
        return Single.create((onSub) -> {
            hold();
            test(stat, modifier, minScoreToEndTest, onSub, new TestFlavorRequest(), false);
            release();
        });
    }

    @Override
    public Single<TestData> test(Stat stat, int modifier, int minScoreToEndTest, TestFlavorRequest testFlavorRequest) {
        return test(stat, modifier, minScoreToEndTest, testFlavorRequest, false);
    }


    private Single<TestData> test(Stat stat, int modifier, int minScoreToEndTest, TestFlavorRequest testFlavorRequest, boolean isCombat) {
        return Single.create((onSub) -> {
            hold();
            test(stat, modifier, minScoreToEndTest, onSub, testFlavorRequest, isCombat);
            release();
        });
    }

    @Override
    public Single<Integer> rollDie(RollData rollData) {
        return Single.create((onSub) -> {
            executeHookable(rollData, testActionProvider.rollAndShowDie(), onSub);
        });
    }

    private void test(Stat stat, int modifier, int minScoreToEndTest, SingleSubscriber<? super TestData> onSub, TestFlavorRequest testFlavorRequest, boolean isCombat) {
        TestData testData = createTestData(stat, modifier, minScoreToEndTest);
        testData.setCombat(isCombat);
        setTestFlavor(testFlavorRequest);
        execute(new ActionCommand<>(testActionProvider.testStart(testData), "TEST_START"));
        executeHookable(testActionProvider.readStatAction());
        execute(new ActionCommand<>(testActionProvider.registerUsableAssets(), "REGISTER_USABLE_ASSETS"));
        execute(new ActionCommand<>(testActionProvider.registerBonusDice(), "REGISTER_BONUS_DICE"));
        executeHookable(testActionProvider.confirmTest());
        execute(new ActionCommand<>(testActionProvider.useAssetsInTest(), "USE_ASSETS_IN_TEST"));
        executeHookable(testActionProvider.calculateDicePool());
        executeHookable(testActionProvider.rollDices());
        executeHookable(new PrimitiveWrapper<>(5), testActionProvider.findMinSuccessDiceValue());
        executeCollector(PrimitiveWrapper.class, TestData.class, testData,
                (minSuccess, td) -> td.setMinSuccessDiceValue(((Integer) minSuccess.getValue())));
        executeHookable(testActionProvider.evaluateRolledDices());
        executeHookable(testActionProvider.showRolledDices());
        execute(new ActionCommand<>(testActionProvider.rerollUsingAssets1(), "REROLL_USING_ASSETS_1"));
        execute(new ActionCommand<>(testActionProvider.rerollUsingAssets2(), "REROLL_USING_ASSETS_2"));
        execute(new ActionCommand<>(testActionProvider.rerollUsingAssets3(), "REROLL_USING_ASSETS_3"));
        rerollUsingFocus();
        rerollUsingClue();
        addOneToDieResultEvent();
        execute(new ActionCommand<>(testActionProvider.updateScoreUsingAssets(), "UPDATE_SCORE_USING_ASSETS"));
        unsetTestFlavor();
        executeHookable(testActionProvider.confirmTestResult(), onSub); // LAST STEP
    }

    @Override
    public void unsetTestFlavor() {
        execute(new ActionCommand<>(testActionProvider.unsetTestFlavor(), "UNSETTING_TEST_FLAVOR"));
    }

    @Override
    public void setTestFlavor(TestFlavorRequest testFlavorRequest) {
        execute(new ActionCommand<>(testActionProvider.setTestFlavor(testFlavorRequest), "SETTING_TEST_FLAVOR"));
    }

    private void addOneToDieResultEvent() {
        executeConvertor(TestData.class, AddOneToDieResultData.class, AddOneToDieResultData::new);
        execute(new ActionCommand<>(testActionProvider.addOneToDieResultEvent(), "ADD_ONE_TO_DIE_RESULT_EVENT"));
        executeConvertor(AddOneToDieResultData.class, TestData.class, AddOneToDieResultData::getTestData);
    }

    @Override
    public void addOneToDieResult() {
        executeHookable(testActionProvider.addOneToDieResult());
    }

    private TestData createTestData(Stat stat, int modifier, int minScoreToEndTest) {
        TestData testData = new TestData();
        testData.setModifier(modifier);
        testData.setStat(stat);
        testData.setMinScoreToEndTest(minScoreToEndTest);
        return testData;
    }

    @Override
    public void rerollUsingFocus() {
        this.<TestData>addEventCommand(input -> {
            if (!shouldRerollUsingClueOrFocus(input)) {
                return;
            }
            tokenService.canSpend(0, 1, 0, 0).subscribe(spendData -> {
                if (spendData.hasEnough()) {
                    hold();
                    convertTo(RerollUsingData.class, () -> new RerollUsingData(input));
                    executeHookable(testActionProvider.rerollUsingFocus());
                    executeConvertor(RerollData.class, TestData.class, rerollData -> {
                        if (rerollData.isExecuted()) {
                            rerollUsingFocus();
                        }
                        return rerollData.getTestData();
                    });
                    release();
                } else {
                    convertTo(TestData.class, () -> input);
                }
            });
        });
    }

    @Override
    public void rerollUsingClue() {
        this.<TestData>addEventCommand(input -> {
            if (!shouldRerollUsingClueOrFocus(input)) {
                return;
            }
            tokenService.canSpend(1, 0, 0, 0).subscribe(spendData -> {
                if (spendData.hasEnough()) {
                    hold();
                    convertTo(RerollUsingData.class, () -> new RerollUsingData(input));
                    executeHookable(testActionProvider.rerollUsingClue());
                    executeConvertor(RerollData.class, TestData.class, rerollData -> {
                        if (rerollData.isExecuted()) {
                            rerollUsingClue();
                        }
                        return rerollData.getTestData();
                    });
                    release();
                } else {
                    convertTo(TestData.class, () -> input);
                }
            });
        });
    }

    private boolean shouldRerollUsingClueOrFocus(TestData testData) {
        if (testData.hasReachedMinScoreToEndTest()) {
            return false;
        }
        if (testData.getCalculatedDicePool() <= testData.getScore()) {
            return false;
        }
        return true;
    }

    @Override
    public void findCountRerollDice(CountRerollDiceData.CountRerollDiceType type, TestData testData) {
        findCountRerollDice(1, type, testData);
    }

    @Override
    public void findCountRerollDice(int defaultCount, CountRerollDiceData.CountRerollDiceType type, TestData testData) {
        CountRerollDiceData countRerollDiceData = new CountRerollDiceData();
        countRerollDiceData.setCount(defaultCount);
        countRerollDiceData.setCountRerollDiceType(type);
        countRerollDiceData.setTestData(testData);
        executeHookable(countRerollDiceData, testActionProvider.findCountRerollDice());
    }

    @Override
    public void rerollDie(TestData testData) {
        hold();
        findCountRerollDice(CountRerollDiceData.CountRerollDiceType.OTHER, testData);
        rerollDice();
        release();
    }

    @Override
    public void rerollDice() {
        rerollDice(null);
    }

    @Override
    public void rerollDice(List<DiceRoll> dicesToReroll) {
        executeHookable(testActionProvider.rerollDice(dicesToReroll));
    }

    @Override
    public void combat(MonsterInfo monsterInfo) {
        CombatData combatData = new CombatData();
        combatData.setMonsterInfo(monsterInfo);
        combatData.setDamageTestType(monsterInfo.getDamageTestType());
        combatData.setDamageTestFlavorType(TestFlavorType.COMBAT);
        hold();
        setTestFlavor(new TestFlavorRequest(TestFlavorType.COMBAT, combatData));
        execute(new ActionCommand<>(testActionProvider.showCombatBackground(), "SHOW_COMBAT_BACKGROUND"));
        executeHookable(combatData, testActionProvider.showCombatOverview());
        executeHookable(testActionProvider.showCombatTable());
        if (monsterInfo.getHorror() != null) {
            horrorCheck(monsterInfo, combatData);
        }
        checkIfInvestigatorWasNotDefeatedInCombat();
        if (monsterInfo.getDamage() != null) {
            damageCheck(monsterInfo, combatData);
        }
        encounterService.endOfEncounter(); // END_OF_ENCOUNTER
        executeHookable(testActionProvider.hideCombatTable());
        service.hideBackground();
        addEventCommand(in -> {
            if (combatData.getDamageTestResult() != null) {
                monsterService.dealDamageToMonsterInCombat(monsterInfo, combatData.getDamageTestResult().getScore(), false);
            }
        });
        executeHookable(testActionProvider.endOfCombatEvent(combatData));
        unsetTestFlavor();
        encounterService.insertDefeatSequencePoint();
        addEventCommand(in -> {
            if (!combatData.getMonsterInfo().isAlive()) {
                gameService.askToRateThisGame();
            }
        });
        convertTo(CombatData.class, () -> combatData);
        release();
    }

    private void horrorCheck(MonsterInfo monsterInfo, CombatData combatData) {
        hold();
        execute(new ActionCommand<>(testActionProvider.beforeHorrorTest(), "BEFORE_HORROR_TEST"));
        checkIfInvestigatorWasNotDefeatedInCombat();
        execute(new ActionCommand<>(testActionProvider.highlightHorrorCombat(), "HIGHLIGHT_HORROR_COMBAT"));
        updateMonsterHorror(combatData);
        addEventCommand(in -> {
            int minScoreToEndTest = Math.max(combatData.getActualHorror(), 1);
            test(monsterInfo.getHorrorTestType(), monsterInfo.getHorrorTestModifier(), minScoreToEndTest,
                    new TestFlavorRequest(TestFlavorType.COMBAT, combatData), true).subscribe(testResult -> {
                hold();
                execute(new ActionCommand<>(testActionProvider.destroyHorror(testResult.getDiceRolls()), "DESTROY_HORROR"));
                int sanityLost = Math.max(0, combatData.getActualHorror() - testResult.getScore());
                execute(new ActionCommand<>(testActionProvider.destroySanity(sanityLost), "DESTROY_SANITY"));
                combatData.setSanityLost(sanityLost);
                combatData.setHorrorTestResult(testResult);
                tokenService.loseSanity(sanityLost);
                release();
            });
        });
        addEventCommand(in -> {
            if (combatData.getSanityLost() > 0) {
                hold();
                convertTo(CombatData.class, () -> combatData);
                execute(new ActionCommand<>(testActionProvider.sanityLostInHorrorCheck(), "SANITY_LOST_IN_HORROR_CHECK"));
                release();
            }
        });

        checkIfInvestigatorWasNotDefeatedInCombat();
        executeHookable(combatData, testActionProvider.afterHorrorCheck());
        release();
    }

    private void damageCheck(MonsterInfo monsterInfo, CombatData combatData) {
        hold();
        execute(new ActionCommand<>(testActionProvider.beforeDamageTest(), "BEFORE_DAMAGE_TEST"));
        checkIfInvestigatorWasNotDefeatedInCombat();
        execute(new ActionCommand<>(testActionProvider.highlightDamageCombat(), "HIGHLIGHT_DAMAGE_COMBAT"));
        updateMonsterDamage(combatData);
        addEventCommand(in -> {
            int minScoreToEndTest = Math.max(monsterInfo.getCurrentHealth(), combatData.getActualDamage());
            test(combatData.getDamageTestType(), monsterInfo.getDamageTestModifier(), minScoreToEndTest,
                    new TestFlavorRequest(combatData.getDamageTestFlavorType(), combatData), true).subscribe(testResult -> {
                hold();
                addEventCommand(input -> {
                    combatData.setDamageTestResult(testResult);
                });
                convertTo(CombatData.class, () -> combatData);
                execute(new ActionCommand<>(testActionProvider.justAfterDamageTest(), "JUST_AFTER_DAMAGE_TEST"));
                addEventCommand(input -> {
                    int healthLost = Math.max(0, combatData.getActualDamage() - testResult.getScore());
                    combatData.setHealthLost(healthLost);
                    combatData.setDamageDealt(testResult.getScore());
                });
                execute(new ActionCommand<>(testActionProvider.destroyDamage(testResult.getDiceRolls()), "DESTROY_DAMAGE"));
                convertTo(CombatData.class, () -> combatData);
                executeHookable(testActionProvider.destroyHealth());
                this.<CombatData>addEventCommand(cd -> { // flesh ward triggers here
                    tokenService.loseHealth(cd.getHealthLost());
                });

                convertTo(CombatData.class, () -> combatData);
                // ghoul triggers here
                destroyMonsterHealth(testResult.getDiceRolls());
                release();

            });
        });
        checkIfInvestigatorWasNotDefeatedInCombat();
        executeHookable(combatData, testActionProvider.afterDamageCheck());
        release();
    }

    private void checkIfInvestigatorWasNotDefeatedInCombat() {
        execute(new ActionCommand<>(testActionProvider.checkIfInvestigatorWasNotDefeatedInCombat(), "CHECK_IF_INVESTIGATOR_WAS_NOT_DEFEATED_IN_COMBAT"));
    }

    @Override
    public void destroyMonsterHealth(List<DiceRoll> diceRolls) {
        executeHookable(testActionProvider.destroyMonsterHealth(diceRolls));
    }

    @Override
    public void updateMonsterDamage(CombatData combatData) {
        executeHookable(combatData, testActionProvider.updateMonsterDamage());
    }

    @Override
    public void updateMonsterHorror(CombatData combatData) {
        executeHookable(combatData, testActionProvider.updateMonsterHorror());
    }


}
