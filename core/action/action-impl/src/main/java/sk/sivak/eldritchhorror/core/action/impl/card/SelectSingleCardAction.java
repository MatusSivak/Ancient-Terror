package sk.sivak.eldritchhorror.core.action.impl.card;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.Collections;

public class SelectSingleCardAction extends AbstractHookableAction<SelectCardData, CardInfo> {

    public SelectSingleCardAction() {
        super(BeforeAfterEvent.SELECT_SINGLE_CARD);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CardInfo> ss) {
        if (input.getAvailableCards().size() == 1) {
            ss.onSuccess(input.getAvailableCards().get(0));
            return;
        }
        if (input.getAvailableCards().size() == 0) {
            ss.onSuccess(null);
            return;
        }
        ServicePlatform.get().getCardController().selectSingleCard(
                input.getAvailableCards(),
                input.getTitleText(),
                input.getHideText()).subscribe(this::onSelect);
    }

    private void onSelect(CardInfo cardInfo) {
        ss.onSuccess(cardInfo);
    }
}
