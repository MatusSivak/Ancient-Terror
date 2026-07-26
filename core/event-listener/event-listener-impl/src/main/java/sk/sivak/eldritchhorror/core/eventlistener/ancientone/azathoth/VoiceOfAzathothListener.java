package sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId.REQUIEM_PER_SHUGGAY;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.RESOLVE_CURRENT_MYSTERY;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.SELECT_CARD_TO_GAIN;

public class VoiceOfAzathothListener extends AbstractMysteryListener {


    private BeforeSelectCardToGainListener beforeSelectCardToGainListener;
    private BeforeResolveCurrentMysteryListener beforeResolveCurrentMysteryListener;

    public VoiceOfAzathothListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    private int progress = 0;
    @Override
    protected int getProgress() {
        return progress;
    }

    @Override
    public void register() {
        super.register();
        beforeSelectCardToGainListener = new BeforeSelectCardToGainListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeSelectCardToGainListener, SELECT_CARD_TO_GAIN);

        beforeResolveCurrentMysteryListener = new BeforeResolveCurrentMysteryListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeResolveCurrentMysteryListener, RESOLVE_CURRENT_MYSTERY);
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    public void justRegisterListeners(int progress) {
        beforeSelectCardToGainListener = new BeforeSelectCardToGainListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeSelectCardToGainListener, SELECT_CARD_TO_GAIN);

        beforeResolveCurrentMysteryListener = new BeforeResolveCurrentMysteryListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeResolveCurrentMysteryListener, RESOLVE_CURRENT_MYSTERY);
        this.progress = progress;
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeSelectCardToGainListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeResolveCurrentMysteryListener);
    }

    @Override
    protected List<LocationId> getPinLocations() {
        return Collections.singletonList(ServicePlatform.get().getExpeditionDeck().getExpeditionTokenLocation());
    }

    private class BeforeSelectCardToGainListener extends EventListenerImpl<GainCardData> {

        @Override
        public void onNotify(GainCardData eventData) {
            if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                return;
            }
            if (!eventData.isArtifact()) {
                return;
            }

            if (!ServicePlatform.get().getArtifactsDeck().isInDeckOrDiscardPile(REQUIEM_PER_SHUGGAY)) {
                // someone has it
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            question.setTitle("Gain 'Requiem per Shuggay' Artifact instead?");
            question.displayCurrentMysteryCard();
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData));
        }

        private void onAnswer(Answer<Boolean, Object> answer, GainCardData eventData) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                return;
            }
            ServicePlatform.get().getService().convertTo(getDataClass(), () -> new GainCardData(REQUIEM_PER_SHUGGAY));

        }

        @Override
        public Class<GainCardData> getDataClass() {
            return GainCardData.class;
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


    private class BeforeResolveCurrentMysteryListener extends EventListenerImpl<Object> {

        InvestigatorId artifactOwner;

        @Override
        public void onNotify(Object eventData) {
            artifactOwner = null;
            for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                boolean hasArtifact = ServicePlatform.get().getArtifactsDeck().hasArtifact(investigator.getInfo().getInvestigatorId(), REQUIEM_PER_SHUGGAY);
                if (!hasArtifact) {
                    continue;
                }
                artifactOwner = investigator.getInfo().getInvestigatorId();
            }
            if (artifactOwner == null) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(artifactOwner);
            ServicePlatform.get().getTokenService().canSpend(mysteryCardInfo.getMysteryComplexity(), 0,0,0).subscribe(canSpend -> {
                if (canSpend.hasEnough()) {
                    onCanSpend();
                }
            });
            ServicePlatform.get().getService().release();

        }

        private void onCanSpend() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            Question<Boolean> question = new Question<>();
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            question.setTitle("Spend "+mysteryCardInfo.getMysteryComplexity()+" Clue(s) to solve current Mystery?");
            question.displayCurrentMysteryCard();
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                if (answer.getResponseData()) {
                    onSpend();
                }
            });
            ServicePlatform.get().getService().release();
        }

        private void onSpend() {
            ServicePlatform.get().getTokenService().spend(mysteryCardInfo.getMysteryComplexity(), 0,0,0).subscribe(spendData -> {
                if (!spendData.hasEnough()) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                ArtifactInfo artifact = Stream.findFirstOrException(ServicePlatform.get().getArtifactsDeck().getArtifacts(artifactOwner),
                        artifactInfo -> artifactInfo.getId().equals(REQUIEM_PER_SHUGGAY));
                EncounterUtils.onSelectCardToDiscardByChoice(artifact);
                progress += mysteryCardInfo.getMysteryComplexity();
                ServicePlatform.get().getService().release();

            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

}
