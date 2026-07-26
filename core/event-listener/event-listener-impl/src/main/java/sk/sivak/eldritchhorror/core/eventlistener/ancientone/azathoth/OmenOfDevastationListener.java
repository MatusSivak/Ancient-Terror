package sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId.REQUIEM_PER_SHUGGAY;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CLOSE_GATE;

public class OmenOfDevastationListener extends AbstractMysteryListener {


    private int progress;
    private AfterCloseGateListener afterCloseGateListener;

    public OmenOfDevastationListener(MysteryCardInfo mysteryCardInfo) {
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
        OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
        return ServicePlatform.get().getGateStackRead().getSpawnedGates(omenColor.toGateColor());
    }

    private class AfterCloseGateListener extends EventListenerImpl<CloseGateData> {

        @Override
        public void onNotify(CloseGateData eventData) {
            if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                return;
            }
            OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
            if (!eventData.getGateColor().equals(omenColor.toGateColor())) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(spendData -> {
                if (spendData.hasEnough()) {
                    Question<Boolean> question = new Question<>();
                    question.setOptions(Arrays.asList(
                            new Question.Option<>("No", false, 0x800000ff),
                            new Question.Option<>("Yes", true, 0x008000ff)
                    ));
                    question.setTitle("Spend one Clue to advance the Active Mystery?");
                    question.displayCurrentMysteryCard();
                    ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData));
                } else {
                    ServicePlatform.get().getService().convertTo(CloseGateData.class, () -> eventData);
                }
            });
        }

        private void onAnswer(Answer<Boolean, Object> answer, CloseGateData eventData) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                return;
            }
            ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> {
                if (spendData.hasEnough()) {
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    progress++;
                    ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                }
            });


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
