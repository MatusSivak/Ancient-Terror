package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class TheKeyAndTheGateListener extends AbstractMysteryListener implements EventListener<AvailableEncounters>  {

    private AvailableEncounters availableEncounters;

    private int progress;

    public TheKeyAndTheGateListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void register() {
        super.register();
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);

    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    protected List<LocationId> getPinLocations() {
        return ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations();
    }

    @Override
    public void justRegisterListeners(int progress) {
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        this.progress = progress;
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }


    @Override
    public void onNotify(AvailableEncounters eventData) {
        this.availableEncounters = eventData;
        InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (!ServicePlatform.get().getGateStackRead().isGateAtLocation(activeInvestigator.getCurrentLocationId())) {
            return;
        }
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            return;
        }
        MysteryEncounter encounter = new MysteryEncounter(mysteryCardInfo.getName());
        encounter.setEncounterType(EncounterType.THE_KEY_AND_THE_GATE);
        availableEncounters.addEncounter(encounter);
        ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> availableEncounters);
    }

    @Override
    public Class<AvailableEncounters> getDataClass() {
        return AvailableEncounters.class;
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
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
            if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                ServicePlatform.get().getDoomOmenService().resolveCurrentMystery();
            }
            ServicePlatform.get().getService().release();
        }
    }
}
