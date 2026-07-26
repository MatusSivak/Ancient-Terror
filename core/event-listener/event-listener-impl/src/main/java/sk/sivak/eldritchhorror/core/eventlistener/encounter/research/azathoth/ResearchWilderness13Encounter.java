package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness13Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness13Encounter() {
        super(13, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.INTERNAL_INJURY);
                    ServicePlatform.get().getService().release();
                }
        ).withTwoFailInfos().withResearchFlavor().execute();
    }
}
