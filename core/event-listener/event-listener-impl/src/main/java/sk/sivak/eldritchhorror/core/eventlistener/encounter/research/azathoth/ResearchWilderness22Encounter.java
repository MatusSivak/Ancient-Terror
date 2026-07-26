package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness22Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness22Encounter() {
        super(22, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getTokenService().discardClue(getLocationId());
                    LocationId activeInvestigatorLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                    if (activeInvestigatorLocationId != getLocationId()) {
                        ServicePlatform.get().getService().moveCameraToLocation(activeInvestigatorLocationId);
                    }
                    ServicePlatform.get().getGameService().gainArtifact(ArtifactId.CULTES_DES_GOULES);
                    ServicePlatform.get().getService().release();
                },
                () -> ServicePlatform.get().getDoomOmenService().advanceOmen()
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
