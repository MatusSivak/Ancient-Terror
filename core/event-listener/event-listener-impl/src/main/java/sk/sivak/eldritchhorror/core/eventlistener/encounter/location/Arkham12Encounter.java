package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Arkham12Encounter extends AbstractLocationEncounter{

    public Arkham12Encounter() {
        super(12, LocationEncounter.LocationEncounterType.ARKHAM);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> ServicePlatform.get().getGameService().gainSpell(SpellId.PLUMB_THE_VOID),
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE)).execute();
    }
}
