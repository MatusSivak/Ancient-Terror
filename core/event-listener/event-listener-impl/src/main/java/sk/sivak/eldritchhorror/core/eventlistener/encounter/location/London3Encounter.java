package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class London3Encounter extends AbstractLocationEncounter{

    public London3Encounter() {
        super(3, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(locationId ->
                        ServicePlatform.get().getTokenService().spawnClueAt(locationId)),
                () -> ServicePlatform.get().getTokenService().loseSanity(1)
        ).withoutPassFlavor().execute();
    }
}
