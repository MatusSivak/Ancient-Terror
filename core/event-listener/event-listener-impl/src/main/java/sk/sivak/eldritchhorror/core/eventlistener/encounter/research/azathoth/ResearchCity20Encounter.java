package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchCity20Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity20Encounter() {
        super(20, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getDoomOmenService().addTokenToOmenTrack(OmenId.NORTH);
                    ServicePlatform.get().getService().release();
                }
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
