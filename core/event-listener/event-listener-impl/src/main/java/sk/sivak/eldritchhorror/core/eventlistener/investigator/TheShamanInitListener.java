package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.service.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class TheShamanInitListener extends AbstractInvestigatorInitListener {

    private TheShamanPassiveListener theShamanPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_SHAMAN;
    }

    @Override
    protected void initInvestigator() {
        theShamanPassiveListener = new TheShamanPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(theShamanPassiveListener, BeforeAfterEvent.CLOSE_GATE);
        getService().hold();
        getService().gainSpellFromDeck(getInvestigatorId(), SpellId.MISTS_OF_RELEH);
        ServicePlatform.get().getTokenService().gainClueFromPool(InvestigatorId.THE_SHAMAN);
        getService().convertTo(InvestigatorId.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theShamanPassiveListener = new TheShamanPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(theShamanPassiveListener, BeforeAfterEvent.CLOSE_GATE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theShamanPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheShamanPassiveListener extends EventListenerImpl<CloseGateData> {

        @Override
        public void onNotify(CloseGateData eventData) {
            if (!eventData.isOtherworldEncounter()) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_SHAMAN) {
                return;
            }
            Set<LocationId> availableLocations = new HashSet<>();
            availableLocations.addAll(ServicePlatform.get().getCluePool().getClueLocations());
            availableLocations.addAll(ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations());

            if (availableLocations.isEmpty()) {
                return;
            }

            Question<Boolean> question = new Question<>();

            question.setTitle("Move to a Clue or a Gate?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_SHAMAN);
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(eventData, answer, availableLocations));
        }

        private void onAnswer(CloseGateData eventData, Answer<Boolean, Object> answer, Set<LocationId> availableLocations) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(CloseGateData.class, () -> eventData);
            } else {
                SelectLocationData selectLocationData = new SelectLocationData(new LinkedList<>(availableLocations));
                ServicePlatform.get().getGameService().selectLocation(selectLocationData).subscribe(locationId -> onLocationSelected(locationId, eventData));
            }
        }

        private void onLocationSelected(LocationId locationId, CloseGateData eventData) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().removeInvestigatorFromPlace(getActiveInvestigatorId());
            ServicePlatform.get().getInvestigatorService().spawnInvestigator(getActiveInvestigatorId(), locationId);
            ServicePlatform.get().getInvestigatorService().convertTo(CloseGateData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CloseGateData> getDataClass() {
            return CloseGateData.class;
        }
    }
}
