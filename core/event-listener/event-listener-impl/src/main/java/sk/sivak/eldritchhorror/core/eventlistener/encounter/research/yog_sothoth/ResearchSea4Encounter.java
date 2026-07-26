package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchSea4Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea4Encounter() {
        super(4, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getGameService().gainSpell();
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getService().release();
                }
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
