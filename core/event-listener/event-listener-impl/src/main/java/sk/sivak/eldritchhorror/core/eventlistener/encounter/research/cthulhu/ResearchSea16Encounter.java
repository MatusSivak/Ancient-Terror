package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
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

public class ResearchSea16Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea16Encounter() {
        super(16, LocationType.SEA);
    }

    @Override
    protected void execute() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        TestPassFlavorInfoFailFlavorInfoTemplate innerTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0, () -> {
            gainThisClue();
        }, () -> {
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.STAR_SPAWN).subscribe();
        }).withResearchFlavor();

        if (ServicePlatform.get().getVortexes().isAtLocation(currentLocationId)) {
            new InfoFlavorTemplate(getTextBuilder(), this::discardImprovementTokens,
                    innerTemplate).execute();
        } else {
            ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFlavor(2).build());
            innerTemplate.execute();
        }
    }

    private void discardImprovementTokens() {
        List<Stat> improvedSkills = new LinkedList<>();
        for (Stat stat : Stat.values()) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().getStatBonus(stat) == 0) {
                continue;
            }
            improvedSkills.add(stat);
        }

        if (!improvedSkills.isEmpty()) {
            for (Stat improvedSkill : improvedSkills) {
                ServicePlatform.get().getInvestigatorService().loseImprovement(new LoseImprovementData(improvedSkill, 2));
            }
        }
    }
}
