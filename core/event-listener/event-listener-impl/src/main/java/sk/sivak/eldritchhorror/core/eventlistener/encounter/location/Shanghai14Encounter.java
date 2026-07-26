package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Shanghai14Encounter extends AbstractLocationEncounter{

    public Shanghai14Encounter() {
        super(14, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -1,
                () -> {
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                },
                () -> {
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.AMNESIA);
                }).withoutPassFlavor().withTwoFailInfos().execute();
    }
}
