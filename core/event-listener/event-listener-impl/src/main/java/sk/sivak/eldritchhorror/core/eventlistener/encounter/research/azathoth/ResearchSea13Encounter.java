package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import java.util.List;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea13Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea13Encounter() {
        super(13, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getService().hold();
                    LocationId clueSpawnLocationId = findClueSpawnLocationId();
                    List<LocationInfo.Connection> connections = ServicePlatform.get().getLocationMap().getLocationInfo(getLocationId()).getConnections();
                    LocationId targetLocationId = connections.get(new Random().nextInt(connections.size())).getLocationId();
                    ServicePlatform.get().getTokenService().moveClue(clueSpawnLocationId, targetLocationId);
                    ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
                    ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true));
                    ServicePlatform.get().getService().release();
                }
        ).withTwoFailInfos().withResearchFlavor().execute();

    }

    private LocationId findClueSpawnLocationId() {
        for (ClueInfo spawnedClue : ServicePlatform.get().getCluePool().getSpawnedClues()) {
            if (spawnedClue.getCurrentLocationId() == getLocationId()) {
                return spawnedClue.getSpawnLocationId();
            }
        }
        throw new IllegalArgumentException();
    }
}
