package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.List;

public class ResearchWilderness11Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness11Encounter() {
        super(11, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    discardOneVortex();
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().loseHealth(2);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.LEG_INJURY);
                    ServicePlatform.get().getService().release();
                }
        ).withResearchFlavor().withTwoPassInfos().withTwoFailInfos().execute();
    }
}
