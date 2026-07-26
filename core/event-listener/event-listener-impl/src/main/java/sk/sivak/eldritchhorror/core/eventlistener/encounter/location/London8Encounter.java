package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class London8Encounter extends AbstractLocationEncounter{

    public London8Encounter() {
        super(8, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new BecomeDelayedTemplate(getTextBuilder(), () -> ServicePlatform.get().getTokenService().spawnClues(2).subscribe()).execute();

    }
}
