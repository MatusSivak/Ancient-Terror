package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness1Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness1Encounter() {
        super(1, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getDoomOmenService().selectNewOmen();
                    ServicePlatform.get().getService().release();
                },
                () -> ServicePlatform.get().getTokenService().loseHealth(1)
        ).withoutPassFlavor().withTwoPassInfos().withResearchFlavor().execute();
    }
}
