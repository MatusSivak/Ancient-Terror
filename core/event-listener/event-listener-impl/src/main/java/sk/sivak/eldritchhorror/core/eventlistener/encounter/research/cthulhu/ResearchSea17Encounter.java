package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;

import java.util.LinkedList;
import java.util.List;

public class ResearchSea17Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea17Encounter() {
        super(17, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.STAR_SPAWN).subscribe();
        }).withTwoPassInfos().withResearchFlavor().execute();
    }
}
