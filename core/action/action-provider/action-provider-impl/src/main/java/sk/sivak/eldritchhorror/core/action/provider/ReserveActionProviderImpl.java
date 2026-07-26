package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.card.SelectSingleCardAction;
import sk.sivak.eldritchhorror.core.action.impl.reserve.*;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

/**
 * @author msivak
 */
public class ReserveActionProviderImpl extends AbstractActionProvider implements ReserveActionProvider {

    @Override
    public HookableAction<ShowReserveDragAndDropInput, ShowReserveDragAndDropOutput> showReserveDragAndDrop() {
        return new ShowReserveDragAndDropAction();
    }

    @Override
    public Action<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> takeBankLoan() {
        return new TakeBankLoanAction();
    }

    @Override
    public HookableAction<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> registerSelectedAssets() {
        return new RegisterSelectedAssetsAction();
    }

    @Override
    public HookableAction<Object, Void> refillReserve() {
        return new RefillReserveAction();
    }

    @Override
    public HookableAction<SelectCardData, CardInfo> selectSingleCard() {
        return new SelectSingleCardAction();
    }

    @Override
    public HookableAction<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> replaceCardInReserve() {
        return new ReplaceCardInReserveAction();
    }

    @Override
    public Action<Object, Void> discardAssetFromReserve(CardInfo cardInfo) {
        return new DiscardAssetFromReserveAction(cardInfo);
    }
}
