package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchWilderness5Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness5Encounter() {
        super(5, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getService().showResearchBackground(LocationType.WILDERNESS);
            ServicePlatform.get().getService().release();
        }, new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> ServicePlatform.get().getDoomOmenService().removeTokenFromOmenTrack(OmenId.NORTH)
        ).withResearchFlavor()).execute();
    }
}
