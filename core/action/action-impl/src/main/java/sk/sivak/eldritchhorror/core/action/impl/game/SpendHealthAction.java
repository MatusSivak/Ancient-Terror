package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

public class SpendHealthAction extends AbstractHookableAction<LoseTokenData, LoseTokenData> {

    public SpendHealthAction() {
        super(BeforeAfterEvent.SPEND_HEALTH);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LoseTokenData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (input.getAmount() > 0) {
            input.setExecuted(true);
            activeInvestigator.setCurrentHealth(activeInvestigator.getCurrentHealth() - input.getAmount());
            ServicePlatform.get().getGameController().loseHealth(input.getAmount())
                    .subscribe(() -> ss.onSuccess(input));
        } else {
            input.setExecuted(false);
            ss.onSuccess(input);
        }
    }
}
