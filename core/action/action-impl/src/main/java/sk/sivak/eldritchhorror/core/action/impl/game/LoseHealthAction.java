package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

public class LoseHealthAction extends AbstractHookableAction<LoseTokenData, LoseTokenData> {

    public LoseHealthAction() {
        super(BeforeAfterEvent.LOSE_HEALTH);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LoseTokenData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (input.getAmount() > 0) {
            input.setExecuted(true);
            activeInvestigator.setCurrentHealth(activeInvestigator.getCurrentHealth() - input.getAmount());
            int amount = Math.min(input.getAmount(), activeInvestigator.getCurrentHealth() + input.getAmount());
            updateCombatData(amount);
            ServicePlatform.get().getGameController().loseHealth(amount)
                    .subscribe(() -> ss.onSuccess(input));
        } else {
            updateCombatData(0);
            input.setExecuted(false);
            ss.onSuccess(input);
        }
    }

    private void updateCombatData(int healthLost) {
        if (ServicePlatform.get().getTestFlavor().getFlavorType() == TestFlavorType.COMBAT) {
            ((CombatData) ServicePlatform.get().getTestFlavor().getFlavorData()).setHealthLost(healthLost);
        }
    }
}
