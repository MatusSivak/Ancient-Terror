package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;

import java.util.Arrays;

/**
 * @author msivak
 */
public class TheAstronomerInitListener extends AbstractInvestigatorInitListener {

    private UseSanityInsteadOfClueListener useSanityInsteadOfClueListener;
    private boolean temporarySupressSpendListener = false;
    private SpendOneLessClueListener spendOneLessClueListener;
    private ReenableDisabledAbility reenableDisabledAbility;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_ASTRONOMER;
    }

    @Override
    protected void initInvestigator() {
        spendOneLessClueListener = new SpendOneLessClueListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(spendOneLessClueListener, BeforeAfterEvent.SPEND);

        useSanityInsteadOfClueListener = new UseSanityInsteadOfClueListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(useSanityInsteadOfClueListener, DirectEvent.REROLL_USING_ASSETS_2);

        reenableDisabledAbility = new ReenableDisabledAbility();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reenableDisabledAbility, DirectEvent.REENABLE_DISABLED_ABILITIES);

        getService().hold();
        getService().gainSpellFromDeck(getInvestigatorId(), SpellId.FEED_THE_MIND);
        getService().convertTo(InvestigatorId.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        spendOneLessClueListener = new SpendOneLessClueListener();
        useSanityInsteadOfClueListener = new UseSanityInsteadOfClueListener();
        reenableDisabledAbility = new ReenableDisabledAbility();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(spendOneLessClueListener, BeforeAfterEvent.SPEND);
        ServicePlatform.get().getEventQueue().addDirectEventListener(useSanityInsteadOfClueListener, DirectEvent.REROLL_USING_ASSETS_2);
        ServicePlatform.get().getEventQueue().addDirectEventListener(reenableDisabledAbility, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(spendOneLessClueListener);
        ServicePlatform.get().getEventQueue().unregisterListener(reenableDisabledAbility);
        ServicePlatform.get().getEventQueue().unregisterListener(useSanityInsteadOfClueListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class ReenableDisabledAbility extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ASTRONOMER).setPassiveAbilityDisabled(false);
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }
    private class UseSanityInsteadOfClueListener extends EventListenerImpl<TestData> {

        public UseSanityInsteadOfClueListener() {
        }

        @Override
        public void onNotify(TestData eventData) {
            ServicePlatform.get().getService().<TestData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        private Runnable getEventAction(TestData testData) {
            return () -> {
                if (ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ASTRONOMER).isPassiveAbilityDisabled()) {
                    return;
                }
                if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_ASTRONOMER) {
                    return;
                }
                if (testData.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (testData.getCalculatedDicePool() <= testData.getScore()) {
                    return;
                }

                ServicePlatform.get().getTokenService().canSpend(0,0,0,1).subscribe(spendData -> {
                    if (spendData.hasEnough()) {
                        Question<Boolean> question = new Question<>();
                        question.setTitle("Reroll using Sanity instead of Clue? (Once per round)");
                        question.setPortraitBeforeTitle(InvestigatorId.THE_ASTRONOMER);
                        question.setOptions(Arrays.asList(
                                new Question.Option<>("No", false, 0x800000ff),
                                new Question.Option<>("Yes", true, 0x008000ff)
                        ));
                        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, testData));
                    } else {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
                    }
                });
            };
        }


        private void onAnswer(Answer<Boolean, Object> answer, TestData testData) {
            if (answer.getResponseData()) {
                ServicePlatform.get().getTokenService().spend(0,0,0,1).subscribe(spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        ServicePlatform.get().getTestService().findCountRerollDice(CountRerollDiceData.CountRerollDiceType.CLUE, testData);
                        ServicePlatform.get().getTestService().rerollDice();
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
                        ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ASTRONOMER).setPassiveAbilityDisabled(true);
                        ServicePlatform.get().getService().release();
                    } else {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
                    }
                });
            } else {
                temporarySupressSpendListener = true;
                ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
            }
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }


    private class SpendOneLessClueListener extends EventListenerImpl<SpendData> {


        @Override
        public void onNotify(SpendData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                getEventAction(eventData).run();
            });
        }

        private Runnable getEventAction(SpendData input) {
            return () -> {
                if (ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ASTRONOMER).isPassiveAbilityDisabled()) {
                    return;
                }
                if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_ASTRONOMER) {
                    return;
                }
                Integer cluesRequired = input.getTokenAmountMap().get(TokenType.CLUE);
                if (cluesRequired <= 0) {
                    return;
                }
                if (temporarySupressSpendListener) {
                    temporarySupressSpendListener = false;
                    return;
                }
                if (input.isSilent()) {
                    onSilent(input, cluesRequired);
                } else {
                    onLoud(input, cluesRequired);
                }

            };
        }

        private void onSilent(SpendData input, Integer cluesRequired) {
            input.getTokenAmountMap().put(TokenType.CLUE, cluesRequired-1);
            Integer sanityRequired = input.getTokenAmountMap().get(TokenType.SANITY);
            input.getTokenAmountMap().put(TokenType.SANITY, sanityRequired +1);
        }

        private void onLoud(SpendData input, Integer cluesRequired) {
            Question<Boolean> question = new Question<>();
            question.setTitle("Spend 1 Sanity in place of spending 1 Clue?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_ASTRONOMER);
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, input, cluesRequired));
        }

        private void onAnswer(Answer<Boolean, Object> answer, SpendData input, Integer cluesRequired) {
            ServicePlatform.get().getService().hold();
            if (answer.getResponseData()) {
                Integer sanityRequired = input.getTokenAmountMap().get(TokenType.SANITY);
                input.getTokenAmountMap().put(TokenType.CLUE, cluesRequired-1);
                input.getTokenAmountMap().put(TokenType.SANITY, sanityRequired+1);
                ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ASTRONOMER).setPassiveAbilityDisabled(true);
                input.addPostRevertAction(() -> ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ASTRONOMER).setPassiveAbilityDisabled(false));
            }
            ServicePlatform.get().getGameService().convertTo(SpendData.class, () -> input);
            ServicePlatform.get().getService().release();
        }


        @Override
        public Class<SpendData> getDataClass() {
            return SpendData.class;
        }
    }
}
