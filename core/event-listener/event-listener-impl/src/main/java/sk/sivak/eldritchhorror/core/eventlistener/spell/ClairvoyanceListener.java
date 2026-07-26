package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.spell.clairvoyance.ClairvoyanceSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ResearchEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClairvoyanceListener extends AbstractSpellListener<ClairvoyanceSpell> {

    private EncounterClueListener encounterClueListener;

    private ResearchEncounter researchEncounter = null;
    private RemoveResearchEncounterListener removeResearchEncounterListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(encounterClueListener, removeResearchEncounterListener);
    }

    @Override
    protected void register() {
        encounterClueListener = new EncounterClueListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(encounterClueListener, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        removeResearchEncounterListener = new RemoveResearchEncounterListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(removeResearchEncounterListener, DirectEvent.INVESTIGATOR_FINISHED_ENCOUNTER);
    }

    private class EncounterClueListener extends EventListenerImpl<AvailableEncounters> {

        @Override
        public void onNotify(AvailableEncounters eventData) {
            if (researchEncounter != null) {
                eventData.addEncounter(researchEncounter);
                return;
            }
            List<? extends ClueInfo> spawnedClues = ServicePlatform.get().getCluePool().getSpawnedClues();

            if (!isOwner()) {
                return;
            }
            if (spawnedClues.isEmpty()) {
                return;
            }
            if (!isClueOnDifferentPlace(spawnedClues)) {
                return;
            }

            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setTitle("Test Lore to Encounter a Clue?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setSpellInfo(spellInfo);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(response -> afterShowCard(response, eventData, spawnedClues));
        }

        private boolean isClueOnDifferentPlace(List<? extends ClueInfo> spawnedClues) {
            for (ClueInfo spawnedClue : spawnedClues) {
                if (spawnedClue.getCurrentLocationId() != ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId()) {
                    return true;
                }
            }
            return false;
        }

        private void afterShowCard(ShowCardResponse response, AvailableEncounters eventData, List<? extends ClueInfo> spawnedClues) {
            if (response != ShowCardResponse.YES) {
                ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> eventData);
                return;
            }
            List<LocationId> clueLocations = new LinkedList<>(Stream.map(spawnedClues, ClueInfo::getCurrentLocationId));
            SelectLocationData selectLocationData = new SelectLocationData(clueLocations);
            ServicePlatform.get().getGameService().selectLocation(selectLocationData).subscribe(locationId -> {
                afterLocationSelect(locationId, eventData);
            });
        }

        private void afterLocationSelect(LocationId locationId, AvailableEncounters eventData) {

            ServicePlatform.get().getTestService().test(Stat.LORE, 0, 3,
                    new TestFlavorRequest(TestFlavorType.SPELL, spellInfo)).subscribe(testResult ->
                    afterTest(testResult, locationId, eventData));
        }

        private void afterTest(TestData testResult, LocationId locationId, AvailableEncounters eventData) {
            ServicePlatform.get().getService().hold();

            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setData("clueLocationId", locationId);
            spellBackListener.setMainSpellAction(createMainSpellAction(locationId, eventData));
//            spellBackListener.setData("selectedInvestigator", selectedInvestigator);
            spellBackListener.executeWhole();

            ServicePlatform.get().getEncounterService().checkDefeatedAfterEncounter();
            ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }


    private Runnable createMainSpellAction(LocationId locationId, AvailableEncounters eventData) {
        return () -> {
            LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(locationId);
            researchEncounter = new ResearchEncounter(locationInfo.getLocationType(), locationInfo.getLocationId()) {
                @Override
                public EncounterButtonData buildButtonData() {
                    EncounterButtonData encounterButtonData = super.buildButtonData();
                    encounterButtonData.setFirstLine("Research");
                    encounterButtonData.setSecondLine("Clairvoyance");
                    encounterButtonData.setButtonIcon("token/clue.png");
                    return encounterButtonData;
                }
            };
            eventData.addEncounter(researchEncounter);
        };
    }

    private boolean isOwner() {
        return spellOwnerId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
    }

    private class RemoveResearchEncounterListener extends EventListenerImpl<EncounterResult> {
        @Override
        public void onNotify(EncounterResult eventData) {
            researchEncounter = null;
        }

        @Override
        public Class<EncounterResult> getDataClass() {
            return EncounterResult.class;
        }
    }
}
