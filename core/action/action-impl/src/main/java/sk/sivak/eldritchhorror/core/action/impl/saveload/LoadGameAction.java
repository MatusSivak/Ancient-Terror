package sk.sivak.eldritchhorror.core.action.impl.saveload;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadGameAction implements Action<Object, Object> {

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            SaveDataRead saveData = loadData();

            if (saveData == null) {
                onSub.onError(new IllegalStateException("Save file is missing or corrupt"));
                return;
            }

            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_STATE, "loaded");
            ServicePlatform.get().getInitGameController().rewriteUnlockedAssets(saveData.getUnlockedAssets());
            ServicePlatform.get().getInitGameController().rewriteUnlockedArtifacts(saveData.getUnlockedArtifacts());
            ServicePlatform.get().getGeneralEncounterDeck().init();
            ServicePlatform.get().getLocationEncounterDeck().init();
            ServicePlatform.get().getPerformedEncounters().reset();

            ServicePlatform.get().getTestFlavor().init();


            ServicePlatform.get().getModel().getBackgroundModel().init();
            ServicePlatform.get().getRumors().init();
            ServicePlatform.get().getEventQueue().init();
            ServicePlatform.get().getEventListenerProvider().init();
            ServicePlatform.get().getSpellListenerProvider().init();
            ServicePlatform.get().getConditionListenerProvider().init();
            ServicePlatform.get().getAssetListenerProvider().init();
            ServicePlatform.get().getArtifactListenerProvider().init();
            ServicePlatform.get().getActionListenerProvider().init();

            ServicePlatform.get().getEventListenerProvider().registerCollectCommonEncountersListener();
            ServicePlatform.get().getLocationMap().initLocations();

            ServicePlatform.get().getVortexes().load(saveData.getVortexes());
            ServicePlatform.get().getMythosDeck().load(saveData.getMythosDeck());
            ServicePlatform.get().getCluePool().load(saveData.getCluePool());
            ServicePlatform.get().getDoomTrack().load(saveData.getDoomTrack());
            ServicePlatform.get().getExpeditionDeck().load(saveData.getExpeditionDeck());
            ServicePlatform.get().getGateStack().load(saveData.getGateStack());
            ServicePlatform.get().getModel().load(saveData.getModel());
            ServicePlatform.get().getOmenTrack().load(saveData.getOmenTrack());
            ServicePlatform.get().getPhase().load(saveData.getPhase());

            LoadAncientOneHelper.loadAncientOne(saveData);
            LoadAssetsDeckHelper.loadAssetsDeck(saveData);
            LoadArtifactsDeckHelper.loadArtifactsDeck(saveData);
            LoadConditionsDeckHelper.loadConditionsDeck(saveData);
            LoadSpellsDeckHelper.loadSpellsDeck(saveData);
            LoadMysteryDeckHelper.loadMysteryDeck(saveData);
            LoadMonsteCupHelper.loadMonsterCup(saveData);
            LoadRumorsHelper.loadRumors(saveData);
            LoadInvestigatorsHelper.loadInvestigators(saveData);

            ServicePlatform.get().getInitGameController().toGameScreen();
            ServicePlatform.get().getGameController().setClearQueueAction(() -> ServicePlatform.get().getService().clearQueue());
            onSub.onSuccess(saveData);
        });
    }

    private SaveDataRead loadData() {
        try {
            return (SaveDataRead) ServicePlatform.get().getInitGameController().load(Class.forName("sk.sivak.eldritchhorror.core.model.save.SaveData"));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setInput(Object input) {

    }
}
