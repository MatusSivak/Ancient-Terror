package sk.sivak.eldritchhorror.core.constants.tracker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.ItemAlreadyOwnedException;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.*;

public class CustomPurchaseObserver implements PurchaseObserver {
    private List<Action0> handleInstallActionList = new LinkedList<>();
    private List<Action1<Throwable>> handleInstallErrorActionList = new LinkedList<>();
    private List<Action1<Transaction[]>> handleRestoreActionList = new LinkedList<>();
    private List<Action1<Throwable>> handleRestoreErrorActionList = new LinkedList<>();
    private List<Action1<Transaction>> handlePurchaseActionList = new LinkedList<>();
    private List<Action1<Throwable>> handlePurchaseErrorActionList = new LinkedList<>();
    private List<Action0> handlePurchaseCanceledActionList = new LinkedList<>();

    @Override
    public void handleInstall() {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(IN_APP_PURCHASE, "handleInstall");
        for (Action0 action : new LinkedList<>(handleInstallActionList)) {
            action.call();
        }
    }

    @Override
    public void handleInstallError(Throwable e) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(IN_APP_PURCHASE, "handleInstallError", e.toString());
        for (Action1<Throwable> action : new LinkedList<>(handleInstallErrorActionList)) {
            action.call(e);
        }
    }

    @Override
    public void handleRestore(Transaction[] transactions) {
        Collection<String> transactionIds = Stream.map(Stream.collectToList(Arrays.asList(transactions)), Transaction::getIdentifier);
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(IN_APP_PURCHASE, "handleRestore", transactionIds.toString());
        for (Action1<Transaction[]> action : new LinkedList<>(handleRestoreActionList)) {
            Gdx.app.postRunnable(() -> {
                action.call(transactions);
            });
        }
    }

    @Override
    public void handleRestoreError(Throwable e) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(IN_APP_PURCHASE, "handleRestoreError", e.toString());
        for (Action1<Throwable> action : new LinkedList<>(handleRestoreErrorActionList)) {
            action.call(e);
        }
    }

    @Override
    public void handlePurchase(Transaction transaction) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(IN_APP_PURCHASE, "handlePurchase", transaction.getIdentifier());
        for (Action1<Transaction> action : new LinkedList<>(handlePurchaseActionList)) {
            Gdx.app.postRunnable(() -> action.call(transaction));
        }
    }

    @Override
    public void handlePurchaseError(Throwable e) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(IN_APP_PURCHASE, "handlePurchaseError", e.toString());
        for (Action1<Throwable> action : new LinkedList<>(handlePurchaseErrorActionList)) {
            Gdx.app.postRunnable(() -> action.call(e));
        }
    }

    @Override
    public void handlePurchaseCanceled() {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(IN_APP_PURCHASE, "handlePurchaseCanceled");
        for (Action0 action : new LinkedList<>(handlePurchaseCanceledActionList)) {
            Gdx.app.postRunnable(action::call);
        }
    }

    public void addHandleInstallAction(Action0 handleInstallAction) {
        handleInstallActionList.add(handleInstallAction);
    }

    public void addHandleInstallErrorAction(Action1<Throwable> handleInstallErrorAction) {
        handleInstallErrorActionList.add(handleInstallErrorAction);
    }

    public void addHandleRestoreAction(Action1<Transaction[]> handleRestoreAction) {
        handleRestoreActionList.add(handleRestoreAction);
    }

    public void addHandleRestoreErrorAction(Action1<Throwable> handleRestoreErrorAction) {
        handleRestoreErrorActionList.add(handleRestoreErrorAction);
    }

    public void addHandlePurchaseAction(Action1<Transaction> handlePurchaseAction) {
        handlePurchaseActionList.add(handlePurchaseAction);
    }

    public void addHandlePurchaseErrorAction(Action1<Throwable> handlePurchaseErrorAction) {
        handlePurchaseErrorActionList.add(handlePurchaseErrorAction);
    }

    public void addHandlePurchaseCanceledAction(Action0 handlePurchaseCanceledAction) {
        handlePurchaseCanceledActionList.add(handlePurchaseCanceledAction);
    }

    ///////////////////////////////////////////////////////

    public void removeHandleInstallAction(Action0 handleInstallAction) {
        handleInstallActionList.remove(handleInstallAction);
    }

    public void removeHandleInstallErrorAction(Action1<Throwable> handleInstallErrorAction) {
        handleInstallErrorActionList.remove(handleInstallErrorAction);
    }

    public void removeHandleRestoreAction(Action1<Transaction[]> handleRestoreAction) {
        handleRestoreActionList.remove(handleRestoreAction);
    }

    public void removeHandleRestoreErrorAction(Action1<Throwable> handleRestoreErrorAction) {
        handleRestoreErrorActionList.remove(handleRestoreErrorAction);
    }

    public void removeHandlePurchaseAction(Action1<Transaction> handlePurchaseAction) {
        handlePurchaseActionList.remove(handlePurchaseAction);
    }

    public void removeHandlePurchaseErrorAction(Action1<Throwable> handlePurchaseErrorAction) {
        handlePurchaseErrorActionList.remove(handlePurchaseErrorAction);
    }

    public void removeHandlePurchaseCanceledAction(Action0 handlePurchaseCanceledAction) {
        handlePurchaseCanceledActionList.remove(handlePurchaseCanceledAction);
    }
}
