package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import static java8.features.util.MapUtils.getOrDefault;

public class SpendAction extends AbstractHookableAction<SpendData, SpendData> {

    public SpendAction() {
        super(BeforeAfterEvent.SPEND);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super SpendData> ss) {
        Integer clueRequired = getOrDefault(input.getTokenAmountMap(), TokenType.CLUE, 0);
        Integer focusRequired = getOrDefault(input.getTokenAmountMap(), TokenType.FOCUS, 0);
        Integer healthRequired = getOrDefault(input.getTokenAmountMap(), TokenType.HEALTH, 0);
        Integer sanityRequired = getOrDefault(input.getTokenAmountMap(), TokenType.SANITY, 0);

        boolean hasEnough = hasEnough(clueRequired, focusRequired, healthRequired, sanityRequired);
        input.setHasEnough(hasEnough);
        if (hasEnough) {
            Action0 payAction = createPayAction(clueRequired, focusRequired, healthRequired, sanityRequired);
            Action0 revertAction = createRevertAction(clueRequired, focusRequired, healthRequired, sanityRequired);
            input.setPayAction(payAction);
            input.setRevertAction(revertAction);
        }
        ss.onSuccess(input);
    }

    private Action0 createPayAction(Integer clueRequired, Integer focusRequired, Integer healthRequired, Integer sanityRequired) {
        return () -> {
            for (int i = 0; i < clueRequired; i++) {
                ServicePlatform.get().getTokenService().loseClue();
            }
            for (int i = 0; i < focusRequired; i++) {
                ServicePlatform.get().getTokenService().loseFocus();
            }
            if (healthRequired > 0) {
                ServicePlatform.get().getTokenService().spendHealth(healthRequired);
            }
            if (sanityRequired > 0) {
                ServicePlatform.get().getTokenService().spendSanity(sanityRequired);
            }
        };
    }

    private Action0 createRevertAction(Integer clueRequired, Integer focusRequired, Integer healthRequired, Integer sanityRequired) {
        return () -> {
            for (int i = 0; i < clueRequired; i++) {
                ServicePlatform.get().getTokenService().gainClueFromPool();
            }
            for (int i = 0; i < focusRequired; i++) {
                ServicePlatform.get().getTokenService().gainFocus();
            }
            if (healthRequired > 0) {
                ServicePlatform.get().getTokenService().gainHealth(healthRequired);
            }
            if (sanityRequired > 0) {
                ServicePlatform.get().getTokenService().gainSanity(sanityRequired);
            }
        };
    }

    private boolean hasEnough(Integer clueRequired, Integer focusRequired, Integer healthRequired, Integer sanityRequired) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        int ownedClues = ServicePlatform.get().getCluePool().getClueCount(activeInvestigator.getInfo().getInvestigatorId());
        int ownedFocus = activeInvestigator.getFocusTokens();
        int ownedHealth = activeInvestigator.getCurrentHealth();
        int ownedSanity = activeInvestigator.getCurrentSanity();

        if (ownedClues < clueRequired) {
            return false;
        }
        if (ownedFocus < focusRequired) {
            return false;
        }
        if (ownedHealth <= healthRequired) {
            return false;
        }
        if (ownedSanity <= sanityRequired) {
            return false;
        }
        return true;
    }
}
