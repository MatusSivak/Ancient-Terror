package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.provider.ReserveActionProvider;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

/**
 * @author msivak
 */
public class CardServiceImpl extends AbstractCommonService implements CardService {

    private ReserveActionProvider reserveActionProvider;

    public void setReserveActionProvider(ReserveActionProvider reserveActionProvider) {
        this.reserveActionProvider = reserveActionProvider;
    }

    @Override
    public void acquireAssets(int value) {
        ShowReserveDragAndDropInput input = new ShowReserveDragAndDropInput();
        input.setTestScore(value);
        hold();
        executeHookable(input, reserveActionProvider.showReserveDragAndDrop());
        executeHookable(reserveActionProvider.registerSelectedAssets());
        executeHookable(reserveActionProvider.replaceCardInReserve());
        refillReserve();
        release();
    }

    @Override
    public void refillReserve() {
        executeHookable(reserveActionProvider.refillReserve());
    }

    @Override
    public void takeBankLoan() {
        execute(new ActionCommand<>(reserveActionProvider.takeBankLoan(), "TAKE_BANK_LOAN"));
    }

    @Override
    public Single<CardInfo> selectSingleCard(SelectCardData selectCardData) {
        return Single.create(onSub -> {
            executeHookable(selectCardData, reserveActionProvider.selectSingleCard(), onSub);
        });
    }

    @Override
    public void discardAssetFromReserve(CardInfo cardInfo) {
        execute(new ActionCommand<>(reserveActionProvider.discardAssetFromReserve(cardInfo), "DISCARD_ASSET_FROM_RESERVE"));
    }
}
