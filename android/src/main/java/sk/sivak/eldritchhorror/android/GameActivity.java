package sk.sivak.eldritchhorror.android;

import android.os.Bundle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import main.java.sk.sivak.eldritchhorror.android.JustRateThisGame;
import sk.sivak.eldritchhorror.core.Game;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsTracker;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

public class GameActivity extends AndroidApplication {

    private Game game;
    private AnalyticsTracker analyticsTracker;
    private AndroidBillingPurchaseManager billingPurchaseManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = new Game();
        analyticsTracker = new AndroidFirebaseAnalyticsTracker(this);
        game.setAnalyticsTracker(analyticsTracker);

        JustRateThisGame justRateThisGame = new JustRateThisGame(this);
        GoogleServicesHolder.setOpenStoreAction(justRateThisGame::openStore);
        GoogleServicesHolder.setAskLaterAction(justRateThisGame::askLater);
        GoogleServicesHolder.setShouldShowRateDialogSupplier(justRateThisGame::shouldShowRateDialog);

        game.setAdHandler(new AndroidAdHandler(this));
        billingPurchaseManager = new AndroidBillingPurchaseManager(this);
        game.setPurchaseManager(billingPurchaseManager);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(game, config);
    }

    @Override
    protected void onDestroy() {
        if (billingPurchaseManager != null) {
            billingPurchaseManager.dispose();
        }
        super.onDestroy();
    }
}
