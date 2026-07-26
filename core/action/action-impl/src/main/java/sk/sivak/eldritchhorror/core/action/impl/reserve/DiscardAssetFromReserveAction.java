package sk.sivak.eldritchhorror.core.action.impl.reserve;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

public class DiscardAssetFromReserveAction implements Action<Object, Void> {


    private final CardInfo cardInfo;

    public DiscardAssetFromReserveAction(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getAssetsDeck().discardFromReserve(((AssetInfo) cardInfo));
            ServicePlatform.get().getGameController().enableDiscardButton();
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }

}
