package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco3Encounter extends AbstractLocationEncounter{

    public SanFrancisco3Encounter() {
        super(3, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {

        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED),
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED)).execute();
    }
}
