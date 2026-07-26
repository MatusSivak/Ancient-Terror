package sk.sivak.eldritchhorror.core.action.impl.saveload;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadViewAction implements Action<Object, Void> {

    private SaveDataRead saveData;

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {

            ServicePlatform.get().getInitGameController().rewriteUnlockedAssets(saveData.getUnlockedAssets());
            ServicePlatform.get().getInitGameController().rewriteUnlockedArtifacts(saveData.getUnlockedArtifacts());

            // Spawn Clues
            for (ClueInfo spawnedClue : ServicePlatform.get().getCluePool().getSpawnedClues()) {
                ServicePlatform.get().getGameController().justSpawnClue(
                        spawnedClue.getSpawnLocationId(), spawnedClue.getCurrentLocationId());
            }

            for (LocationId spawnedVortex : ServicePlatform.get().getVortexes().getSpawnedVortexes()) {
                ServicePlatform.get().getGameController().justAddVortex(spawnedVortex);
            }

            // Omen & Doom Track
            for (OmenId omenId : OmenId.values()) {
                int tokensCount = ServicePlatform.get().getOmenTrack().getOmenInfo(omenId).getTokensCount();
                ServicePlatform.get().getDoomOmenController().justAddTokensToOmenTrack(omenId, tokensCount);
            }

            // Expedition
            ServicePlatform.get().getGameController().justPlaceExpeditionToken();

            // Gates
            for (GateInfo spawnedGate : ServicePlatform.get().getGateStack().getSpawnedGates()) {
                ServicePlatform.get().getGameController().justPlaceGate(spawnedGate);
            }

            // Monsters
            for (MonsterInfo monster : ServicePlatform.get().getMonsterCup().getMonsters()) {
                ServicePlatform.get().getGameController().justPlaceMonster(monster);
            }

            // Investigators
            for (InvestigatorWrite investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                ServicePlatform.get().getGameController().justPlaceInvestigator(investigator.getInfo().getInvestigatorId(), investigator.getCurrentLocationId());
                if (ServicePlatform.get().getInvestigators().getInvestigator(investigator.getInfo().getInvestigatorId()).isDelayed()) {
                    ServicePlatform.get().getGameController().justAddDelayedAnimation(investigator.getInfo().getInvestigatorId());
                }
            }
            for (InvestigatorsSaveDataRead.DefeatedInvestigatorSaveDataRead defeatedInvestigator : saveData.getInvestigators().getDefeatedInvestigators()) {
                ServicePlatform.get().getGameController().loadDefeatedInvestigator(
                        defeatedInvestigator.getInvestigatorId(),defeatedInvestigator.getLocationId(), defeatedInvestigator.getDefeatedByHealth()).subscribe(() -> {
                });
            }

            //Discarded assets
            if (!ServicePlatform.get().getAssetsDeck().getDiscardPile().isEmpty()) {
                ServicePlatform.get().getGameController().enableDiscardButton();
            }

            // Rumors
            for (RumorCardInfo activeRumor : ServicePlatform.get().getRumors().getActiveRumors()) {
                if (activeRumor.isStormSpawned()) {
                    ServicePlatform.get().getGameController().justSpawnStorm(activeRumor.getId(), activeRumor.getRumorLocation());
                }
            }
            ServicePlatform.get().getGameController().updateRumorButtonVisibility();

            initPhase();
            onSub.onSuccess(null);


        });
    }

    private void initPhase() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEventListenerProvider().justAddRedPins(ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard());
        ServicePlatform.get().getInitGameService().registerBasicActions();
        ServicePlatform.get().getGameService().showPhase();
        if (ServicePlatform.get().getPhase().getPhaseType() == PhaseType.ACTION) {
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        }
        ServicePlatform.get().getGameService().initPhase();
        ServicePlatform.get().getService().release();
    }

    @Override
    public void setInput(Object input) {
        saveData = ((SaveDataRead) input);
    }
}
