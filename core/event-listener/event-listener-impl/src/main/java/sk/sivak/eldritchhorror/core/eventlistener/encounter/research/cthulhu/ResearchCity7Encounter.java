package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity7Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity7Encounter() {
        super(7, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.CULTIST, this::gainThisClue, () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.AMNESIA);
            moveClueToSea();
            ServicePlatform.get().getService().release();
        }).withTwoFailInfos().withoutPassFlavor().execute();
    }
}
