package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

public class EndTutorialAction implements Action<Object, Object> {

    private final long startTime;

    public EndTutorialAction(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {

            int durationInSeconds = (int)(System.currentTimeMillis()-startTime)/1000;
            int minutes = (durationInSeconds) / 60;
            int seconds = (durationInSeconds) % 60;

            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.TUTORIAL, "duration",
                    minutes+"m"+seconds+"s");
            GoogleServicesHolder.executeTutorialPassedAction();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTutorialService().waitFewMilliseconds(1000);

            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getGameController().restartGame() ;
            });
            ServicePlatform.get().getService().release();

            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
