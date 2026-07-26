package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

import java.util.List;

public class ResearchWilderness18Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness18Encounter() {
        super(18, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getDoomOmenService().removeTokenFromOmenTrack(OmenId.NORTH);
                    ServicePlatform.get().getService().release();
                },
                () -> ServicePlatform.get().getDoomOmenService().advanceOmen()
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
