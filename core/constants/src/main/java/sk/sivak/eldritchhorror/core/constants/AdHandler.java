package sk.sivak.eldritchhorror.core.constants;

import java8.features.function.Consumer;
import rx.Single;

public interface AdHandler {
    Single<Boolean> isRewardedVideoAdLoaded();

    default void showRewardedAd() {
        showRewardedAd(new AdCallbacks());
    }

    void showRewardedAd(AdCallbacks callbacks);

    default void showInterstitialAd() {
        showInterstitialAd(new AdCallbacks());
    }

    void showInterstitialAd(AdCallbacks callbacks);

    default void showInterstitialAdAfterThreeMinutes() {
        showInterstitialAdAfterThreeMinutes(new AdCallbacks());
    }

    void showInterstitialAdAfterThreeMinutes(AdCallbacks callbacks);

    Runnable addOnRewardedAdLoadedAction(Runnable onAdLoadedAction);

    Runnable addOnRewardedAdFailedToLoadAction(Consumer<Integer> onAdFailedToLoadAction);

    default void dispose() {
    }

    class AdCallbacks {
        private Runnable onAdOpenedAction = () -> {};
        private Runnable onAdStartedAction = () -> {};
        private Runnable onAdClosedAction = () -> {};
        private Runnable onAdRewardedAction = () -> {};
        private Runnable onAdLeftApplicationAction = () -> {};
        private Runnable onAdCompletedAction = () -> {};
        private Consumer<Integer> onAdFailedToLoadAction = ignored -> {};

        public Runnable getOnAdOpenedAction() {
            return onAdOpenedAction;
        }

        public AdCallbacks setOnAdOpenedAction(Runnable onAdOpenedAction) {
            this.onAdOpenedAction = onAdOpenedAction;
            return this;
        }

        public Runnable getOnAdStartedAction() {
            return onAdStartedAction;
        }

        public AdCallbacks setOnAdStartedAction(Runnable onAdStartedAction) {
            this.onAdStartedAction = onAdStartedAction;
            return this;
        }

        public Runnable getOnAdClosedAction() {
            return onAdClosedAction;
        }

        public AdCallbacks setOnAdClosedAction(Runnable onAdClosedAction) {
            this.onAdClosedAction = onAdClosedAction;
            return this;
        }

        public Runnable getOnAdRewardedAction() {
            return onAdRewardedAction;
        }

        public AdCallbacks setOnAdRewardedAction(Runnable onAdRewardedAction) {
            this.onAdRewardedAction = onAdRewardedAction;
            return this;
        }

        public Runnable getOnAdLeftApplicationAction() {
            return onAdLeftApplicationAction;
        }

        public AdCallbacks setOnAdLeftApplicationAction(Runnable onAdLeftApplicationAction) {
            this.onAdLeftApplicationAction = onAdLeftApplicationAction;
            return this;
        }

        public Runnable getOnAdCompletedAction() {
            return onAdCompletedAction;
        }

        public AdCallbacks setOnAdCompletedAction(Runnable onAdCompletedAction) {
            this.onAdCompletedAction = onAdCompletedAction;
            return this;
        }

        public Consumer<Integer> getOnAdFailedToLoadAction() {
            return onAdFailedToLoadAction;
        }

        public AdCallbacks setOnAdFailedToLoadAction(Consumer<Integer> onAdFailedToLoadAction) {
            this.onAdFailedToLoadAction = onAdFailedToLoadAction;
            return this;
        }
    }
}
