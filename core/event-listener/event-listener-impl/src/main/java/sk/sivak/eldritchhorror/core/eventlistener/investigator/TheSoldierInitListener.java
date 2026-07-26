package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.detained.DetainedCondition;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import java.util.Collections;

/**
 * @author msivak
 */
public class TheSoldierInitListener extends AbstractInvestigatorInitListener {

    private BeforeDelayedListener beforeDelayedListener;
    private BeforeGainDetainedListener beforeGainDetainedListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_SOLDIER;
    }

    @Override
    protected void initInvestigator() {
        beforeDelayedListener = new BeforeDelayedListener();
        beforeGainDetainedListener = new BeforeGainDetainedListener();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDelayedListener, BeforeAfterEvent.BECOME_DELAYED);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeGainDetainedListener, BeforeAfterEvent.SELECT_CARD_TO_GAIN);

        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.DOT_38_REVOLVER);
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.KEROSENE);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        beforeDelayedListener = new BeforeDelayedListener();
        beforeGainDetainedListener = new BeforeGainDetainedListener();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDelayedListener, BeforeAfterEvent.BECOME_DELAYED);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeGainDetainedListener, BeforeAfterEvent.SELECT_CARD_TO_GAIN);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDelayedListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeGainDetainedListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class BeforeGainDetainedListener extends EventListenerImpl<GainCardData> {

        private GainCardData eventData;

        @Override
        public void onNotify(GainCardData eventData) {
            this.eventData = eventData;
            if (ConditionId.DETAINED != eventData.getConditionId()) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_SOLDIER) {
                return;
            }

            Question<Boolean> question = new Question<>();
            question.setTitle("You cannot become Detained");
            question.setPortraitBeforeTitle(InvestigatorId.THE_SOLDIER);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", false)
            ));
            ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswer);
        }

        private void onAnswer(Answer<Boolean, Object> answer) {
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.REGISTER_GAINED_CARD, null);
        }

        @Override
        public Class<GainCardData> getDataClass() {
            return GainCardData.class;
        }
    }

    private class BeforeDelayedListener extends EventListenerImpl<DelayedData> {

        private DelayedData eventData;

        @Override
        public void onNotify(DelayedData eventData) {
            this.eventData = eventData;
            if (!eventData.isForced()) {
                return;
            }
            if (eventData.getInvestigatorId() != InvestigatorId.THE_SOLDIER) {
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setTitle("You cannot become Delayed");
            question.setPortraitBeforeTitle(InvestigatorId.THE_SOLDIER);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", false)
            ));
            ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswer);
        }

        private void onAnswer(Answer<Boolean, Object> answer) {
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.BECOME_DELAYED, eventData);
        }

        @Override
        public Class<DelayedData> getDataClass() {
            return DelayedData.class;
        }
    }
}
