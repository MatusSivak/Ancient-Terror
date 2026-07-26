package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea9Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea9Encounter() {
        super(9, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build(),
                getTextBuilder().withInfo(3).build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getTokenService().discardClue(getLocationId());
            ServicePlatform.get().getGameService().gainArtifact(ArtifactId.SWORD_OF_YHA_TALLA);
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.STAR_SPAWN).subscribe();
            ServicePlatform.get().getService().release();
        });
    }
}
