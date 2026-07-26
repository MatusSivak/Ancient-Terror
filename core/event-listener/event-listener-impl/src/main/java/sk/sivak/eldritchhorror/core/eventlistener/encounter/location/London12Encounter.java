package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class London12Encounter extends AbstractLocationEncounter{

    public London12Encounter() {
        super(12, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.CULTIST,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().spawnClues(1).subscribe();
                    ServicePlatform.get().getService().release();
                },
                null)
                /*
                .withJustAfterAmbushAction(
                        () -> ServicePlatform.get().getService().showLocationBackground(LocationId.LONDON)
                )*/.execute();
    }
}
