package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness20Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness20Encounter() {
        super(20, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::gainThisClue,
                () -> {
                    int tokensCount = ServicePlatform.get().getOmenTrack().getOmenInfo(OmenId.NORTH).getTokensCount();
                    ServicePlatform.get().getTokenService().loseHealth(tokensCount * 2);
                }
        ).withoutPassFlavor().withResearchFlavor().execute();
    }
}
