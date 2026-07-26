package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity12Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity12Encounter() {
        super(12, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                this::gainThisClue,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.AMNESIA)
        ).withResearchFlavor().execute();
    }
}
