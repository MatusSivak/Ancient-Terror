package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DealDamageToMonsterData;

public class DealDamageToMonsterAction extends AbstractHookableAction<DealDamageToMonsterData, DealDamageToMonsterData> {

    public DealDamageToMonsterAction() {
        super(BeforeAfterEvent.DEAL_DAMAGE_TO_MONSTER);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DealDamageToMonsterData> ss) {
        if (input.getAmount() == 0) {
            ss.onSuccess(input);
            return;
        }
        if (input.isDisplayOrHideMonsterCard()) {
            ServicePlatform.get().getGameController().displayMonsterCard(input.getMonsterInfo(), null, this::onDisplayAction);
        } else {
            int amount = Math.min(input.getAmount(), input.getMonsterInfo().getCurrentHealth());
            ServicePlatform.get().getMonsterCup().loseHealth(input.getMonsterInfo(), amount);
            onTakeHealth();
        }


    }

    private void onDisplayAction() {
        int amount = Math.min(input.getAmount(), input.getMonsterInfo().getCurrentHealth());
        ServicePlatform.get().getMonsterCup().loseHealth(input.getMonsterInfo(), amount);
        ServicePlatform.get().getMonsterController().loseHealth(amount)
                .subscribe(this::onTakeHealth);
    }

    private void onTakeHealth() {
        if (input.getMonsterInfo().getCurrentHealth() == 0) {
            ServicePlatform.get().getMonsterService().hold();
            ServicePlatform.get().getMonsterService().defeatMonster(input.getMonsterInfo(), input.isInCombat(), input.isDisplayOrHideMonsterCard());
            ServicePlatform.get().getMonsterService().convertTo(DealDamageToMonsterData.class, () -> input);
            ServicePlatform.get().getMonsterService().release();
            ss.onSuccess(null);
        } else if (input.isDisplayOrHideMonsterCard()) {
            ServicePlatform.get().getGameController().hideMonsterCard(input.getMonsterInfo(), () -> ss.onSuccess(input));
        } else {
            ss.onSuccess(input);
        }
    }
}
