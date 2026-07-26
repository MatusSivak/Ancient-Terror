package sk.sivak.eldritchhorror.core.action.impl.basic;

import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainHealthData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class GainHealthAction extends AbstractHookableAction<GainHealthData, GainHealthData> {

    public GainHealthAction() {
        super(BeforeAfterEvent.GAIN_HEALTH);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainHealthData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        int currentHealth = activeInvestigator.getCurrentHealth();
        int maxHealth = activeInvestigator.getInfo().getMaxHealth();
        if (currentHealth + input.getAmount() > maxHealth) {
            input.setAmount(maxHealth - currentHealth);
        }
        if (input.getAmount() == 0) {
            ss.onSuccess(input);
            return;
        }
        Completable healthCompletable = ServicePlatform.get().getGameController().gainHealth(input.getAmount());
        activeInvestigator.setCurrentHealth(currentHealth + input.getAmount());
        healthCompletable.subscribe(() -> ss.onSuccess(input));
    }
}
