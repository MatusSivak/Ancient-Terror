package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul2Encounter extends AbstractLocationEncounter{

    public Istanbul2Encounter() {
        super(2, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new SpendCluePassFlavorInfoTemplate(getTextBuilder(), () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.INFLUENCE)).execute();
    }
}
