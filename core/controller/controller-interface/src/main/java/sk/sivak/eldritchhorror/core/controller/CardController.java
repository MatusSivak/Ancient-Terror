package sk.sivak.eldritchhorror.core.controller;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;

import java.util.List;

/**
 * @author msivak
 */
public interface CardController {

    Single<ShowReserveDragAndDropOutput> showReserveDragAndDrop(int testScore, boolean bankLoanAvailable, AssetId mandatoryAssetId);

    Single<CardInfo> selectSingleCard(List<? extends CardInfo> availableCards, String titleText, String hideText);

    void darkenWorld(float alpha);

    void brightenWorld();
}
