package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness7Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness7Encounter() {
        super(7, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getDoomOmenService().retreatDoom();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.BACK_INJURY);
        }).withResearchFlavor().withTwoPassInfos().execute();
    }

}
