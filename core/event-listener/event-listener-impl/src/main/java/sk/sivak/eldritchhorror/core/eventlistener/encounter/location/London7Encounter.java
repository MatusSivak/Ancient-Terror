package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class London7Encounter extends AbstractLocationEncounter{

    public London7Encounter() {
        super(7, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getTokenService().spawnClues(2).subscribe();
            ServicePlatform.get().getService().showLocationBackground(LocationId.LONDON);
            ServicePlatform.get().getService().release();
        },
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                        () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.INTERNAL_INJURY)
                ).withoutFailFlavor()).execute();

    }
}
