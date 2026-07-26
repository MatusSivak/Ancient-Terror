package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CLOSE_GATE;

public class WhereTheOldOnesBrokeThroughListener extends AbstractMysteryListener {


    private int progress;
    private AfterCloseGateListener afterCloseGateListener;

    public WhereTheOldOnesBrokeThroughListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    protected int getProgress() {
        return progress;
    }

    @Override
    public void register() {
        super.register();
        afterCloseGateListener = new AfterCloseGateListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterCloseGateListener, CLOSE_GATE);
    }

    @Override
    public void justRegisterListeners(int progress) {
        afterCloseGateListener = new AfterCloseGateListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterCloseGateListener, CLOSE_GATE);
        this.progress = progress;
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterCloseGateListener);
    }

    @Override
    protected List<LocationId> getPinLocations() {
        return ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations();
    }

    private class AfterCloseGateListener extends EventListenerImpl<CloseGateData> {

        @Override
        public void onNotify(CloseGateData eventData) {
            if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                return;
            }
            if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).size() < 2) {
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            question.setTitle("Discard two Spells to seal the rift?");
            question.displayCurrentMysteryCard();
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData));
        }

        private void onAnswer(Answer<Boolean, Object> answer, CloseGateData eventData) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                return;
            }

            ServicePlatform.get().getService().hold();
            for (int i = 0; i < 2; i++) {
                ServicePlatform.get().getService().addEventCommand(in -> {
                    SelectCardData selectCardData = new SelectCardData();
                    selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                    selectCardData.setHideText("Display Spells?");
                    selectCardData.setTitleText("Select Spell");
                    ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
                });
            }
            progress++;
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CloseGateData> getDataClass() {
            return CloseGateData.class;
        }
    }

    @Override
    public void advanceActiveMystery() {
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        } else {
            progress++;
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
        }
    }
}
