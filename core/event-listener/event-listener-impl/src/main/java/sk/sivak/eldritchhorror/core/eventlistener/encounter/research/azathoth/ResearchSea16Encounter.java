package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchSea16Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea16Encounter() {
        super(16, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1, () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getTokenService().discardClue(getLocationId());
            ServicePlatform.get().getDoomOmenService().selectNewOmen();
            ServicePlatform.get().getService().release();
        }
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
