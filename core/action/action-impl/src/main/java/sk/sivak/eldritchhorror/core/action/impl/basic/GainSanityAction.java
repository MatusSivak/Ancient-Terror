package sk.sivak.eldritchhorror.core.action.impl.basic;

import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainSanityData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class GainSanityAction extends AbstractHookableAction<GainSanityData, GainSanityData> {

    public GainSanityAction() {
        super(BeforeAfterEvent.GAIN_SANITY);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainSanityData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        int currentSanity = activeInvestigator.getCurrentSanity();
        int maxSanity = activeInvestigator.getInfo().getMaxSanity();
        if (currentSanity + input.getAmount() > maxSanity) {
            input.setAmount(maxSanity - currentSanity);
        }
        if (input.getAmount() == 0) {
            ss.onSuccess(input);
            return;
        }
        Completable sanityCompletable = ServicePlatform.get().getGameController().gainSanity(input.getAmount());
        activeInvestigator.setCurrentSanity(currentSanity + input.getAmount());
        sanityCompletable.subscribe(() -> ss.onSuccess(input));
    }
}
