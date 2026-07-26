package sk.sivak.eldritchhorror.core.eventlistener.card;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

public class EnableCardListener extends EventListenerImpl<Void> {

    private final CardInfo cardInfo;

    public EnableCardListener(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    @Override
    public void onNotify(Void eventData) {
        ServicePlatform.get().getService().enableCard(cardInfo);
    }

    @Override
    public Class<Void> getDataClass() {
        return Void.class;
    }
}