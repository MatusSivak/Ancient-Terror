package sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;

public abstract class AbstractBlessedConditionBackListener extends AbstractConditionBackListener {

    public AbstractBlessedConditionBackListener(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    public void executeWhole() {
        onFlip(null);
    }
}
