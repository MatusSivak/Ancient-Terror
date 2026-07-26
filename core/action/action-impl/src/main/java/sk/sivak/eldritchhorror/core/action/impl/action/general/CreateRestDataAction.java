package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class CreateRestDataAction extends AbstractHookableAction<Object, RestData> {

    public CreateRestDataAction() {
        super(BeforeAfterEvent.CREATE_REST_DATA);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super RestData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();

        RestData restData = new RestData(0, 0);
        if (activeInvestigator.getCurrentHealth() < activeInvestigator.getInfo().getMaxHealth()) {
            restData.setHealthGained(1);
        }
        if (activeInvestigator.getCurrentSanity() < activeInvestigator.getInfo().getMaxSanity()) {
            restData.setSanityGained(1);
        }

        ss.onSuccess(restData);
    }
}
