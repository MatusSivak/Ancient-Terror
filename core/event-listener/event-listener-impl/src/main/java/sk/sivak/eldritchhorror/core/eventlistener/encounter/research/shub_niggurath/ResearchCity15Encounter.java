package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity15Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchCity15Encounter() {
        super(15, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.GOAT_SPAWN, this::gainThisClue, null)
                .withIsCombatSuccessfulFunction(combatData -> combatData.getHorrorTestResult().isSuccessful())
                .withOnSuccessMessageFunction(monsterInfo -> "[#GOOD]You passed the Will test.[]")
                .withOnFailMessageFunction(monsterInfo -> "[#BAD]You didn't pass the Will test.[]")
                .execute();

    }
}
