package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity19Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity19Encounter() {
        super(19, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> ServicePlatform.get().getDoomOmenService().removeTokenFromOmenTrack(OmenId.NORTH),
                () -> {
                    int tokensCount = ServicePlatform.get().getOmenTrack().getOmenInfo(OmenId.NORTH).getTokensCount();
                    ServicePlatform.get().getTokenService().loseSanity(tokensCount * 2);
                }
        ).withResearchFlavor().execute();
    }
}
