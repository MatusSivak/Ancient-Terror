package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Shanghai1Encounter extends AbstractLocationEncounter{

    public Shanghai1Encounter() {
        super(1, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.LORE);
        Stat testStat = Stat.WILL;
        int modifier = 0;
        Runnable onFail = () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.HALLUCINATIONS);

        TestFailFlavorInfoTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail);
        new InfoFlavorTemplate(getTextBuilder(), autoRewardAction, innerTemplate).execute();
    }
}
