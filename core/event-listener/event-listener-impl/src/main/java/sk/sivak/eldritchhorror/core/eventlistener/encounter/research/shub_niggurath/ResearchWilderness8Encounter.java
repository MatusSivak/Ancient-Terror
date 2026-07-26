package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness8Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness8Encounter() {
        super(8, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, () -> {
            gainThisClue();
        }, () -> {
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.GOAT_SPAWN).subscribe();
        }).withResearchFlavor().withoutFailFlavor().execute();
    }

}
