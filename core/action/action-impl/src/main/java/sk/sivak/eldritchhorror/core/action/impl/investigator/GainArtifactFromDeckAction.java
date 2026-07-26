package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.AbstractArtifactListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainArtifactFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;

/**
 * @author msivak
 */
public class GainArtifactFromDeckAction extends AbstractHookableAction<GainArtifactFromDeckData, GainArtifactFromDeckData> {

    private static final Logger logger = LogManager.getLogger(GainArtifactFromDeckAction.class);

    public GainArtifactFromDeckAction() {
        super(BeforeAfterEvent.GAIN_ARTIFACT_FROM_DECK);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainArtifactFromDeckData> ss) {
        ArtifactInfo artifactInfo = ServicePlatform.get().getArtifactsDeck().removeFromDeck(input.getArtifactId());
        ServicePlatform.get().getArtifactsDeck().addToInvestigator(input.getInvestigatorId(), artifactInfo);
        AbstractArtifactListener<? extends ArtifactInfo> artifactListener = ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(input.getArtifactId());
        artifactListener.registerWithOwner(input.getInvestigatorId(), artifactInfo);
        ss.onSuccess(input);
    }
}
