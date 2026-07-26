package sk.sivak.eldritchhorror.core.constants.tracker;

import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import rx.Completable;

import java.util.concurrent.TimeUnit;

public class DummyPurchaseManager implements PurchaseManager {

    private PurchaseObserver observer;

    @Override
    public String storeName() {
        return null;
    }

    @Override
    public void install(PurchaseObserver observer, PurchaseManagerConfig config, boolean autoFetchInformation) {
        this.observer = observer;
    }

    @Override
    public boolean installed() {
        return true;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void purchase(String identifier) {
        Completable.timer(2, TimeUnit.SECONDS).subscribe(() -> {
            Transaction transaction = new Transaction();
            transaction.setIdentifier("whatever");
            observer.handlePurchase(transaction);
        });

    }

    @Override
    public void purchaseRestore() {

    }

    @Override
    public Information getInformation(String identifier) {
        return null;
    }
}
