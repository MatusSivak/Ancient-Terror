package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchSea3Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea3Encounter() {
        super(3, LocationType.SEA);
    }

    @Override
    protected void execute() {
       new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0, () -> {
           ServicePlatform.get().getService().hold();
           gainThisClue();
           ServicePlatform.get().getDoomOmenService().selectNewOmen();
           ServicePlatform.get().getService().release();
       }).withResearchFlavor().withTwoPassInfos().withoutPassFlavor().execute();
    }
}
