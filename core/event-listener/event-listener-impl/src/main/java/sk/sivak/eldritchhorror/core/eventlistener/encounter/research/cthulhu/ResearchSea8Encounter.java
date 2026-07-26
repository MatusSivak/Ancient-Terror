package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchSea8Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea8Encounter() {
        super(8, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build(),
                getTextBuilder().withInfo(3).build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            gainThisClue();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.DEEP_ONE).subscribe(combatData -> {
                ServicePlatform.get().getService().hold();
                if (!combatData.getMonsterInfo().isAlive()) {
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                    ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor().build());
                    TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getGameService().gainArtifact(ArtifactId.RUBY_OF_RLYEH);
                        ServicePlatform.get().getService().release();
                    });
                } else {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }
}
