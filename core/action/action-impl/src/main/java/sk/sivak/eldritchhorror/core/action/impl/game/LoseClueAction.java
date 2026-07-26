package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

public class LoseClueAction extends AbstractHookableAction<LoseTokenData, LoseTokenData> {

    public LoseClueAction() {
        super(BeforeAfterEvent.LOSE_CLUE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LoseTokenData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        for (int i = 0; i < input.getAmount(); i++) {
            ServicePlatform.get().getCluePool().loseClue(activeInvestigator.getInfo().getInvestigatorId());
        }
        input.setExecuted(true);
        ServicePlatform.get().getGameController().loseClue(input.getAmount())
                .subscribe(() -> ss.onSuccess(input));
    }
}
