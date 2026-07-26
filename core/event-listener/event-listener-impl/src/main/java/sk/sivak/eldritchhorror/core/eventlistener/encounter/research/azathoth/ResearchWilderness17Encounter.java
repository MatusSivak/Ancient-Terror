package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

import java.util.List;

public class ResearchWilderness17Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness17Encounter() {
        super(17, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getService().hold();
                    OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
                    List<LocationId> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates(omenColor.toGateColor());
                    for (LocationId spawnedGate : spawnedGates) {
                        ServicePlatform.get().getService().moveCameraToLocation(spawnedGate);
                        ServicePlatform.get().getTokenService().loseSanity(2);
                    }
                    ServicePlatform.get().getService().release();

                }
        ).withoutPassFlavor().withoutFailFlavor().withResearchFlavor().execute();
    }
}
