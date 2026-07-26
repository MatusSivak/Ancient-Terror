package sk.sivak.eldritchhorror.core.action.impl.game;

import com.badlogic.gdx.Gdx;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
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

public class AskToRateThisGameAction implements Action<Object, Object> {
    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            if (!GoogleServicesHolder.shouldShowRateDialog()) {
                onSub.onSuccess(input);
                return;
            }
            if (!GoogleServicesHolder.isTutorialPassed()) {
                onSub.onSuccess(input);
                return;
            }

            List<AssetId> lockedAssets = ServicePlatform.get().getAssetsDeck().getLockedAssets();
            List<ArtifactId> lockedArtifacts = ServicePlatform.get().getArtifactsDeck().getLockedArtifacts();
            List<CardId> lockedCards = new LinkedList<>();
            lockedCards.addAll(lockedAssets);
            lockedCards.addAll(lockedArtifacts);
            if (lockedCards.isEmpty()) {
                onSub.onSuccess(input);
                return;
            }
            xxx(onSub, lockedCards);

        });
    }

    private void xxx(SingleSubscriber<? super Object> onSub, List<CardId> lockedCards) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setTitle("Rate this game to unlock new content?");
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setHideType(HideType.RETURN);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        showCardRequest.setHiddenTemplate(true);
        darkenWorld();
        ServicePlatform.get().getGameController().showCard(showCardRequest).subscribe(rateThisGameResponse -> {
            if (rateThisGameResponse != ShowCardResponse.YES) {
                GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.RATE_THIS_APP, "No");
                brightenWorld();
                GoogleServicesHolder.askLater();
                onSub.onSuccess(input);
                return;
            }
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.RATE_THIS_APP, "Yes");
            Completable.complete().delay(500, TimeUnit.MILLISECONDS).subscribe(GoogleServicesHolder::openStore);
            Completable.complete().delay(1000, TimeUnit.MILLISECONDS).subscribe(() -> {
                ServicePlatform.get().getGameController().postRunnable(() -> {
                    giveReward(onSub, lockedCards);
                });
            });
        });
    }

    private void giveReward(SingleSubscriber<? super Object> onSub, List<CardId> lockedCards) {
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
            onSub.onSuccess(input);
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

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
