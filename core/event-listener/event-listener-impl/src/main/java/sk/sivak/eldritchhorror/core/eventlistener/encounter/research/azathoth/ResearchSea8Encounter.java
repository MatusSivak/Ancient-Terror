package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchSea8Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea8Encounter() {
        super(8, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getService().showResearchBackground(LocationType.SEA);
            ServicePlatform.get().getService().release();
        }, new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0, () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getDoomOmenService().selectNewOmen();
            ServicePlatform.get().getService().release();
        }).withoutPassFlavor()
        ).execute();
    }
}
