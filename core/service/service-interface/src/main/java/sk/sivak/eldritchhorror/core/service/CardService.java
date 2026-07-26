package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

public interface CardService extends CommonService {
    void acquireAssets(int value);

    void refillReserve();

    void takeBankLoan();

    Single<CardInfo> selectSingleCard(SelectCardData selectCardData);

    void discardAssetFromReserve(CardInfo selectedAsset);
}
