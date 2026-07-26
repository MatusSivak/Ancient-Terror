package sk.sivak.eldritchhorror.core.action.impl.initgame;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.reference.ReferenceInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.GateSpawnData;
import sk.sivak.eldritchhorror.core.service.GameService;
import sk.sivak.eldritchhorror.core.service.InvestigatorService;
import sk.sivak.eldritchhorror.core.service.Service;
import sk.sivak.eldritchhorror.core.service.TokenService;

import java.util.List;

/**
 * @author msivak
 */
public class InitGameAction implements Action<Void, Void> {

    @Override
    public Single<Void> execute() {
        return Single.fromCallable(() -> {
            GoogleServicesHolder.getAnalyticsTracker().trackScreenName("Prepare Board");
            ServicePlatform servicePlatform = ServicePlatform.get();
            ReferenceInfo referenceCard = servicePlatform.getModel().getReferenceCard();
            Service service = servicePlatform.getService();
            TokenService tokenService = servicePlatform.getTokenService();
            GameService gameService = servicePlatform.getGameService();
            InvestigatorService investigatorService = servicePlatform.getInvestigatorService();

            ServicePlatform.get().getEventListenerProvider().registerCollectCommonEncountersListener();

            Completable showInvestigators = gameService.initInvestigators();
            Completable showExpeditionLocation = gameService.showExpeditionLocation();
            Single<List<ClueInfo>> spawnClues;
            Single<List<GateSpawnData>> spawnGates;
            if (!GoogleServicesHolder.isTutorialPassed()) {
                spawnClues = tokenService.spawnCluesQuick(referenceCard.getSpawnClues());
                spawnGates = gameService.spawnGatesQuick(referenceCard.getSpawnGates(), 1);
            } else {
                spawnClues = tokenService.spawnClues(referenceCard.getSpawnClues());
                spawnGates = gameService.spawnGates(referenceCard.getSpawnGates(), 1);
            }



            servicePlatform.getAssetsDeck().initReserve();


            service.hold();
            showInvestigators
                    .andThen(spawnClues).map(clues -> null)
                    .mergeWith(spawnGates)
                    .mergeWith(showExpeditionLocation.toObservable())
                    .subscribe();

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInitGameService().initAncientOne();
            ServicePlatform.get().getInitGameService().registerCurrentMysteryCard();
            ServicePlatform.get().getService().release();

            if (!GoogleServicesHolder.isTutorialPassed()) {
                ServicePlatform.get().getTutorialService().startTutorial();
                service.release();
                return null;
            }
            investigatorService.changeActiveInvestigator();
            gameService.changeAndShowPhase();
            investigatorService.showActiveInvestigator(true);

            gameService.initPhase();

            service.release();
            return null;
        });
    }

    @Override
    public void setInput(Void input) {
        // input is ignored
    }
}
