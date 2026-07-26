package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchSea3Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea3Encounter() {
        super(3, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getGameService().gainArtifact(ArtifactId.GROTESQUE_STATUE);
                },
                () -> {
                    ServicePlatform.get().getTokenService().hold();
                    ServicePlatform.get().getTokenService().loseSanity(3);
                    ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.DEEP_ONE).subscribe();
                    ServicePlatform.get().getTokenService().release();
                }
        ).withResearchFlavor().withoutFailFlavor().withTwoFailInfos().execute();
    }
}
