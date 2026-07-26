package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity3Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity3Encounter() {
        super(3, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    ServicePlatform.get().getDoomOmenService().advanceDoom();
                }
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
