package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.pay.Transaction;
import rx.Single;
import rx.functions.Action0;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.firebase.FirebasePurchase;

public class InAppPurchaseManager {
    public static final String FULL_GAME = "full_game";

    private static void unlockFullGame(Preferences preferences) {
        for (String feature : new String[]{FULL_GAME, "no_ads", "investigators_1", "cthulhu", "shub_niggurath", "yog_sothoth"}) {
            preferences.putBoolean(feature, true);
        }
        preferences.flush();
        GoogleServicesHolder.overwriteUnlockedAssets(new java.util.ArrayList<>(java.util.Arrays.asList(
                sk.sivak.eldritchhorror.core.constants.asset.AssetId.values())));
        GoogleServicesHolder.overwriteUnlockedArtifacts(new java.util.ArrayList<>(java.util.Arrays.asList(
                sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId.values())));
    }


    public Single<Boolean> isProductPurchased(String productName) {
        final Action1<Transaction[]>[] restoreActionRef = new Action1[1];
        final Action1<Throwable>[] restoreErrorActionRef = new Action1[1];

        return Single.<Boolean>create(onSub -> {
            Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");

            if (preferences.getBoolean(FULL_GAME, false)) {
                unlockFullGame(preferences);
                onSub.onSuccess(true);
                return;
            }
            if (preferences.getBoolean(productName, false)) {
                onSub.onSuccess(preferences.getBoolean(productName, false));
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
                for (Transaction transaction : transactions) {
                    if (!productName.equals(transaction.getIdentifier()) || !transaction.isPurchased()) {
                        continue;
                    }
                    preferences.putBoolean(productName, true);
                    preferences.flush();
                    onSub.onSuccess(transaction.isPurchased());
                    return;
                }
                // Do not cache false — let the next launch re-verify with Google Play
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

    public Single<Boolean> purchaseProduct(String productName) {
        final Action1<Transaction>[] purchaseActionRef = new Action1[1];
        final Action0[] purchaseCanceledActionRef = new Action0[1];
        final Action1<Throwable>[] purchaseErrorActionRef = new Action1[1];

        return Single.<Boolean>create(onSub -> {
            if (Gdx.app.getType() == ApplicationType.Desktop && FULL_GAME.equals(productName)) {
                unlockFullGame(Gdx.app.getPreferences("AncientTerror.xml"));
                onSub.onSuccess(true);
                return;
            }
            if (GoogleServicesHolder.getCustomPurchaseObserver() == null || GoogleServicesHolder.getPurchaseManager() == null) {
                onSub.onError(new IllegalStateException("Store unavailable"));
                return;
            }
            purchaseActionRef[0] = transaction -> {
                if (!productName.equals(transaction.getIdentifier())) return;
                Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
                preferences.putBoolean(productName, transaction.isPurchased());
                preferences.flush();
                if (FULL_GAME.equals(productName) && transaction.isPurchased()) unlockFullGame(preferences);
                onSub.onSuccess(transaction.isPurchased());
                new FirebasePurchase().recordPurchase(productName);
            };

            purchaseCanceledActionRef[0] = () -> onSub.onSuccess(false);

            purchaseErrorActionRef[0] = onSub::onError;

            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseAction(purchaseActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseCanceledAction(purchaseCanceledActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseErrorAction(purchaseErrorActionRef[0]);

            try {
                GoogleServicesHolder.getPurchaseManager().purchase(productName);
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
