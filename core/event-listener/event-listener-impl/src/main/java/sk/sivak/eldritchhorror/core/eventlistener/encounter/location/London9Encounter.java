package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class London9Encounter extends AbstractLocationEncounter{

    public London9Encounter() {
        super(9, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> ServicePlatform.get().getTokenService().spawnClues(2).subscribe(),
                () -> ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true))).execute();
    }
}
