package sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;

public class RlyehRisenListener extends AbstractMysteryListener implements EventListener<AvailableEncounters>  {

    private AvailableEncounters availableEncounters;

    private int progress;

    public RlyehRisenListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void register() {
        ServicePlatform.get().getGameService().hold();
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
        ServicePlatform.get().getGameService().spawnRedPins();
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        ServicePlatform.get().getGameService().release();
    }

    @Override
    public void justAddRedPins() {
        ServicePlatform.get().getGameService().justAddRedPins();
    }

    @Override
    public void justRegisterListeners(int progress) {
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        this.progress = progress;
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(this);
        ServicePlatform.get().getGameService().clearRedPins();
    }


    @Override
    public void onNotify(AvailableEncounters eventData) {
        this.availableEncounters = eventData;
        InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (!mysteryCardInfo.getPinLocations().contains(activeInvestigator.getCurrentLocationId())) {
            return;
        }
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            return;
        }
        MysteryEncounter encounter = new MysteryEncounter(mysteryCardInfo.getName());
        encounter.setEncounterType(EncounterType.RLYEH_RISEN);
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
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
        }
    }
}
