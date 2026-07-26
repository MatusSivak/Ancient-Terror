package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea18Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea18Encounter() {
        super(18, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> {
                    int tokensCount = ServicePlatform.get().getOmenTrack().getOmenInfo(OmenId.NORTH).getTokensCount();
                    ServicePlatform.get().getDoomOmenService().advanceDoom(tokensCount);
                }
        ).withResearchFlavor().execute();

    }
}
