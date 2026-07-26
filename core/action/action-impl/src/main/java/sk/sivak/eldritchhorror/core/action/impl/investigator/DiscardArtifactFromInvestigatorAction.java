package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardArtifactFromInvestigatorData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardAssetFromInvestigatorData;

/**
 * @author msivak
 */
public class DiscardArtifactFromInvestigatorAction extends AbstractHookableAction<DiscardArtifactFromInvestigatorData, DiscardArtifactFromInvestigatorData> {

    private static final Logger logger = LogManager.getLogger(DiscardArtifactFromInvestigatorAction.class);

    public DiscardArtifactFromInvestigatorAction() {
        super(BeforeAfterEvent.DISCARD_ARTIFACT_FROM_INVESTIGATOR);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DiscardArtifactFromInvestigatorData> ss) {
        ServicePlatform.get().getArtifactsDeck().discard(input.getInvestigatorId(), input.getArtifactInfo());
        ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(input.getArtifactInfo().getId()).unregister();
        ServicePlatform.get().getGameController().enableDiscardButton();
        ss.onSuccess(input);
    }
}
