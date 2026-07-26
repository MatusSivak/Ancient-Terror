package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;

/**
 * @author msivak
 */
public class TheExConvictInitListener extends AbstractInvestigatorInitListener {

    private TheExConvictPassiveListener afterRollListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_EX_CONVICT;
    }

    @Override
    protected void initInvestigator() {
        afterRollListener = new TheExConvictPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollListener, BeforeAfterEvent.SHOW_ROLLED_DICES);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollListener, BeforeAfterEvent.REROLL_DIE);
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.AXE);
        getService().convertFromTo(GainAssetFromDeckData.class, InvestigatorId.class, (in) -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        afterRollListener = new TheExConvictPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollListener, BeforeAfterEvent.SHOW_ROLLED_DICES);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollListener, BeforeAfterEvent.REROLL_DIE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterRollListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }



    private class TheExConvictPassiveListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_EX_CONVICT) {
                return;
            }
            if (eventData.hasReachedMinScoreToEndTest()) {
                return;
            }
            if (eventData.getCalculatedDicePool() <= eventData.getScore()) {
                return;
            }
            int rolledOnes = Stream.count(eventData.getDiceRolls(), diceRoll -> diceRoll.getDiceValue() == 1);
            if (rolledOnes == 0) {
                return;
            }

            askQuestion().subscribe(answer -> {
                if (Boolean.TRUE.equals(answer.getResponseData())) {
                    ServicePlatform.get().getTestService().hold();
                    ServicePlatform.get().getTestService().findCountRerollDice(rolledOnes, CountRerollDiceData.CountRerollDiceType.OTHER, eventData);
                    ServicePlatform.get().getTestService().rerollDice();
                    ServicePlatform.get().getTestService().release();
                } else {
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                }
            });

        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();
            question.setTitle("Reroll dice with value 1?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_EX_CONVICT);
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

}
