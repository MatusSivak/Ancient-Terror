package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity15Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity15Encounter() {
        super(15, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                this::gainThisClue,
                () -> {
                    OmenInfo currentOmen = ServicePlatform.get().getOmenTrack().getCurrentOmen();
                    ServicePlatform.get().getDoomOmenService().advanceDoomByCurrentOmen(currentOmen);
                }
        ).withoutPassFlavor().withResearchFlavor().execute();
    }
}
