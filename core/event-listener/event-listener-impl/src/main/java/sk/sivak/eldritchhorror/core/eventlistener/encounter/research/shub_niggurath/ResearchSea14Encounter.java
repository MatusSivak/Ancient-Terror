package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchSea14Encounter extends AbstractShubNiggurathResearchEncounter {

    public ResearchSea14Encounter() {
        super(14, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), null, this::gainThisClue, null).execute();
    }
}
