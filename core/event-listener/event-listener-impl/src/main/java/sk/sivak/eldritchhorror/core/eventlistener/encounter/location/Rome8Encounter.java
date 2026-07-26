package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome8Encounter extends AbstractLocationEncounter{

    public Rome8Encounter() {
        super(8, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL);
        Stat testStat = Stat.WILL;
        int modifier = -1;
        Runnable onFail = () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
        };

        EncounterTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail).withoutFailFlavor();
        new InfoFlavorTemplate(getTextBuilder(),autoRewardAction, innerTemplate).execute();
    }
}
