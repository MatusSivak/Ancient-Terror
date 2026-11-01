package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.pay.Transaction;
import rx.Single;
import rx.functions.Action0;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.LocalTesting;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.firebase.FirebasePurchase;

import java.util.ArrayList;
import java.util.Arrays;

/** The game has a single in-app product: {@link #FULL_GAME}, which unlocks all premium content and removes ads. */
public class InAppPurchaseManager {
    public static final String FULL_GAME = "full_game";
    private static final String PREFERENCES = "AncientTerror.xml";

    public static boolean isFullGamePurchased() {
        return Gdx.app.getPreferences(PREFERENCES).getBoolean(FULL_GAME, false);
    }

    public static boolean areBonusInvestigatorsAvailable() {
        return LocalTesting.areAllInvestigatorsUnlocked() || isFullGamePurchased();
    }

    private static void unlockFullGame(Preferences preferences) {
        preferences.putBoolean(FULL_GAME, true);
        preferences.flush();
        GoogleServicesHolder.overwriteUnlockedAssets(new ArrayList<>(Arrays.asList(AssetId.values())));
        GoogleServicesHolder.overwriteUnlockedArtifacts(new ArrayList<>(Arrays.asList(ArtifactId.values())));
    }

    /** Resolves to true when the full game is owned, restoring it from the store if it is not cached locally. */
    public Single<Boolean> restoreFullGame() {
        final Action1<Transaction[]>[] restoreActionRef = new Action1[1];
        final Action1<Throwable>[] restoreErrorActionRef = new Action1[1];

        return Single.<Boolean>create(onSub -> {
            Preferences preferences = Gdx.app.getPreferences(PREFERENCES);

            if (preferences.getBoolean(FULL_GAME, false)) {
                unlockFullGame(preferences);
                onSub.onSuccess(true);
                return;
            }

            if (LocalTesting.useLocalPurchases()) {
                onSub.onSuccess(false);
                return;
            }

            restoreActionRef[0] = transactions -> {
                for (Transaction transaction : transactions) {
                    if (FULL_GAME.equals(transaction.getIdentifier()) && transaction.isPurchased()) {
                        unlockFullGame(preferences);
                        onSub.onSuccess(true);
                        return;
                    }
                }
                // Do not cache false — let the next launch re-verify with the store
                onSub.onSuccess(false);
            };

            restoreErrorActionRef[0] = error -> onSub.onSuccess(false); // do not cache on error

            if (GoogleServicesHolder.getCustomPurchaseObserver() != null) {
                GoogleServicesHolder.getCustomPurchaseObserver().addHandleRestoreAction(restoreActionRef[0]);
                GoogleServicesHolder.getCustomPurchaseObserver().addHandleRestoreErrorAction(restoreErrorActionRef[0]);
            }

            try {
                GoogleServicesHolder.getPurchaseManager().purchaseRestore();
            } catch (Exception e) {
                onSub.onSuccess(false);
            }

        }).doOnSuccess(value -> {
            if (GoogleServicesHolder.getCustomPurchaseObserver() != null) {
                GoogleServicesHolder.getCustomPurchaseObserver().removeHandleRestoreAction(restoreActionRef[0]);
                GoogleServicesHolder.getCustomPurchaseObserver().removeHandleRestoreErrorAction(restoreErrorActionRef[0]);
            }
        });
    }

    public Single<Boolean> purchaseFullGame() {
        final Action1<Transaction>[] purchaseActionRef = new Action1[1];
        final Action0[] purchaseCanceledActionRef = new Action0[1];
        final Action1<Throwable>[] purchaseErrorActionRef = new Action1[1];

        return Single.<Boolean>create(onSub -> {
            if (LocalTesting.useLocalPurchases() || Gdx.app.getType() == ApplicationType.Desktop) {
                unlockFullGame(Gdx.app.getPreferences(PREFERENCES));
                onSub.onSuccess(true);
                return;
            }
            if (GoogleServicesHolder.getCustomPurchaseObserver() == null || GoogleServicesHolder.getPurchaseManager() == null) {
                onSub.onError(new IllegalStateException("Store unavailable"));
                return;
            }
            purchaseActionRef[0] = transaction -> {
                if (!FULL_GAME.equals(transaction.getIdentifier())) return;
                if (transaction.isPurchased()) unlockFullGame(Gdx.app.getPreferences(PREFERENCES));
                onSub.onSuccess(transaction.isPurchased());
                new FirebasePurchase().recordPurchase(FULL_GAME);
            };

            purchaseCanceledActionRef[0] = () -> onSub.onSuccess(false);

            purchaseErrorActionRef[0] = onSub::onError;

            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseAction(purchaseActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseCanceledAction(purchaseCanceledActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseErrorAction(purchaseErrorActionRef[0]);

            try {
                GoogleServicesHolder.getPurchaseManager().purchase(FULL_GAME);
            } catch (Exception e) {
                onSub.onError(e);
            }

        }).doAfterTerminate(() -> {
            if (GoogleServicesHolder.getCustomPurchaseObserver() == null) return;
            GoogleServicesHolder.getCustomPurchaseObserver().removeHandlePurchaseAction(purchaseActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().removeHandlePurchaseCanceledAction(purchaseCanceledActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().removeHandlePurchaseErrorAction(purchaseErrorActionRef[0]);
        });
    }
}
