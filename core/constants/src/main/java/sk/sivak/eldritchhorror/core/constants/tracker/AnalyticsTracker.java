package sk.sivak.eldritchhorror.core.constants.tracker;

public interface AnalyticsTracker {
    void trackScreenName(String screenName);
    void trackInteraction(AnalyticsCategory category, String action);
    void trackInteraction(AnalyticsCategory category, String action, String label);
    void trackNonInteraction(AnalyticsCategory category, String action);
    void trackNonInteraction(AnalyticsCategory category, String action, String label);
    void trackTiming(AnalyticsCategory category, String name, Long value);

    void uncaughtException(Thread thread, Throwable throwable);
}
