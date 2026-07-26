package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchSea14Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea14Encounter() {
        super(14, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.LEG_INJURY);
                    ServicePlatform.get().getService().release();
                }
        ).withTwoFailInfos().withResearchFlavor().execute();
    }
}
