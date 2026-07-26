package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchSea6Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea6Encounter() {
        super(6, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getDoomOmenService().retreatDoom();
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.HALLUCINATIONS);
                }
        ).withResearchFlavor().withTwoPassInfos().execute();
    }
}
