package sk.sivak.eldritchhorror.android;

import android.app.Activity;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AndroidBillingPurchaseManager implements PurchaseManager, PurchasesUpdatedListener, BillingClientStateListener {

    private static final String PLAY_STORE_NAME = "GooglePlay";

    private final Activity activity;
    private final Map<String, ProductDetails> productDetailsById = new LinkedHashMap<>();

    private BillingClient billingClient;
    private PurchaseObserver observer;
    private boolean installed;

    public AndroidBillingPurchaseManager(Activity activity) {
        this.activity = activity;
    }

    @Override
    public String storeName() {
        return PLAY_STORE_NAME;
    }

    @Override
    public void install(PurchaseObserver observer, PurchaseManagerConfig config, boolean autoFetchInformation) {
        this.observer = observer;
        this.billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases(
                        PendingPurchasesParams.newBuilder()
                                .enableOneTimeProducts()
                                .build()
                )
                .build();
        billingClient.startConnection(this);
    }

    @Override
    public boolean installed() {
        return installed;
    }

    @Override
    public void dispose() {
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }

    @Override
    public void purchase(final String identifier) {
        if (!installed || billingClient == null) {
            notifyPurchaseError(new IllegalStateException("Billing client is not connected"));
            return;
        }

        List<QueryProductDetailsParams.Product> products = new ArrayList<>();
        products.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(identifier)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, queryResult) -> {
            List<ProductDetails> productDetailsList = queryResult != null ? queryResult.getProductDetailsList() : null;
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || productDetailsList == null || productDetailsList.isEmpty()) {
                notifyPurchaseError(new IllegalStateException("Unable to load product details for " + identifier));
                return;
            }

            ProductDetails details = productDetailsList.get(0);
            productDetailsById.put(details.getProductId(), details);

            BillingFlowParams.ProductDetailsParams productDetailsParams =
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(details)
                            .build();

            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
            productDetailsParamsList.add(productDetailsParams);
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();

            BillingResult flowResult = billingClient.launchBillingFlow(activity, flowParams);
            if (flowResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                notifyPurchaseError(new IllegalStateException("Billing flow failed with code " + flowResult.getResponseCode()));
            }
        });
    }

    @Override
    public void purchaseRestore() {
        if (!installed || billingClient == null) {
            notifyRestoreError(new IllegalStateException("Billing client is not connected"));
            return;
        }

        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();

        billingClient.queryPurchasesAsync(params, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    notifyRestoreError(new IllegalStateException("Restore query failed with code " + billingResult.getResponseCode()));
                    return;
                }

                List<Transaction> transactions = new ArrayList<>();
                if (purchases != null) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
                            continue;
                        }
                        for (String productId : purchase.getProducts()) {
                            transactions.add(toTransaction(productId, purchase));
                        }
                    }
                }
                observer.handleRestore(transactions.toArray(new Transaction[0]));
            }
        });
    }

    @Override
    public Information getInformation(String identifier) {
        ProductDetails details = productDetailsById.get(identifier);
        if (details == null || details.getOneTimePurchaseOfferDetails() == null) {
            return Information.UNAVAILABLE;
        }
        return new Information(
                details.getTitle(),
                details.getDescription(),
                details.getOneTimePurchaseOfferDetails().getFormattedPrice()
        );
    }

    @Override
    public void onBillingServiceDisconnected() {
        installed = false;
        billingClient.startConnection(this);
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            installed = true;
            if (observer != null) {
                observer.handleInstall();
            }
            return;
        }

        installed = false;
        if (observer != null) {
            observer.handleInstallError(new IllegalStateException("Billing setup failed with code " + billingResult.getResponseCode()));
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (observer == null) {
            return;
        }

        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                acknowledgeIfNeeded(purchase);
                for (String productId : purchase.getProducts()) {
                    observer.handlePurchase(toTransaction(productId, purchase));
                }
            }
            return;
        }

        if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            observer.handlePurchaseCanceled();
            return;
        }

        observer.handlePurchaseError(new IllegalStateException("Purchase failed with code " + responseCode));
    }

    private Transaction toTransaction(String productId, Purchase purchase) {
        Transaction transaction = new Transaction();
        transaction.setIdentifier(productId);
        transaction.setStoreName(PLAY_STORE_NAME);
        transaction.setOrderId(purchase.getOrderId());
        transaction.setTransactionData(purchase.getOriginalJson());
        transaction.setTransactionDataSignature(purchase.getSignature());
        transaction.setPurchaseTime(new Date(purchase.getPurchaseTime()));
        return transaction;
    }

    private void acknowledgeIfNeeded(Purchase purchase) {
        if (purchase.isAcknowledged()) {
            return;
        }
        AcknowledgePurchaseParams acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.acknowledgePurchase(acknowledgeParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                // Billing library returns purchase via listener, acknowledgement result is tracked by Play.
            }
        });
    }

    private void notifyPurchaseError(Throwable throwable) {
        if (observer != null) {
            observer.handlePurchaseError(throwable);
        }
    }

    private void notifyRestoreError(Throwable throwable) {
        if (observer != null) {
            observer.handleRestoreError(throwable);
        }
    }
}
