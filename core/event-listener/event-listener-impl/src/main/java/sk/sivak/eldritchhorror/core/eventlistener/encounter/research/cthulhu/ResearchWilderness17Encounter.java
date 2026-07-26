package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness17Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness17Encounter() {
        super(17, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            gainThisClue();
        }, () -> {
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.DEEP_ONE).subscribe();
        }).withResearchFlavor().execute();
    }
}
