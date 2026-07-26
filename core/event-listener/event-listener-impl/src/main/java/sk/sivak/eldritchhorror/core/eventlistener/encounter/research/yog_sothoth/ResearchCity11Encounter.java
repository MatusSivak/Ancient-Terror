package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;

public class ResearchCity11Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity11Encounter() {
        super(11, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.CULTIST, () -> {
            gainThisClue();
        }, null).execute();
    }
}
