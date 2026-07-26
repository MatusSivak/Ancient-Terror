package sk.sivak.eldritchhorror.core.action.impl.saveload;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

public class SaveGameAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            if (!GoogleServicesHolder.isTutorialPassed()) {
                onSub.onSuccess(null);
                return;
            }
            SaveDataWrite saveData = ServicePlatform.get().getModel().createSaveData();

            saveData.setMythosDeck(ServicePlatform.get().getMythosDeck().save());
            saveData.setAncientOne(ServicePlatform.get().getModel().getAncientOne().save());
            saveData.setArtifactsDeck(ServicePlatform.get().getArtifactsDeck().save());
            saveData.setAssetsDeck(ServicePlatform.get().getAssetsDeck().save());
            saveData.setConditionsDeck(ServicePlatform.get().getConditionsDeck().save());
            saveData.setSpellsDeck(ServicePlatform.get().getSpellsDeck().save());
            saveData.setCluePool(ServicePlatform.get().getCluePool().save());
            saveData.setVortexes(ServicePlatform.get().getVortexes().save());
            saveData.setDoomTrack(ServicePlatform.get().getDoomTrack().save());
            saveData.setExpeditionDeck(ServicePlatform.get().getExpeditionDeck().save());
            saveData.setGateStack(ServicePlatform.get().getGateStack().save());
            saveData.setModel(ServicePlatform.get().getModel().save());
            saveData.setMonsterCup(ServicePlatform.get().getMonsterCup().save());
            saveData.setMysteryDeck(ServicePlatform.get().getMysteryDeck().save());
            saveData.setRumors(ServicePlatform.get().getRumors().save());
            saveData.setOmenTrack(ServicePlatform.get().getOmenTrack().save());
            saveData.setPhase(ServicePlatform.get().getPhase().save());
            saveData.setInvestigators(ServicePlatform.get().getInvestigators().save());
            saveData.setUnlockedAssets(ServicePlatform.get().getInitGameController().getUnlockedAssets());
            saveData.setUnlockedArtifacts(ServicePlatform.get().getInitGameController().getUnlockedArtifacts());

            saveData.setGeneralEncounterDeck(ServicePlatform.get().getGeneralEncounterDeck().save());
            ServicePlatform.get().getInitGameController().save(saveData);

            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
