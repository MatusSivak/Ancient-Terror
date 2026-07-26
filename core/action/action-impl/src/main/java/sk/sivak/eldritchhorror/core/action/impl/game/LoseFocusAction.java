package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

public class LoseFocusAction extends AbstractHookableAction<LoseTokenData, LoseTokenData> {

    public LoseFocusAction() {
        super(BeforeAfterEvent.LOSE_FOCUS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LoseTokenData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        input.setExecuted(true);
        activeInvestigator.setFocusTokens(activeInvestigator.getFocusTokens() - input.getAmount());

        ServicePlatform.get().getGameController().loseFocus(input.getAmount())
                .subscribe(() -> ss.onSuccess(input));
    }
}
