package sk.sivak.eldritchhorror.core.controller;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.model.AssetDeckRead;
import sk.sivak.eldritchhorror.core.view.CardView;

import java.util.List;

/**
 * @author msivak
 */
public class CardControllerImpl implements CardController {

    private CardView cardView;
    private AssetDeckRead assetDeck;

    public void setAssetDeck(AssetDeckRead assetDeck) {
        this.assetDeck = assetDeck;
    }

    public void setView(CardView cardView) {
        this.cardView = cardView;
    }

    @Override
    public Single<ShowReserveDragAndDropOutput> showReserveDragAndDrop(int testScore, boolean bankLoanAvailable, AssetId mandatoryAssetId) {
        return cardView.showReserveDragAndDrop(testScore, bankLoanAvailable, assetDeck.getReserve(), mandatoryAssetId);
    }

    @Override
    public Single<CardInfo> selectSingleCard(List<? extends CardInfo> availableCards, String titleText, String hideText) {
        return cardView.selectSingleCard(availableCards, titleText, hideText);
    }

    @Override
    public void darkenWorld(float alpha) {
        cardView.darkenWorld(alpha);
    }

    @Override
    public void brightenWorld() {
        cardView.brightenWorld();
    }
}
