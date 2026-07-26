package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

import java.util.List;

public class ResearchWilderness24Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness24Encounter() {
        super(24, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getGameService().gainArtifact(AssetTrait.WEAPON);
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    ServicePlatform.get().getService().hold();
                    OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
                    List<LocationId> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates(omenColor.toGateColor());
                    for (LocationId spawnedGate : spawnedGates) {
                        ServicePlatform.get().getService().moveCameraToLocation(spawnedGate);
                        ServicePlatform.get().getTokenService().loseHealth(2);
                    }
                    ServicePlatform.get().getService().release();

                }
        ).withTwoPassInfos().withoutFailFlavor().withResearchFlavor().execute();
    }
}
