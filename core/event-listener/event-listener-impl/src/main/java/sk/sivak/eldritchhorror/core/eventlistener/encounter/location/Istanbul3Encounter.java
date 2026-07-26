package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul3Encounter extends AbstractLocationEncounter{

    public Istanbul3Encounter() {
        super(3, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.INFLUENCE);
        Stat testStat = Stat.WILL;
        int modifier = 0;
        Runnable onFail = () -> ServicePlatform.get().getTokenService().loseHealth(2);

        TestFailFlavorInfoTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail);
        new InfoFlavorTemplate(getTextBuilder(), autoRewardAction, innerTemplate).execute();
    }

}
