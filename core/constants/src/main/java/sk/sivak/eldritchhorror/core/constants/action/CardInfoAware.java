package sk.sivak.eldritchhorror.core.constants.action;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

public interface CardInfoAware<T extends CardInfo> {
    T getCardInfo();
}
