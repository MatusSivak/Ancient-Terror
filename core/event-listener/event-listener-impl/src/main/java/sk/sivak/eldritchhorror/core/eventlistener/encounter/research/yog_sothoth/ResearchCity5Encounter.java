package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity5Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity5Encounter() {
        super(5, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getGameService().gainSpell();
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getGameService().spawnGates(1, 1).subscribe();
                    ServicePlatform.get().getService().release();
                }
        ).withTwoPassInfos().withResearchFlavor().execute();
    }
}
