package sk.sivak.eldritchhorror.android;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import java.util.ArrayList;
import java.util.List;
import java8.features.function.Consumer;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.AdHandler;

public class AndroidAdHandler implements AdHandler {

    private static final String TAG = "AndroidAdHandler";
    private static final String TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final long INTERSTITIAL_COOLDOWN_MS = 6L * 60L * 1000L;
    private static final long LOAD_RETRY_DELAY_MS = 15_000L;

    private final Activity activity;
    private final Context applicationContext;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String rewardedAdUnitId;
    private final String interstitialAdUnitId;

    private final List<Runnable> rewardedAdLoadedActions = new ArrayList<>();
    private final List<Consumer<Integer>> rewardedAdFailedToLoadActions = new ArrayList<>();

    private RewardedAd rewardedAd;
    private InterstitialAd interstitialAd;
    private long lastInterstitialShownAt = 0L;
    private boolean rewardedAdShowing = false;
    private boolean interstitialAdShowing = false;
    private boolean rewardedLoadInFlight = false;
    private boolean interstitialLoadInFlight = false;
    private volatile boolean destroyed = false;
    private final Runnable rewardedReloadRunnable = () -> {
        if (destroyed) {
            return;
        }
        if (rewardedAd == null && !rewardedAdShowing) {
            loadRewardedAd("rewarded_retry");
        }
    };
    private final Runnable interstitialReloadRunnable = () -> {
        if (destroyed) {
            return;
        }
        if (interstitialAd == null && !interstitialAdShowing) {
            loadInterstitialAd("interstitial_retry");
        }
    };

    public AndroidAdHandler(Activity activity) {
        this.activity = activity;
        this.applicationContext = activity.getApplicationContext();
        rewardedAdUnitId = resolveAdUnitId(BuildConfig.ADMOB_REWARDED_AD_UNIT_ID, TEST_REWARDED_AD_UNIT_ID);
        interstitialAdUnitId = resolveAdUnitId(BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID, TEST_INTERSTITIAL_AD_UNIT_ID);
        Log.i(TAG, "Initializing ads. rewardedUnitId=" + rewardedAdUnitId + ", interstitialUnitId=" + interstitialAdUnitId);
        MobileAds.initialize(applicationContext, initializationStatus -> {
            Log.i(TAG, "MobileAds initialized");
            preloadAds("startup");
        });
    }

    @Override
    public Single<Boolean> isRewardedVideoAdLoaded() {
        return Single.just(rewardedAd != null);
    }

    @Override
    public void showRewardedAd(AdCallbacks callbacks) {
        final AdCallbacks effectiveCallbacks = callbacks == null ? new AdCallbacks() : callbacks;
        if (!runOnUiThreadSafely(() -> {
            if (rewardedAdShowing) {
                Log.w(TAG, "Rewarded show skipped because another rewarded ad is already showing");
                dispatchFailure(-2, effectiveCallbacks.getOnAdFailedToLoadAction());
                dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                return;
            }
            if (rewardedAd == null) {
                Log.w(TAG, "Rewarded show requested before ad was loaded");
                dispatchFailure(-1, effectiveCallbacks.getOnAdFailedToLoadAction());
                dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                loadRewardedAd("show_requested_without_cache");
                return;
            }

            final RewardedAd activeRewardedAd = rewardedAd;
            rewardedAdShowing = true;
            Log.i(TAG, "Showing rewarded ad");

            dispatchAction(effectiveCallbacks.getOnAdStartedAction());
            activeRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    Log.i(TAG, "Rewarded ad opened");
                    dispatchAction(effectiveCallbacks.getOnAdOpenedAction());
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    rewardedAd = null;
                    rewardedAdShowing = false;
                    Log.i(TAG, "Rewarded ad dismissed");
                    dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                    dispatchAction(effectiveCallbacks.getOnAdCompletedAction());
                    loadRewardedAd("rewarded_dismissed");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    rewardedAd = null;
                    rewardedAdShowing = false;
                    Log.w(TAG, "Rewarded ad failed to show. code=" + adError.getCode() + ", message=" + adError.getMessage());
                    dispatchFailure(adError.getCode(), effectiveCallbacks.getOnAdFailedToLoadAction());
                    dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                    loadRewardedAd("rewarded_show_failed");
                }
            });

            activeRewardedAd.show(activity, new com.google.android.gms.ads.OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(RewardItem rewardItem) {
                    Log.i(TAG, "Reward granted. type=" + rewardItem.getType() + ", amount=" + rewardItem.getAmount());
                    dispatchAction(effectiveCallbacks.getOnAdRewardedAction());
                }
            });
        })) {
            dispatchFailure(-3, effectiveCallbacks.getOnAdFailedToLoadAction());
            dispatchAction(effectiveCallbacks.getOnAdClosedAction());
        }
    }

    @Override
    public void showInterstitialAd(AdCallbacks callbacks) {
        final AdCallbacks effectiveCallbacks = callbacks == null ? new AdCallbacks() : callbacks;
        if (!runOnUiThreadSafely(() -> {
            if (interstitialAdShowing) {
                Log.w(TAG, "Interstitial show skipped because another interstitial is already showing");
                dispatchFailure(-2, effectiveCallbacks.getOnAdFailedToLoadAction());
                dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                return;
            }
            if (interstitialAd == null) {
                Log.w(TAG, "Interstitial show requested before ad was loaded");
                dispatchFailure(-1, effectiveCallbacks.getOnAdFailedToLoadAction());
                dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                loadInterstitialAd("show_requested_without_cache");
                return;
            }

            final InterstitialAd activeInterstitialAd = interstitialAd;
            interstitialAdShowing = true;
            Log.i(TAG, "Showing interstitial ad");

            activeInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    Log.i(TAG, "Interstitial ad opened");
                    dispatchAction(effectiveCallbacks.getOnAdOpenedAction());
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    interstitialAd = null;
                    interstitialAdShowing = false;
                    Log.i(TAG, "Interstitial ad dismissed");
                    dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                    loadInterstitialAd("interstitial_dismissed");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    interstitialAd = null;
                    interstitialAdShowing = false;
                    Log.w(TAG, "Interstitial ad failed to show. code=" + adError.getCode() + ", message=" + adError.getMessage());
                    dispatchFailure(adError.getCode(), effectiveCallbacks.getOnAdFailedToLoadAction());
                    dispatchAction(effectiveCallbacks.getOnAdClosedAction());
                    loadInterstitialAd("interstitial_show_failed");
                }
            });

            lastInterstitialShownAt = SystemClock.elapsedRealtime();
            activeInterstitialAd.show(activity);
        })) {
            dispatchFailure(-3, effectiveCallbacks.getOnAdFailedToLoadAction());
            dispatchAction(effectiveCallbacks.getOnAdClosedAction());
        }
    }

    @Override
    public void showInterstitialAdAfterThreeMinutes(AdCallbacks callbacks) {
        if (SystemClock.elapsedRealtime() - lastInterstitialShownAt < INTERSTITIAL_COOLDOWN_MS) {
            Log.i(TAG, "Interstitial cooldown active, skipping show");
            return;
        }
        showInterstitialAd(callbacks == null ? new AdCallbacks() : callbacks);
    }

    @Override
    public Runnable addOnRewardedAdLoadedAction(Runnable onAdLoadedAction) {
        rewardedAdLoadedActions.add(onAdLoadedAction);
        if (rewardedAd != null) {
            dispatchAction(onAdLoadedAction);
        }
        return () -> rewardedAdLoadedActions.remove(onAdLoadedAction);
    }

    @Override
    public Runnable addOnRewardedAdFailedToLoadAction(Consumer<Integer> onAdFailedToLoadAction) {
        rewardedAdFailedToLoadActions.add(onAdFailedToLoadAction);
        return () -> rewardedAdFailedToLoadActions.remove(onAdFailedToLoadAction);
    }

    private void preloadAds(String reason) {
        loadRewardedAd(reason);
        loadInterstitialAd(reason);
    }

    private void loadRewardedAd(String reason) {
        if (destroyed || !isActivityUsable()) {
            return;
        }
        if (rewardedLoadInFlight) {
            Log.i(TAG, "Rewarded load skipped, already in flight. reason=" + reason);
            return;
        }
        rewardedLoadInFlight = true;
        Log.i(TAG, "Loading rewarded ad. reason=" + reason);
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(applicationContext, rewardedAdUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                if (destroyed) {
                    return;
                }
                rewardedLoadInFlight = false;
                rewardedAd = ad;
                Log.i(TAG, "Rewarded ad loaded");
                for (Runnable onAdLoadedAction : new ArrayList<>(rewardedAdLoadedActions)) {
                    dispatchAction(onAdLoadedAction);
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                if (destroyed) {
                    return;
                }
                rewardedLoadInFlight = false;
                rewardedAd = null;
                Log.w(TAG, "Rewarded ad failed to load. code=" + adError.getCode() + ", message=" + adError.getMessage());
                for (Consumer<Integer> onAdFailedToLoadAction : new ArrayList<>(rewardedAdFailedToLoadActions)) {
                    dispatchFailure(adError.getCode(), onAdFailedToLoadAction);
                }
                scheduleRewardedReload();
            }
        });
    }

    private void loadInterstitialAd(String reason) {
        if (destroyed || !isActivityUsable()) {
            return;
        }
        if (interstitialLoadInFlight) {
            Log.i(TAG, "Interstitial load skipped, already in flight. reason=" + reason);
            return;
        }
        interstitialLoadInFlight = true;
        Log.i(TAG, "Loading interstitial ad. reason=" + reason);
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(applicationContext, interstitialAdUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(InterstitialAd ad) {
                if (destroyed) {
                    return;
                }
                interstitialLoadInFlight = false;
                interstitialAd = ad;
                Log.i(TAG, "Interstitial ad loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                if (destroyed) {
                    return;
                }
                interstitialLoadInFlight = false;
                interstitialAd = null;
                Log.w(TAG, "Interstitial ad failed to load. code=" + adError.getCode() + ", message=" + adError.getMessage());
                scheduleInterstitialReload();
            }
        });
    }

    private void scheduleRewardedReload() {
        if (destroyed) {
            return;
        }
        mainHandler.removeCallbacks(rewardedReloadRunnable);
        mainHandler.postDelayed(rewardedReloadRunnable, LOAD_RETRY_DELAY_MS);
    }

    private void scheduleInterstitialReload() {
        if (destroyed) {
            return;
        }
        mainHandler.removeCallbacks(interstitialReloadRunnable);
        mainHandler.postDelayed(interstitialReloadRunnable, LOAD_RETRY_DELAY_MS);
    }

    private static String resolveAdUnitId(String configuredValue, String debugFallback) {
        if (TextUtils.isEmpty(configuredValue)) {
            return debugFallback;
        }
        return configuredValue;
    }

    private void dispatchAction(Runnable action) {
        if (destroyed) {
            return;
        }
        if (action == null) {
            return;
        }
        if (Gdx.app != null) {
            Gdx.app.postRunnable(action);
            return;
        }
        action.run();
    }

    private void dispatchFailure(Integer code, Consumer<Integer> action) {
        if (destroyed) {
            return;
        }
        if (action == null) {
            return;
        }
        if (Gdx.app != null) {
            Gdx.app.postRunnable(() -> action.accept(code));
            return;
        }
        action.accept(code);
    }

    private boolean runOnUiThreadSafely(Runnable runnable) {
        if (destroyed || !isActivityUsable()) {
            return false;
        }
        activity.runOnUiThread(() -> {
            if (destroyed || !isActivityUsable()) {
                return;
            }
            runnable.run();
        });
        return true;
    }

    private boolean isActivityUsable() {
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            return false;
        }
        return true;
    }

    @Override
    public void dispose() {
        destroyed = true;
        mainHandler.removeCallbacksAndMessages(null);
        rewardedAd = null;
        interstitialAd = null;
        rewardedAdLoadedActions.clear();
        rewardedAdFailedToLoadActions.clear();
        rewardedAdShowing = false;
        interstitialAdShowing = false;
        rewardedLoadInFlight = false;
        interstitialLoadInFlight = false;
    }
}
