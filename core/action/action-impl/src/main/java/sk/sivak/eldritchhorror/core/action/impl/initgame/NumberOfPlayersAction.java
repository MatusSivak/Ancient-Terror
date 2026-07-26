package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.SELECT_INVESTIGATORS;

/**
 * @author msivak
 */
public class NumberOfPlayersAction implements Action<Void, Integer> {

    private static final Logger logger = LogManager.getLogger(NumberOfPlayersAction.class);

    @Override
    public Single<Integer> execute() {
        if (!GoogleServicesHolder.isTutorialPassed()) {
            return Single.create(onSub -> {
                ServicePlatform.get().getAssetsDeck().removeTeamworkAssets();
                ServicePlatform.get().getArtifactsDeck().removeTeamworkArtifacts();
                ServicePlatform.get().getInvestigators().initWithPlayers(1);
                ServicePlatform.get().getModel().initReferenceCard(1);
                onSub.onSuccess(1);
            });
        }
        GoogleServicesHolder.getAnalyticsTracker().trackScreenName("Select Investigators");
        Single<Integer> numberOfPlayers = ServicePlatform.get().getInitGameController().getNumberOfPlayers().cache();
        numberOfPlayers.subscribe(num -> {
            if (num == 1) {
                ServicePlatform.get().getAssetsDeck().removeTeamworkAssets();
                ServicePlatform.get().getArtifactsDeck().removeTeamworkArtifacts();
            }
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(SELECT_INVESTIGATORS, "count", String.valueOf(num));
            ServicePlatform.get().getInvestigators().initWithPlayers(num);
            ServicePlatform.get().getModel().initReferenceCard(num);
            logger.debug("Total players: " + num);
        });
        return numberOfPlayers;
    }

    @Override
    public void setInput(Void input) {
        // input is ignored
    }
}
