package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class BuenosAires9Encounter extends AbstractLocationEncounter{

    public BuenosAires9Encounter() {
        super(9, LocationEncounter.LocationEncounterType.BUENOS_AIRES);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> ServicePlatform.get().getGameService().gainSpell(SpellTrait.RITUAL),
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.HALLUCINATIONS)).execute();
    }

}
