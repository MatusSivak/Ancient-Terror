package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class ShowCardAction extends AbstractHookableAction<ShowCardRequest, ShowCardResponse> {

    public ShowCardAction() {
        super(BeforeAfterEvent.SHOW_CARD);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super ShowCardResponse> ss) {
        ServicePlatform.get().getGameController().showCard(input)
                .subscribe(ss::onSuccess);
    }
}
