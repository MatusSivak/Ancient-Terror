package sk.sivak.eldritchhorror.core.view;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;

import java.util.List;

/**
 * @author msivak
 */
public interface CardView {

    Single<ShowReserveDragAndDropOutput> showReserveDragAndDrop(int testScore, boolean bankLoanAvailable, List<AssetInfo> reserve, AssetId mandatoryAssetId);

    Single<CardInfo> selectSingleCard(List<? extends CardInfo> availableCards, String titleText, String hideText);

    void darkenWorld(float alpha);

    void brightenWorld();
}
