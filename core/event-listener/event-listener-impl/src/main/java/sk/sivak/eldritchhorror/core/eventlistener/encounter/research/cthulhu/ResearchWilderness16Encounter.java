package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;

public class ResearchWilderness16Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness16Encounter() {
        super(16, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().hideBackground();
            discardOneVortex();
            ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
            ServicePlatform.get().getService().showResearchBackground(LocationType.WILDERNESS);
            ServicePlatform.get().getService().release();
        }, new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -2, () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.BACK_INJURY);
            ServicePlatform.get().getService().release();
        }).withTwoFailInfos().withResearchFlavor()
        ).execute();
    }
}
