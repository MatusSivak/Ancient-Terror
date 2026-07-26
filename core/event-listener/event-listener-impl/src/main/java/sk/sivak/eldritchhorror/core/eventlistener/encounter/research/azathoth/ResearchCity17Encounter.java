package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity17Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity17Encounter() {
        super(17, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                this::gainThisClue,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA)
        ).withResearchFlavor().execute();
    }
}
