package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea1Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea1Encounter() {
        super(1, LocationType.SEA);
    }

    @Override
    protected void execute() {
        /*
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true));
                }
        ).withResearchFlavor().execute();
        */

        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0, () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true));
        }).withTwoPassInfos().withoutPassFlavor().withoutFailFlavor().withResearchFlavor().execute();
    }
}
