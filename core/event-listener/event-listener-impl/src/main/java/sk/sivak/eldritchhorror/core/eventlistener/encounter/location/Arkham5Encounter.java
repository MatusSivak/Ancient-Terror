package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Arkham5Encounter extends AbstractLocationEncounter{

    public Arkham5Encounter() {
        super(5, LocationEncounter.LocationEncounterType.ARKHAM);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getGameService().gainSpell(SpellTrait.INCANTATION);
        Stat testStat = Stat.LORE;
        int modifier = 0;
        Runnable onFail = () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.HALLUCINATIONS);

        TestFailFlavorInfoTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail);
        new InfoFlavorTemplate(getTextBuilder(), autoRewardAction, innerTemplate).execute();
    }
}
