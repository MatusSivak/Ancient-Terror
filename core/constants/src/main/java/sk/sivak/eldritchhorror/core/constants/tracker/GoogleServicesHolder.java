package sk.sivak.eldritchhorror.core.constants.tracker;

import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import java8.features.function.Consumer;
import java8.features.function.Supplier;
import rx.Completable;
import rx.Single;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.AdHandler;
import sk.sivak.eldritchhorror.core.constants.GameRestarter;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleServicesHolder {
    private static AnalyticsTracker analyticsTracker;
    private static AdHandler adHandler;
    private static boolean tutorialPassed = false;
    private static Action0 tutorialPassedAction = () -> {};
    private static Consumer<List<AssetId>> overwriteUnlockedAssetsFunction = whatever -> {};
    private static Consumer<List<ArtifactId>> overwriteUnlockedArtifactsFunction = whatever -> {};
    private static Runnable openStore = () -> {};
    private static Runnable askLaterAction = () -> {};
    private static Supplier<Boolean> shouldShowRateDialogSupplier = () -> true;
    private static PurchaseManager purchaseManager;
    private static CustomPurchaseObserver customPurchaseObserver;
    ;

    public static void setOverwriteUnlockedAssetsFunction(Consumer<List<AssetId>> overwriteUnlockedAssetsFunction) {
        GoogleServicesHolder.overwriteUnlockedAssetsFunction = overwriteUnlockedAssetsFunction;
    }

    public static void setOpenStoreAction(Runnable openStore) {
        GoogleServicesHolder.openStore = openStore;
    }

    public static void setAskLaterAction(Runnable askLaterAction) {
        GoogleServicesHolder.askLaterAction = askLaterAction;
    }

    public static void openStore() {
        GoogleServicesHolder.openStore.run();
    }

    public static void askLater() {
        GoogleServicesHolder.askLaterAction.run();
    }

    public static void setShouldShowRateDialogSupplier(Supplier<Boolean> supplier) {
        GoogleServicesHolder.shouldShowRateDialogSupplier = supplier;
    }

    public static boolean shouldShowRateDialog() {
        return GoogleServicesHolder.shouldShowRateDialogSupplier.get();
    }

    public static void setOverwriteUnlockedArtifactsFunction(Consumer<List<ArtifactId>> overwriteUnlockedArtifactsFunction) {
        GoogleServicesHolder.overwriteUnlockedArtifactsFunction = overwriteUnlockedArtifactsFunction;
    }

    public static boolean isTutorialPassed() {
        return GoogleServicesHolder.tutorialPassed;
    }

    public static void setTutorialPassed(boolean tutorialPassed) {
        GoogleServicesHolder.tutorialPassed = tutorialPassed;
    }

    public static void setTutorialPassedAction(Action0 tutorialPassedAction) {
        GoogleServicesHolder.tutorialPassedAction = tutorialPassedAction;
    }

    public static void executeTutorialPassedAction() {
        GoogleServicesHolder.tutorialPassed = true;
        tutorialPassedAction.call();
    }

    public static void setAnalyticsTracker(AnalyticsTracker analyticsTracker) {
        GoogleServicesHolder.analyticsTracker = analyticsTracker;
    }

    public static AdHandler getAdHandler() {
        if (adHandler == null) {
            adHandler = new AdHandler() {
                private final List<Runnable> onAdLoadedActions = new ArrayList<>();
                private final List<Consumer<Integer>> onAdFailedToLoadActions = new ArrayList<>();

                @Override
                public void showRewardedAd(AdCallbacks callbacks) {
                    AdCallbacks effectiveCallbacks = callbacks == null ? new AdCallbacks() : callbacks;
                    effectiveCallbacks.getOnAdStartedAction().run();
                    Completable.complete().delay(3, TimeUnit.SECONDS).subscribe(() -> {
                        effectiveCallbacks.getOnAdRewardedAction().run();
                        effectiveCallbacks.getOnAdClosedAction().run();

                    });

                }

                @Override
                public Single<Boolean> isRewardedVideoAdLoaded() {
                    return Single.just(true);
                }

                @Override
                public void showInterstitialAd(AdCallbacks callbacks) {
                    AdCallbacks effectiveCallbacks = callbacks == null ? new AdCallbacks() : callbacks;
                    effectiveCallbacks.getOnAdOpenedAction().run();
                    effectiveCallbacks.getOnAdClosedAction().run();
                }

                @Override
                public void showInterstitialAdAfterThreeMinutes(AdCallbacks callbacks) {
                    showInterstitialAd(callbacks == null ? new AdCallbacks() : callbacks);
                }

                @Override
                public Runnable addOnRewardedAdLoadedAction(Runnable onAdLoadedAction) {
                    onAdLoadedActions.add(onAdLoadedAction);
                    Completable.complete().delay(3, TimeUnit.SECONDS).subscribe(onAdLoadedAction::run);
                    return () -> onAdLoadedActions.remove(onAdLoadedAction);
                }

                @Override
                public Runnable addOnRewardedAdFailedToLoadAction(Consumer<Integer> onAdFailedToLoadAction) {
                    onAdFailedToLoadActions.add(onAdFailedToLoadAction);
                    return () -> onAdFailedToLoadActions.remove(onAdFailedToLoadAction);
                }
            };
        }
        return adHandler;
    }
    public static AnalyticsTracker getAnalyticsTracker() {
        if (analyticsTracker == null) {
            return new AnalyticsTracker() {
                @Override
                public void trackScreenName(String screenName) {
                    System.out.println("### Tracking screen name: " + screenName);
                }

                @Override
                public void trackInteraction(AnalyticsCategory category, String action) {
                    System.out.println("### Tracking interaction: " + category + "/" + action);
                }

                @Override
                public void trackInteraction(AnalyticsCategory category, String action, String label) {
                    System.out.println("### Tracking interaction: " + category + "/" + action + "/" + label);
                }

                @Override
                public void trackNonInteraction(AnalyticsCategory category, String action) {
                    System.out.println("### Tracking non-interaction: " + category + "/" + action);
                }

                @Override
                public void trackNonInteraction(AnalyticsCategory category, String action, String label) {
                    System.out.println("### Tracking non-interaction: " + category + "/" + action+ "/" + label);
                }

                @Override
                public void trackTiming(AnalyticsCategory category, String name, Long value) {
                    System.out.println("### Tracking timing: " + category + "/" + name+ "/" + value);
                }

                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(thread, throwable);
                }
            };
        } else {
            return analyticsTracker;
        }
    }

    public static void setAdHandler(AdHandler adHandler) {
        GoogleServicesHolder.adHandler = adHandler;
    }

    public static void overwriteUnlockedAssets(List<AssetId> unlockedCards) {
        overwriteUnlockedAssetsFunction.accept(unlockedCards);
    }

    public static void overwriteUnlockedArtifacts(List<ArtifactId> unlockedCards) {
        overwriteUnlockedArtifactsFunction.accept(unlockedCards);
    }

    public static void setPurchaseManager(PurchaseManager purchaseManager) {
        GoogleServicesHolder.purchaseManager = purchaseManager;
        PurchaseManagerConfig pmc = new PurchaseManagerConfig();

        customPurchaseObserver = new CustomPurchaseObserver();
        purchaseManager.install(customPurchaseObserver, pmc, false);
    }

    public static PurchaseManager getPurchaseManager() {
        return purchaseManager;
    }

    public static CustomPurchaseObserver getCustomPurchaseObserver() {
        return customPurchaseObserver;
    }

}
