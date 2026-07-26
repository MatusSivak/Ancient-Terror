package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchWilderness21Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness21Encounter() {
        super(21, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getDoomOmenService().selectNewOmen();
                    ServicePlatform.get().getService().release();
                }
        ).withResearchFlavor().execute();
    }
}
