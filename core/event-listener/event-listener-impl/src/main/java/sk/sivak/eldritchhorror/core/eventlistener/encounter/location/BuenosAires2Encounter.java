package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class BuenosAires2Encounter extends AbstractLocationEncounter{

    public BuenosAires2Encounter() {
        super(2, LocationEncounter.LocationEncounterType.BUENOS_AIRES);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getGameService().gainSpell(SpellTrait.RITUAL);
        Stat testStat = Stat.LORE;
        int modifier = 0;
        Runnable onFail = () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);

        TestFailFlavorInfoTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail);
        new InfoFlavorTemplate(getTextBuilder(), autoRewardAction, innerTemplate).execute();
    }

}
