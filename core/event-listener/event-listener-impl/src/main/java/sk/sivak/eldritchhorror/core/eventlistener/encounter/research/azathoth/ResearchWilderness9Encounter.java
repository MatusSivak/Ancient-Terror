package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness9Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness9Encounter() {
        super(9, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getDoomOmenService().removeTokenFromOmenTrack(OmenId.NORTH);
                    ServicePlatform.get().getService().release();
                },
                () -> ServicePlatform.get().getTokenService().loseSanity(2)
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
