package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Arkham1Encounter extends AbstractLocationEncounter{

    public Arkham1Encounter() {
        super(1, LocationEncounter.LocationEncounterType.ARKHAM);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, +1, () -> ServicePlatform.get().getGameService().gainSpell()).execute();
    }
}
