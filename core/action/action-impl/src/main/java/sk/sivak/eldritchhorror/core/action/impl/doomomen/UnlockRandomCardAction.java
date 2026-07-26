package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.AdHandler;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardId;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.AD_MOB;

public class UnlockRandomCardAction implements Action<Object, Void> {
    private boolean adRewarded;

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            adRewarded = false;
            List<AssetId> lockedAssets = ServicePlatform.get().getAssetsDeck().getLockedAssets();
            List<ArtifactId> lockedArtifacts = ServicePlatform.get().getArtifactsDeck().getLockedArtifacts();
            List<CardId> lockedCards = new LinkedList<>();
            lockedCards.addAll(lockedAssets);
            lockedCards.addAll(lockedArtifacts);
            if (lockedCards.isEmpty()) {
                onSub.onSuccess(null);
                return;
            }
            if (ServicePlatform.get().getInitGameController().hasPurchasedNoAds()) {
                darkenWorld();
                giveReward(onSub, lockedCards);
                return;
            }
            GoogleServicesHolder.getAdHandler().isRewardedVideoAdLoaded().subscribe(isRewardedVideoLoaded -> {
                postRunnable(() -> {
                    if (!isRewardedVideoLoaded) {
                        onSub.onSuccess(null);
                        return;
                    }
                    onAdLoaded(onSub, lockedCards);
                });
            });

        });
    }

    private void onAdLoaded(SingleSubscriber<? super Void> onSub, List<CardId> lockedCards) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setTitle("Watch Ad to unlock new content?");
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setHideType(HideType.RETURN);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        showCardRequest.setHiddenTemplate(true);
        darkenWorld();
        ServicePlatform.get().getGameController().showCard(showCardRequest).subscribe(watchAdResponse -> {
            if (watchAdResponse != ShowCardResponse.YES) {
                brightenWorld();
                onSub.onSuccess(null);
                return;
            }
            Completable.complete().delay(500, TimeUnit.MILLISECONDS).subscribe(() -> {
                GoogleServicesHolder.getAdHandler().showRewardedAd(new AdHandler.AdCallbacks()
                        .setOnAdStartedAction(() -> GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "show_started"))
                        .setOnAdRewardedAction(() -> {
                            adRewarded = true;
                            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "reward_granted");
                        })
                        .setOnAdClosedAction(() -> postRunnable(() -> {
                            if (!adRewarded) {
                                brightenWorld();
                                onSub.onSuccess(null);
                                return;
                            }
                            giveReward(onSub, lockedCards);
                        }))
                        .setOnAdFailedToLoadAction(errorCode -> postRunnable(() -> {
                            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "show_failed_" + errorCode);
                            brightenWorld();
                            onSub.onSuccess(null);
                        })));
            });


        });
    }

    private void giveReward(SingleSubscriber<? super Void> onSub, List<CardId> lockedCards) {
        Collections.shuffle(lockedCards);
        CardId unlockedCard = lockedCards.get(0);
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        showCardRequest.setHideType(HideType.RETURN);
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setTitle("Congratulations! You unlocked a new card!");
        try {
            if (unlockedCard instanceof AssetId) {
                showCardRequest.setAssetInfo((AssetInfo) Class.forName(((AssetId) unlockedCard).getAssetClassName()).newInstance());
                ServicePlatform.get().getAssetsDeck().unlockCard((AssetId)unlockedCard);
            } else if (unlockedCard instanceof ArtifactId) {
                showCardRequest.setArtifactInfo((ArtifactInfo) Class.forName(((ArtifactId) unlockedCard).getArtifactClassName()).newInstance());
                ServicePlatform.get().getArtifactsDeck().unlockCard((ArtifactId) unlockedCard);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        ServicePlatform.get().getGameController().showCard(showCardRequest).subscribe(ok -> {
            brightenWorld();
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.CARD_UNLOCKED, unlockedCard.asString());
            onSub.onSuccess(null);
        });

    }

    private void darkenWorld() {
        ServicePlatform.get().getGameController().hideButtonsAndInvestigatorHud(true);
        ServicePlatform.get().getCardController().darkenWorld(0.85f);
    }

    private void brightenWorld() {
        ServicePlatform.get().getGameController().showHud();
        ServicePlatform.get().getCardController().brightenWorld();
    }

    private void postRunnable(Runnable runnable) {
        ServicePlatform.get().getGameController().postRunnable(runnable);
    }

    @Override
    public void setInput(Object input) {

    }
}
