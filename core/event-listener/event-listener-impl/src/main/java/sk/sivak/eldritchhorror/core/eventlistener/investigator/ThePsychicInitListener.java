package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionBack;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;

/**
 * @author msivak
 */
public class ThePsychicInitListener extends AbstractInvestigatorInitListener {

    private ReenableDisabledAbility reenableDisabledAbility;
    private AfterConditionGainedListener afterConditionGainedListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_PSYCHIC;
    }

    @Override
    protected void initInvestigator() {

        reenableDisabledAbility = new ReenableDisabledAbility();
        afterConditionGainedListener = new AfterConditionGainedListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reenableDisabledAbility, DirectEvent.REENABLE_DISABLED_ABILITIES);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterConditionGainedListener, BeforeAfterEvent.REGISTER_GAINED_CARD);

        getService().hold();
        getService().gainSpellFromDeck(getInvestigatorId(), SpellId.FLESH_WARD);
        ServicePlatform.get().getTokenService().gainClueFromPool(getInvestigatorId());
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        reenableDisabledAbility = new ReenableDisabledAbility();
        afterConditionGainedListener = new AfterConditionGainedListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reenableDisabledAbility, DirectEvent.REENABLE_DISABLED_ABILITIES);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterConditionGainedListener, BeforeAfterEvent.REGISTER_GAINED_CARD);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(reenableDisabledAbility);
        ServicePlatform.get().getEventQueue().unregisterListener(afterConditionGainedListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class ReenableDisabledAbility extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_PSYCHIC).setPassiveAbilityDisabled(false);
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }


    private class AfterConditionGainedListener extends EventListenerImpl<GainedCardData> {

        @Override
        public void onNotify(GainedCardData eventData) {
            if (ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_PSYCHIC).isPassiveAbilityDisabled()) {
                return;
            }
            if (eventData.getCardToGain() == null) {
                return;
            }
            if (eventData.getCardType() != GainedCardData.CardType.CONDITION) {
                return;
            }
            if (((ConditionInfo) eventData.getCardToGain()).getTraits().contains(ConditionTrait.COMMON)) {
                return;
            }
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            if (activeInvestigatorId == InvestigatorId.THE_PSYCHIC) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_PSYCHIC);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);

            Question<Boolean> question = new Question<>();
            question.setTitle("Look at the back of that card to gain 1 Clue? (Once per round)");
            question.setPortraitBeforeTitle(InvestigatorId.THE_PSYCHIC);
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).subscribe(
                    answer -> onAnswer(answer, eventData, activeInvestigatorId));

            ServicePlatform.get().getService().release();
        }

        private void onAnswer(Answer<Boolean, Object> answer, GainedCardData eventData, InvestigatorId activeInvestigatorId) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                ServicePlatform.get().getService().convertTo(GainedCardData.class, () -> eventData);
                ServicePlatform.get().getService().release();
                return;
            }
            ConditionBack conditionBack = ((ConditionInfo) eventData.getCardToGain()).getConditionBack();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
            ServicePlatform.get().getEncounterService().typeHeader(conditionBack.getTitle());
            ServicePlatform.get().getEncounterService().typeFlavor(conditionBack.getFlavorText());
            TypewriterUtils.confirmInfos(conditionBack.getEffectText()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().gainClueFromPool(InvestigatorId.THE_PSYCHIC);
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_PSYCHIC).setPassiveAbilityDisabled(true);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();

        }

        @Override
        public Class<GainedCardData> getDataClass() {
            return GainedCardData.class;
        }
    }

}
