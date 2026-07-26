package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;

public class ResearchCity1Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity1Encounter() {
        super(1, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getService().release();
        }).withResearchFlavor().withTwoPassInfos().withoutPassFlavor().execute();
    }
}
