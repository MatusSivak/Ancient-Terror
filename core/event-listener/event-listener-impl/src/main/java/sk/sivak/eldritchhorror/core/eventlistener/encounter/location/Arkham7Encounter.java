package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Arkham7Encounter extends AbstractLocationEncounter{

    public Arkham7Encounter() {
        super(7, LocationEncounter.LocationEncounterType.ARKHAM);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, () -> {
            ServicePlatform.get().getGameService().gainSpell(SpellTrait.INCANTATION);
            ServicePlatform.get().getTokenService().spawnClues(1).subscribe();
        }).withTwoPassInfos().execute();
    }
}
