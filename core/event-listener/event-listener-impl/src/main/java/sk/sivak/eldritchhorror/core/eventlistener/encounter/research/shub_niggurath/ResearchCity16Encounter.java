package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity16Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchCity16Encounter() {
        super(16, LocationType.CITY);
    }

    @Override
    protected void execute() {
       new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
           gainThisClue();
       }, () -> {
           ServicePlatform.get().getService().hold();
           ServicePlatform.get().getTokenService().loseHealth(1);
           ServicePlatform.get().getTokenService().loseSanity(1);
           ServicePlatform.get().getService().release();
       }).withResearchFlavor().withTwoFailInfos().execute();

    }
}
