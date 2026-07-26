package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

public class SewCardAction implements Action<Object, Void> {

    private final CardInfo cardInfo;

    public SewCardAction(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().sewCard(cardInfo).subscribe(() -> onSub.onSuccess(null));
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
