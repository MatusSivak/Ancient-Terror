package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;

import java.util.LinkedList;
import java.util.List;

public class ResearchCity13Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity13Encounter() {
        super(13, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -1,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getGameService().hold();
                    ServicePlatform.get().getTokenService().loseSanity(1);
                    discardImprovementTokens();
                    ServicePlatform.get().getGameService().release();
                }
        ).withTwoFailInfos().withResearchFlavor().execute();
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
