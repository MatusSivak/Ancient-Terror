package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardAssetFromInvestigatorData;

/**
 * @author msivak
 */
public class DiscardAssetFromInvestigatorAction extends AbstractHookableAction<DiscardAssetFromInvestigatorData, DiscardAssetFromInvestigatorData> {

    private static final Logger logger = LogManager.getLogger(DiscardAssetFromInvestigatorData.class);

    public DiscardAssetFromInvestigatorAction() {
        super(BeforeAfterEvent.DISCARD_ASSET_FROM_INVESTIGATOR);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DiscardAssetFromInvestigatorData> ss) {
        ServicePlatform.get().getAssetsDeck().discard(input.getInvestigatorId(), input.getAssetInfo());
        ServicePlatform.get().getAssetListenerProvider().getAssetListener(input.getAssetInfo().getId()).unregister();
        ServicePlatform.get().getGameController().enableDiscardButton();
        ss.onSuccess(input);
    }
}
