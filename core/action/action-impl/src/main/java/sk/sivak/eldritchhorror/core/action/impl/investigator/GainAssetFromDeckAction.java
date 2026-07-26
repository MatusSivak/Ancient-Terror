package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.model.AssetsDeckWrite;

/**
 * @author msivak
 */
public class GainAssetFromDeckAction extends AbstractHookableAction<GainAssetFromDeckData, GainAssetFromDeckData> {

    private static final Logger logger = LogManager.getLogger(GainAssetFromDeckAction.class);

    public GainAssetFromDeckAction() {
        super(BeforeAfterEvent.GAIN_ASSET_FROM_DECK);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainAssetFromDeckData> ss) {
        AssetsDeckWrite assetsDeck = ServicePlatform.get().getAssetsDeck();
        AssetInfo assetInfo = assetsDeck.find(input.getAssetId());
        if (assetInfo == null) {
            ss.onSuccess(input);
            return;
        }
        boolean isInReserve = assetsDeck.removeFromReserve(assetInfo);
        if (isInReserve) {
            assetsDeck.addToInvestigator(input.getInvestigatorId(), assetInfo);
            assetsDeck.initReserve();
        } else if (assetsDeck.removeFromDiscardPile(assetInfo)) {
            assetsDeck.addToInvestigator(input.getInvestigatorId(), assetInfo);
        } else if (assetsDeck.removeFromDeck(input.getAssetId()) != null) {
            assetsDeck.addToInvestigator(input.getInvestigatorId(), assetInfo);
        }
        AbstractAssetListener<? extends AssetInfo> assetListener = ServicePlatform.get().getAssetListenerProvider().getAssetListener(input.getAssetId());
        assetListener.registerWithOwner(input.getInvestigatorId(), assetInfo);
        ss.onSuccess(input);
    }
}
