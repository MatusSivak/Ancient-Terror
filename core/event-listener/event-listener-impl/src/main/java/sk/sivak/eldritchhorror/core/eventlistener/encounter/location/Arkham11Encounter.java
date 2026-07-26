package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Arkham11Encounter extends AbstractLocationEncounter{

    public Arkham11Encounter() {
        super(11, LocationEncounter.LocationEncounterType.ARKHAM);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> ServicePlatform.get().getGameService().gainSpell(SpellTrait.INCANTATION),
                () -> ServicePlatform.get().getTokenService().loseSanity(2))
                .withoutPassFlavor().execute();
    }
}
