package sk.sivak.eldritchhorror.core.util;

import java8.features.function.Consumer;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

public class PlayTimeTracker implements Consumer<Float> {

    private static final int INTERVAL = 30;
    private float total;
    private int count = 0;

    @Override
    public void accept(Float delta) {
        total += delta;
        if (total % INTERVAL < 0) {
            return;
        }
        total -= INTERVAL;
        count++;

        int minutes = (count * INTERVAL) / 60;
        int seconds = (count * INTERVAL) % 60;

        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(
                AnalyticsCategory.PLAY_TIME, "playTimeDuration", minutes+"m"+seconds+"s");

    }
}
