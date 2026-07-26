package sk.sivak.eldritchhorror.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsTracker;

import java.util.Locale;

public class AndroidFirebaseAnalyticsTracker implements AnalyticsTracker {

    private static final String EVENT_INTERACTION = "interaction";
    private static final String EVENT_NON_INTERACTION = "non_interaction";
    private static final String EVENT_TIMING = "timing";
    private static final String EVENT_EXCEPTION = "uncaught_exception";

    private final FirebaseAnalytics firebaseAnalytics;

    public AndroidFirebaseAnalyticsTracker(Activity activity) {
        if (!hasManualFirebaseConfig() && TextUtils.isEmpty(activity.getString(R.string.google_app_id))) {
            firebaseAnalytics = null;
            return;
        }
        FirebaseApp firebaseApp = FirebaseApp.getApps(activity).isEmpty() ? FirebaseApp.initializeApp(activity) : FirebaseApp.getInstance();
        if (firebaseApp == null && hasManualFirebaseConfig()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_API_KEY)
                    .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                    .setGcmSenderId(BuildConfig.FIREBASE_GCM_SENDER_ID)
                    .build();
            firebaseApp = FirebaseApp.initializeApp(activity, options);
        }
        firebaseAnalytics = firebaseApp != null ? FirebaseAnalytics.getInstance(activity) : null;
    }

    @Override
    public void trackScreenName(String screenName) {
        if (firebaseAnalytics == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, normalizeValue(screenName));
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "GameActivity");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    @Override
    public void trackInteraction(AnalyticsCategory category, String action) {
        trackInteraction(category, action, "");
    }

    @Override
    public void trackInteraction(AnalyticsCategory category, String action, String label) {
        if (firebaseAnalytics == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("category", category.name().toLowerCase(Locale.ROOT));
        bundle.putString("action", normalizeValue(action));
        bundle.putString("label", normalizeValue(label));
        firebaseAnalytics.logEvent(EVENT_INTERACTION, bundle);
    }

    @Override
    public void trackNonInteraction(AnalyticsCategory category, String action) {
        trackNonInteraction(category, action, "");
    }

    @Override
    public void trackNonInteraction(AnalyticsCategory category, String action, String label) {
        if (firebaseAnalytics == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("category", category.name().toLowerCase(Locale.ROOT));
        bundle.putString("action", normalizeValue(action));
        bundle.putString("label", normalizeValue(label));
        firebaseAnalytics.logEvent(EVENT_NON_INTERACTION, bundle);
    }

    @Override
    public void trackTiming(AnalyticsCategory category, String name, Long value) {
        if (firebaseAnalytics == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("category", category.name().toLowerCase(Locale.ROOT));
        bundle.putString("name", normalizeValue(name));
        if (value != null) {
            bundle.putLong("value", value);
        }
        firebaseAnalytics.logEvent(EVENT_TIMING, bundle);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (firebaseAnalytics != null && throwable != null) {
            Bundle bundle = new Bundle();
            bundle.putString("thread", normalizeValue(thread != null ? thread.getName() : "unknown"));
            bundle.putString("error", normalizeValue(throwable.toString()));
            firebaseAnalytics.logEvent(EVENT_EXCEPTION, bundle);
        }
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        }
    }

    private static boolean hasManualFirebaseConfig() {
        return !TextUtils.isEmpty(BuildConfig.FIREBASE_APP_ID)
                && !TextUtils.isEmpty(BuildConfig.FIREBASE_API_KEY)
                && !TextUtils.isEmpty(BuildConfig.FIREBASE_PROJECT_ID)
                && !TextUtils.isEmpty(BuildConfig.FIREBASE_GCM_SENDER_ID);
    }

    private static String normalizeValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > 100) {
            return value.substring(0, 100);
        }
        return value;
    }
}
