package sk.sivak.eldritchhorror.core.eventlistener.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.CombatEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ExpeditionEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.GeneralEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.OtherWorldEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ResearchEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.SkipEncounter;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class CollectCommonEncountersListener implements EventListener<AvailableEncounters> {

    private AvailableEncounters availableEncounters;
    private LocationInfo locationInfo;

    @Override
    public void onNotify(AvailableEncounters availableEncounters) {
        this.availableEncounters = availableEncounters;
        InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        this.locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(activeInvestigator.getCurrentLocationId());

        if (activeInvestigator.getCurrentLocationId() != null) {
            addCombatEncounter();
            addGeneralEncounter();
            addLocationEncounter();
            addOtherWorldEncounter();
            addExpeditionEncounter();
            addResearchEncounter();
            addSkipEncounter();
        }
    }

    private void addSkipEncounter() {
        List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup()
                .getMonstersAtLocation(locationInfo.getLocationId());
        if (ServicePlatform.get().getPerformedEncounters().canSkip(getActiveInvestigatorId(), !monstersAtLocation.isEmpty())) {
            availableEncounters.addEncounter(new SkipEncounter());
        }
    }

    private void addCombatEncounter() {
        if (!ServicePlatform.get().getMonsterCup().containsMonster(locationInfo.getLocationId())) {
            return;
        }
        List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(locationInfo.getLocationId());
        for (MonsterInfo monsterInfo : monstersAtLocation) {
            availableEncounters.addEncounter(new CombatEncounter(monsterInfo));
        }
    }

    private void addGeneralEncounter() {
        availableEncounters.addEncounter(new GeneralEncounter(locationInfo.getLocationType()));
    }

    private void addLocationEncounter() {
        LocationEncounter.LocationEncounterType locationEncounterType;
        if (locationInfo.getLocationType() != LocationType.CITY) {
            return;
        }
        if (locationInfo.getLocationId() == LocationId.SAN_FRANCISCO) {
            locationEncounterType = LocationEncounter.LocationEncounterType.SAN_FRANCISCO;
        } else if (locationInfo.getLocationId() == LocationId.BUENOS_AIRES) {
            locationEncounterType = LocationEncounter.LocationEncounterType.BUENOS_AIRES;
        } else if (locationInfo.getLocationId() == LocationId.ARKHAM) {
            locationEncounterType = LocationEncounter.LocationEncounterType.ARKHAM;
        } else if (locationInfo.getLocationId() == LocationId.LONDON) {
            locationEncounterType = LocationEncounter.LocationEncounterType.LONDON;
        } else if (locationInfo.getLocationId() == LocationId.ROME) {
            locationEncounterType = LocationEncounter.LocationEncounterType.ROME;
        } else if (locationInfo.getLocationId() == LocationId.ISTANBUL) {
            locationEncounterType = LocationEncounter.LocationEncounterType.ISTANBUL;
        } else if (locationInfo.getLocationId() == LocationId.SHANGHAI) {
            locationEncounterType = LocationEncounter.LocationEncounterType.SHANGHAI;
        } else if (locationInfo.getLocationId() == LocationId.TOKYO) {
            locationEncounterType = LocationEncounter.LocationEncounterType.TOKYO;
        } else if (locationInfo.getLocationId() == LocationId.SYDNEY) {
            locationEncounterType = LocationEncounter.LocationEncounterType.SYDNEY;
        } else {
            return;
        }

        availableEncounters.addEncounter(new LocationEncounter(locationEncounterType));
    }

    private void addOtherWorldEncounter() {
        if (!ServicePlatform.get().getGateStackRead().isGateAtLocation(locationInfo.getLocationId())) {
            return;
        }
        availableEncounters.addEncounter(new OtherWorldEncounter(
                ServicePlatform.get().getGateStackRead().getGateColor(locationInfo.getLocationId()),
                locationInfo.getLocationId()));
    }

    private void addExpeditionEncounter() {
        if (ServicePlatform.get().getExpeditionDeck().getExpeditionTokenLocation() != locationInfo.getLocationId()) {
            return;
        }
        availableEncounters.addEncounter(new ExpeditionEncounter(locationInfo.getLocationId()));
    }

    private void addResearchEncounter() {
        if (ServicePlatform.get().getCluePool().isClueAt(locationInfo.getLocationId())) {
            availableEncounters.addEncounter(new ResearchEncounter(locationInfo.getLocationType(), locationInfo.getLocationId()));
        }
    }


    @Override
    public Class<AvailableEncounters> getDataClass() {
        return AvailableEncounters.class;
    }
}
