package sk.sivak.eldritchhorror.core.action.impl.initgame;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

/**
 * @author msivak
 */
public class BasicInitAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.fromCallable(() -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_STATE, "restarted");
            ServicePlatform servicePlatform = ServicePlatform.get();
            servicePlatform.getEventQueue().init();
            servicePlatform.getEventListenerProvider().init();
            servicePlatform.getSpellListenerProvider().init();
            servicePlatform.getConditionListenerProvider().init();
            servicePlatform.getAssetListenerProvider().init();
            servicePlatform.getArtifactListenerProvider().init();
            servicePlatform.getActionListenerProvider().init();

            servicePlatform.getInvestigators().initAvailableInvestigators(servicePlatform.getInitGameController().hasPurchasedBonusInvestigators());
            servicePlatform.getModel().initAvailableAncientOnes();
            servicePlatform.getRumors().init();
            servicePlatform.getLocationMap().initLocations();
            servicePlatform.getCluePool().initCluePool();
            servicePlatform.getVortexes().init();
            servicePlatform.getAssetsDeck().createDeck();
            servicePlatform.getArtifactsDeck().createDeck();
            servicePlatform.getConditionsDeck().createDeck();
            servicePlatform.getSpellsDeck().createDeck();
            servicePlatform.getGateStack().initGateStack();
            servicePlatform.getOmenTrack().initOmenTrack();
            servicePlatform.getExpeditionDeck().initExpeditionDeck();
            servicePlatform.getMonsterCup().createMonsterCup();
            servicePlatform.getPerformedActions().clearPerformedActionsMap();
            servicePlatform.getGeneralEncounterDeck().init();
            servicePlatform.getLocationEncounterDeck().init();
            servicePlatform.getPerformedEncounters().reset();
            servicePlatform.getModel().init();
            servicePlatform.getPhase().init();
            servicePlatform.getTestFlavor().init();
            servicePlatform.getModel().getBackgroundModel().init();
            return null;
        });
    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
