package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;
import java.util.ArrayList;
import java.util.List;

public class TheProfessorInitListener extends AbstractInvestigatorInitListener {
    private final List<EventListener> listeners = new ArrayList<>();
    private int cluesGainedDuringEncounter;

    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_PROFESSOR; }

    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.ANCIENT_TOME);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override public void justRegisterListeners() {
        cluesGainedDuringEncounter = 0;
        EventListener<GainClueData> afterGainClue = new EventListenerImpl<GainClueData>() {
            @Override public Class<GainClueData> getDataClass() { return GainClueData.class; }
            @Override public void onNotify(GainClueData data) {
                if (data.getInvestigatorId() == getInvestigatorId()) cluesGainedDuringEncounter++;
            }
        };
        EventListener<AvailableEncounters> encountersCollected = new EventListenerImpl<AvailableEncounters>() {
            @Override public Class<AvailableEncounters> getDataClass() { return AvailableEncounters.class; }
            @Override public void onNotify(AvailableEncounters data) { cluesGainedDuringEncounter = 0; }
        };
        EventListener<Object> beforeEndOfEncounter = new EventListenerImpl<Object>() {
            @Override public Class<Object> getDataClass() { return Object.class; }
            @Override public void onNotify(Object data) { onEndOfEncounter(data); }
        };
        EventListener<Object> afterEndOfEncounter = new EventListenerImpl<Object>() {
            @Override public Class<Object> getDataClass() { return Object.class; }
            @Override public void onNotify(Object data) { cluesGainedDuringEncounter = 0; }
        };
        listeners.add(afterGainClue);
        listeners.add(encountersCollected);
        listeners.add(beforeEndOfEncounter);
        listeners.add(afterEndOfEncounter);
        getEventQueue().addAfterEventListener(afterGainClue, BeforeAfterEvent.GAIN_CLUE);
        getEventQueue().addBeforeEventListener(encountersCollected, BeforeAfterEvent.DISABLE_PERFORMED_ENCOUNTERS);
        getEventQueue().addBeforeEventListener(beforeEndOfEncounter, BeforeAfterEvent.END_OF_ENCOUNTER);
        getEventQueue().addAfterEventListener(afterEndOfEncounter, BeforeAfterEvent.END_OF_ENCOUNTER);
    }

    @Override public void unregisterInvestigator() {
        for (EventListener listener : listeners) getEventQueue().unregisterListener(listener);
        listeners.clear();
        getEventQueue().unregisterListener(this);
    }

    private void onEndOfEncounter(Object input) {
        if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != getInvestigatorId()) return;
        if (!(input instanceof Encounter) || ((Encounter) input).getEncounterType() != EncounterType.RESEARCH) return;
        if (cluesGainedDuringEncounter < 1) return;
        cluesGainedDuringEncounter = 0;
        getService().addEventCommand(in -> {
            ServicePlatform.get().getTokenService().canSpend(1, 0, 0, 0).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    getService().convertTo(Object.class, () -> input);
                    return;
                }
                Question<Boolean> question = new Question<>();
                question.setTitle("Spend 1 Clue gained from this Research Encounter to improve 1 skill?");
                question.setPortraitBeforeTitle(getInvestigatorId());
                question.setOptions(Question.Option.noYesOptions);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                    if (!answer.getResponseData()) {
                        getService().convertTo(Object.class, () -> input);
                        return;
                    }
                    ServicePlatform.get().getTokenService().spend(1, 0, 0, 0).subscribe(spendData -> {
                        if (!spendData.hasEnough()) {
                            getService().convertTo(Object.class, () -> input);
                            return;
                        }
                        getService().hold();
                        spendData.pay();
                        ServicePlatform.get().getInvestigatorService().improveSkill(null);
                        getService().convertTo(Object.class, () -> input);
                        getService().release();
                    });
                });
            });
        });
    }
}
