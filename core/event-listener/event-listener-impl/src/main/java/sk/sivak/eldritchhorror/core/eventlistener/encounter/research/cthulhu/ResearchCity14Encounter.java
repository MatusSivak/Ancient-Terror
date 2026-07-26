package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;

import java.util.LinkedList;
import java.util.List;

public class ResearchCity14Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity14Encounter() {
        super(14, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.DEEP_ONE, this::gainThisClue, null)
                .withIsCombatSuccessfulFunction(combatData -> combatData.getHorrorTestResult().isSuccessful())
                .withOnSuccessMessageFunction(monsterInfo -> "[#GOOD]You passed the Will test.[]")
                .withOnFailMessageFunction(monsterInfo -> "[#BAD]You didn't pass the Will test.[]")
                .execute();
    }
}
