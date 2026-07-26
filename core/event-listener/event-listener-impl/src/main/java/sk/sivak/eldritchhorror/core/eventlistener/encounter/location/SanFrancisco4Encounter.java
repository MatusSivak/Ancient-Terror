package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class SanFrancisco4Encounter extends AbstractLocationEncounter{

    public SanFrancisco4Encounter() {
        super(4, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        new BecomeDelayedPassFlavorInfoTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
        }).execute();
    }
}
