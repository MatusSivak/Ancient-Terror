package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.EnableCardData;

/**
 * @author msivak
 */
public class EnableCardAction extends AbstractHookableAction<EnableCardData, EnableCardData> {

    private static final Logger logger = LogManager.getLogger(EnableCardAction.class);

    public EnableCardAction() {
        super(BeforeAfterEvent.ENABLE_CARD);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super EnableCardData> ss) {
        if (input.getCardInfo() instanceof AssetInfo) {
            AssetInfo assetInfo = (AssetInfo) input.getCardInfo();
            ServicePlatform.get().getAssetsDeck().enable(assetInfo);
            ServicePlatform.get().getAssetListenerProvider().getAssetListener(assetInfo.getId()).enable();
        } else if (input.getCardInfo() instanceof SpellInfo) {
            SpellInfo spellInfo = (SpellInfo) input.getCardInfo();
            ServicePlatform.get().getSpellsDeck().enable(spellInfo);
            ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).enable();
        } else if (input.getCardInfo() instanceof ArtifactInfo) {
            ArtifactInfo artifactInfo = (ArtifactInfo) input.getCardInfo();
            ServicePlatform.get().getArtifactsDeck().enable(artifactInfo);
            ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifactInfo.getId()).enable();
        }

        ss.onSuccess(input);
    }
}
