package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchSea2Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea2Encounter() {
        super(2, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.HALLUCINATIONS);
                }
        ).withResearchFlavor().withTwoPassInfos().execute();
    }
}
