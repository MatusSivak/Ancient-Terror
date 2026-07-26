package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea14Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea14Encounter() {
        super(14, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.BACK_INJURY);
                    ServicePlatform.get().getService().release();
                }
        ).withTwoFailInfos().withResearchFlavor().execute();
    }
}
