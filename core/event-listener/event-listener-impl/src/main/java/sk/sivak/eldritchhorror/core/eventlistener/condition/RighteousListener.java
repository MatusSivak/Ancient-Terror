package sk.sivak.eldritchhorror.core.eventlistener.condition;

import sk.sivak.eldritchhorror.core.constants.condition.righteous.RighteousCondition;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.List;

public class RighteousListener extends AbstractConditionListener<RighteousCondition> {

    private enum RighteousChoice {FOCUS, SANITY, NONE}

    private AfterRestOrFocusListener afterRestOrFocusListener;
    private AfterRestListener afterRestListener;
    private AfterFocusListener afterFocusListener;
    private ConditionReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(reckoningListener, afterRestListener, afterFocusListener);
    }

    @Override
    protected void register() {
        afterRestOrFocusListener = new AfterRestOrFocusListener();
        afterRestListener = new AfterRestListener();
        afterFocusListener = new AfterFocusListener();
        getEventQueue().addAfterEventListener(afterRestListener, BeforeAfterEvent.REST);
        getEventQueue().addAfterEventListener(afterFocusListener, BeforeAfterEvent.FOCUS);

        reckoningListener = new ConditionReckoningListener(RighteousListener.this);
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class AfterRestListener extends EventListenerImpl<RestData> {

        @Override
        public void onNotify(RestData eventData) {
            afterRestOrFocusListener.onNotify(eventData);
        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }

    private class AfterFocusListener extends EventListenerImpl<FocusData> {

        @Override
        public void onNotify(FocusData eventData) {
            afterRestOrFocusListener.onNotify(eventData);
        }

        @Override
        public Class<FocusData> getDataClass() {
            return FocusData.class;
        }
    }

    private class AfterRestOrFocusListener {

        private Object eventData;

        public void onNotify(Object eventData) {
            this.eventData = eventData;
            if (!isOwner()) {
                return;
            }
            boolean canGainFocus = canGainFocus();
            boolean canGainSanity = canGainSanity();
            if (canGainFocus || canGainSanity) {
                askForBonus(canGainFocus, canGainSanity);
            }
        }

        // One explicit choice, so declining Focus can't silently skip the Sanity option.
        private void askForBonus(boolean canGainFocus, boolean canGainSanity) {
            Question<RighteousChoice> question = new Question<>();
            question.setTitle(conditionInfo.getName() + ": gain 1 Focus or recover 1 Sanity?");
            question.setPortraitBeforeTitle(investigatorId);
            if (canGainFocus) {
                question.addOption(new Question.Option<>("Gain 1 Focus", RighteousChoice.FOCUS));
            }
            if (canGainSanity) {
                question.addOption(new Question.Option<>("Recover 1 Sanity", RighteousChoice.SANITY));
            }
            question.addOption(new Question.Option<>("Neither", RighteousChoice.NONE, 0x800000ff));
            ServicePlatform.get().getGameService().<RighteousChoice, Object>ask(question)
                    .subscribe(answer -> onAnswer(answer.getResponseData()));
        }

        private void onAnswer(RighteousChoice choice) {
            if (choice == RighteousChoice.FOCUS) {
                gainFocus();
            } else if (choice == RighteousChoice.SANITY) {
                gainSanity();
            } else {
                ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
            }
        }

        private void gainSanity() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainSanity(1);
            ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        private void gainFocus() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainFocus();
            ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        private boolean canGainFocus() {
            return ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getFocusTokens() < 2;
        }

        private boolean canGainSanity() {
            if (eventData instanceof RestData && !((RestData) eventData).isSanityEnabled()) {
                return false;
            }
            InvestigatorRead investigator = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId);
            return investigator.getCurrentSanity() < investigator.getInfo().getMaxSanity();
        }
    }


}
