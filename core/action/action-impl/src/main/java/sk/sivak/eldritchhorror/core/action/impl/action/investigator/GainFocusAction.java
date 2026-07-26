package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainFocusData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class GainFocusAction extends AbstractHookableAction<GainFocusData, GainFocusData> {

    public GainFocusAction() {
        super(BeforeAfterEvent.GAIN_FOCUS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainFocusData> ss) {

        InvestigatorWrite investigator;
        if (input.getInvestigatorId() != null) {
            investigator = ServicePlatform.get().getInvestigators().getInvestigator(input.getInvestigatorId());
        } else {
            investigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        }

        if (investigator.getFocusTokens() == 2) {
            ss.onSuccess(input);
        } else {
            investigator.setFocusTokens(investigator.getFocusTokens() + 1);
            ServicePlatform.get().getGameController().gainFocus(1)
                    .subscribe(() -> ss.onSuccess(input));
        }

    }
}
