package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

public class London11Encounter extends AbstractLocationEncounter{

    public London11Encounter() {
        super(11, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                this::spawnCluesOnGates,
                () -> ServicePlatform.get().getTokenService().loseSanity(1)).execute();
    }

    private void spawnCluesOnGates() {
        List<LocationId> spawnedGatesLocations = ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations();
        for (LocationId spawnedGatesLocation : spawnedGatesLocations) {
            ServicePlatform.get().getTokenService().spawnClueAt(spawnedGatesLocation);
        }
    }
}
