package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchCity10Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity10Encounter() {
        super(10, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -2,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getDoomOmenService().selectNewOmen();
                    ServicePlatform.get().getService().release();
                }
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
