package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_FOCUS_TOKENS;

/**
 * @author msivak
 */
public class CreateFocusDataAction extends AbstractHookableAction<Object, FocusData> {

    public CreateFocusDataAction() {
        super(BeforeAfterEvent.CREATE_FOCUS_DATA);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super FocusData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();

        FocusData focusData = new FocusData();
        if (activeInvestigator.getFocusTokens() < MAX_FOCUS_TOKENS) {
            focusData.setFocusGained(1);
        }

        ss.onSuccess(focusData);
    }
}
