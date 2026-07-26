package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;

public class ResearchWilderness14Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness14Encounter() {
        super(14, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), null, this::gainThisClue, () -> {
            ServicePlatform.get().getDoomOmenService().advanceDoom();
        }).withoutPassFlavor().execute();
    }
}
