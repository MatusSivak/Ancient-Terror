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
import sk.sivak.eldritchhorror.core.eventtype.data.DisableCardData;

/**
 * @author msivak
 */
public class DisableCardAction extends AbstractHookableAction<DisableCardData, DisableCardData> {

    private static final Logger logger = LogManager.getLogger(DisableCardAction.class);

    public DisableCardAction() {
        super(BeforeAfterEvent.DISABLE_CARD);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DisableCardData> ss) {
        if (input.getCardInfo() instanceof AssetInfo) {
            AssetInfo assetInfo = (AssetInfo) input.getCardInfo();
            ServicePlatform.get().getAssetsDeck().disable(assetInfo);
            ServicePlatform.get().getAssetListenerProvider().getAssetListener(assetInfo.getId()).disable();
        } else if (input.getCardInfo() instanceof SpellInfo) {
            SpellInfo spellInfo = (SpellInfo) input.getCardInfo();
            ServicePlatform.get().getSpellsDeck().disable(spellInfo);
            ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).disable();
        } else if (input.getCardInfo() instanceof ArtifactInfo) {
            ArtifactInfo artifactInfo = (ArtifactInfo) input.getCardInfo();
            ServicePlatform.get().getArtifactsDeck().disable(artifactInfo);
            ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifactInfo.getId()).disable();
        }

        ss.onSuccess(input);
    }
}
