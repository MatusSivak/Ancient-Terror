package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.GainConditionTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco11Encounter extends AbstractLocationEncounter{

    public SanFrancisco11Encounter() {
        super(11, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        new GainConditionTemplate(getTextBuilder(), ConditionId.DARK_PACT, this::improveSkillOfChoice).execute();
    }

    private void improveSkillOfChoice() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().improveSkill(Stat.OBSERVATION);
        ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
        ServicePlatform.get().getInvestigatorService().improveSkill(null);
        ServicePlatform.get().getService().release();
    }
}
