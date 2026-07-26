package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.pay.Transaction;
import rx.Single;
import rx.functions.Action0;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.firebase.FirebasePurchase;

public class InAppPurchaseManager {

    public Single<Boolean> isProductPurchased(String productName) {
        final Action1<Transaction[]>[] restoreActionRef = new Action1[1];
        final Action1<Throwable>[] restoreErrorActionRef = new Action1[1];

        return Single.<Boolean>create(onSub -> {
            Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");

            if (preferences.contains(productName)) {
                onSub.onSuccess(preferences.getBoolean(productName, false));
                return;
            }

            restoreActionRef[0] = transactions -> {
                for (Transaction transaction : transactions) {
                    if (!productName.equals(transaction.getIdentifier())) {
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
            purchaseActionRef[0] = transaction -> {
                Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
                preferences.putBoolean(productName, transaction.isPurchased());
                preferences.flush();
                onSub.onSuccess(transaction.isPurchased());
                new FirebasePurchase().recordPurchase(productName);
            };

            purchaseCanceledActionRef[0] = () -> onSub.onSuccess(false);

            purchaseErrorActionRef[0] = throwable -> onSub.onSuccess(false);

            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseAction(purchaseActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseCanceledAction(purchaseCanceledActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().addHandlePurchaseErrorAction(purchaseErrorActionRef[0]);

            try {
                GoogleServicesHolder.getPurchaseManager().purchase(productName);
            } catch (Exception e) {
                onSub.onSuccess(false);
            }

        }).doOnSuccess(value -> {
            GoogleServicesHolder.getCustomPurchaseObserver().removeHandlePurchaseAction(purchaseActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().removeHandlePurchaseCanceledAction(purchaseCanceledActionRef[0]);
            GoogleServicesHolder.getCustomPurchaseObserver().removeHandlePurchaseErrorAction(purchaseErrorActionRef[0]);
        });
    }
}
