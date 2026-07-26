package sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.GAIN_CLUE;

public class TheStarsAreRightListener extends AbstractMysteryListener {


    private int progress;
    private AfterGainClueListener afterGainClueListener;

    public TheStarsAreRightListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    protected int getProgress() {
        return progress;
    }

    @Override
    protected List<LocationId> getPinLocations() {
        return new LinkedList<>(ServicePlatform.get().getCluePool().getClueLocations());
    }

    @Override
    public void register() {
        ServicePlatform.get().getGameService().hold();
        for (ClueInfo spawnedClue : ServicePlatform.get().getCluePool().getSpawnedClues()) {
            LocationInfo clueLocationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(spawnedClue.getCurrentLocationId());
            if (clueLocationInfo.getLocationType() == LocationType.SEA) {
                continue;
            }
            FindNearestData nearestSeaSpace = ServicePlatform.get().getLocationMap().findNearest(spawnedClue.getCurrentLocationId(),
                    location -> location.getLocationType() == LocationType.SEA);
            if (nearestSeaSpace != null) {
                ServicePlatform.get().getTokenService().moveClue(spawnedClue.getSpawnLocationId(), nearestSeaSpace.getLocationId());
            }
        }
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
        ServicePlatform.get().getGameService().release();

        afterGainClueListener = new AfterGainClueListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterGainClueListener, GAIN_CLUE);
    }

    @Override
    public void justRegisterListeners(int progress) {
        afterGainClueListener = new AfterGainClueListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterGainClueListener, GAIN_CLUE);
        this.progress = progress;
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterGainClueListener);
    }

    private class AfterGainClueListener extends EventListenerImpl<GainClueData> {

        @Override
        public void onNotify(GainClueData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                    return;
                }
                if (!eventData.isEncounter()) {
                    return;
                }
                if (eventData.isClueGained()) {
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
                        ServicePlatform.get().getService().convertTo(GainClueData.class, () -> eventData);
                    }
                });
            });
        }

        private void onAnswer(Answer<Boolean, Object> answer, GainClueData eventData) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                return;
            }
            ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> {
                if (spendData.hasEnough()) {
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    progress++;
                    eventData.setClueGained(true);
                    ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);


                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                }
            });
        }

        @Override
        public Class<GainClueData> getDataClass() {
            return GainClueData.class;
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
