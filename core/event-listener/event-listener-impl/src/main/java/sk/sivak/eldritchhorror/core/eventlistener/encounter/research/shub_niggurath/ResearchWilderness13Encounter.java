package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class ResearchWilderness13Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness13Encounter() {
        super(13, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getService().hideBackground();
            gainThisClue();

            SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
            spawnMonsterData.setLocationId(getLocationId());
            if (ServicePlatform.get().getMonsterCup().canSpawn(NonEpicMonsterId.DARK_YOUNG)) {
                spawnMonsterData.setMonsterId(NonEpicMonsterId.DARK_YOUNG);
            }
            ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
            ServicePlatform.get().getService().<SpawnMonsterData>addEventCommand(smd -> {
                ServicePlatform.get().getTestService().combat(smd.getMonsterInfo());
            });
            ServicePlatform.get().getService().<CombatData>addEventCommand(combatData -> {
                if (combatData.getMonsterInfo().isAlive() ) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                    TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getTokenService().gainClueFromPool();
                        ServicePlatform.get().getService().release();
                    });
                    ServicePlatform.get().getService().release();
                }
            });

            ServicePlatform.get().getService().release();
        });
    }

}
