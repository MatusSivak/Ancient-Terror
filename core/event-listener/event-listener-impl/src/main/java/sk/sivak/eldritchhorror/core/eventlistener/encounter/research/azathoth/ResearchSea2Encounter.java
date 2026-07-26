package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;

public class ResearchSea2Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea2Encounter() {
        super(2, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getService().showResearchBackground(LocationType.SEA);
            ServicePlatform.get().getService().release();
        }, new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0, () -> {
            ServicePlatform.get().getTokenService().loseHealth(2);
        }).withResearchFlavor()).execute();
    }
}
