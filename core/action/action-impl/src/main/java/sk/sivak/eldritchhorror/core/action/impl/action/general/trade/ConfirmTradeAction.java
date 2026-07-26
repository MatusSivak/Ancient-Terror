package sk.sivak.eldritchhorror.core.action.impl.action.general.trade;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import java.util.Collections;

public class ConfirmTradeAction extends AbstractHookableAction<TradeData, TradeData> {

    public ConfirmTradeAction() {
        super(BeforeAfterEvent.CONFIRM_TRADE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TradeData> ss) {
        ServicePlatform.get().getGameController().trade(input).subscribe(this::onTrade);
    }

    private void onTrade(TradeData tradeData) {
        ss.onSuccess(tradeData);
    }
}
