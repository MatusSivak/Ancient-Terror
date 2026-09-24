package sk.sivak.eldritchhorror.android;

import android.os.Bundle;
import android.os.Process;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import main.java.sk.sivak.eldritchhorror.android.JustRateThisGame;
import sk.sivak.eldritchhorror.core.Game;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsTracker;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

public class GameActivity extends AndroidApplication {

    /**
     * libGDX keeps a lot of state in static singletons bound to the GL context of the first
     * activity. A second activity instance in the same process (activity re-created by the
     * system, or reopened after back) would render a black screen, so we restart the process.
     */
    private static boolean gameCreatedInProcess;

    private boolean restarting;
    private Game game;
    private AnalyticsTracker analyticsTracker;
    private AndroidBillingPurchaseManager billingPurchaseManager;
    private AndroidAdHandler adHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Prevent Log4j auto-property configuration on Android (requires java.beans).
        System.setProperty("log4j.defaultInitOverride", "true");
        super.onCreate(savedInstanceState);

        if (gameCreatedInProcess) {
            restarting = true;
            ProcessRestartActivity.restart(this);
            return;
        }
        gameCreatedInProcess = true;

        game = new Game();
        analyticsTracker = new AndroidFirebaseAnalyticsTracker(this);
        game.setAnalyticsTracker(analyticsTracker);

        JustRateThisGame justRateThisGame = new JustRateThisGame(this);
        GoogleServicesHolder.setOpenStoreAction(justRateThisGame::openStore);
        GoogleServicesHolder.setAskLaterAction(justRateThisGame::askLater);
        GoogleServicesHolder.setShouldShowRateDialogSupplier(justRateThisGame::shouldShowRateDialog);

        adHandler = new AndroidAdHandler(this);
        game.setAdHandler(adHandler);
        billingPurchaseManager = new AndroidBillingPurchaseManager(this);
        game.setPurchaseManager(billingPurchaseManager);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(game, config);
        adHandler.initialize();
    }

    @Override
    protected void onDestroy() {
        if (adHandler != null) {
            adHandler.dispose();
            adHandler = null;
        }
        if (billingPurchaseManager != null) {
            billingPurchaseManager.dispose();
        }
        super.onDestroy();
        if (!restarting && isFinishing() && !isChangingConfigurations()) {
            // Game.dispose() already ran; end the process so the next launch starts clean.
            Process.killProcess(Process.myPid());
        }
    }
}
