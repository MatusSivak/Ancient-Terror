package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

/**
 * @author msivak
 */
public interface ReserveActionProvider {

    HookableAction<ShowReserveDragAndDropInput, ShowReserveDragAndDropOutput> showReserveDragAndDrop();

    Action<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> takeBankLoan();

    HookableAction<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> registerSelectedAssets();

    HookableAction<Object, Void> refillReserve();

    HookableAction<SelectCardData, CardInfo> selectSingleCard();

    HookableAction<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> replaceCardInReserve();

    Action<Object, Void> discardAssetFromReserve(CardInfo cardInfo);

}
