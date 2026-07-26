package main.java.sk.sivak.eldritchhorror.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class JustRateThisGame {

    private static final String PREFS_NAME = "RateThisApp";
    private static final String KEY_INSTALL_DATE = "rta_install_date";
    private static final String KEY_LAUNCH_TIMES = "rta_launch_times";
    private static final String KEY_ASK_LATER_DATE = "rta_ask_later_date";
    private static final String KEY_OPT_OUT = "rta_opt_out";

    private static final long MIN_INSTALL_DURATION_MS = 24L * 60 * 60 * 1000;
    private static final long ASK_LATER_DELAY_MS = 3L * 24 * 60 * 60 * 1000;
    private static final int MIN_LAUNCH_TIMES = 3;

    private final Activity activity;
    private boolean gameRated = false;
    private boolean later = false;

    public JustRateThisGame(Activity activity) {
        this.activity = activity;
        SharedPreferences pref = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!pref.contains(KEY_INSTALL_DATE)) {
            editor.putLong(KEY_INSTALL_DATE, System.currentTimeMillis());
        }
        editor.putInt(KEY_LAUNCH_TIMES, pref.getInt(KEY_LAUNCH_TIMES, 0) + 1);
        editor.apply();
    }

    public boolean shouldShowRateDialog() {
        if (gameRated) {
            return false;
        }
        if (later) {
            return false;
        }
        SharedPreferences pref = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (pref.getBoolean(KEY_OPT_OUT, false)) {
            return false;
        }

        long now = System.currentTimeMillis();
        long installDate = pref.getLong(KEY_INSTALL_DATE, now);
        int launchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        long askLaterDate = pref.getLong(KEY_ASK_LATER_DATE, 0);

        if (launchTimes < MIN_LAUNCH_TIMES) {
            return false;
        }
        if (now - installDate < MIN_INSTALL_DURATION_MS) {
            return false;
        }
        return askLaterDate == 0 || now - askLaterDate >= ASK_LATER_DELAY_MS;
    }

    public void openStore() {
        activity.runOnUiThread(() -> {
            String appPackage = activity.getPackageName();
            String url = "market://details?id=" + appPackage;
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (android.content.ActivityNotFoundException anfe) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            }
            SharedPreferences pref = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(KEY_OPT_OUT, true);
            editor.apply();
            gameRated = true;
        });
    }

    public void askLater() {
        SharedPreferences pref = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(KEY_ASK_LATER_DATE, System.currentTimeMillis());
        later = true;
        editor.apply();
    }
}
