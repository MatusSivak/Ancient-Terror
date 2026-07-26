package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.SilverTwilightRitualAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class SilverTwilightRitualListener extends AbstractAssetListener<SilverTwilightRitualAsset> {

    private static final Logger logger = LogManager.getLogger(SilverTwilightRitualListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand(this::askQuestion);
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    private void askQuestion(Object input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        showCardRequest.setTitle("Retreat Doom.");
        ServicePlatform.get().getGameService().showCard(showCardRequest)
                .subscribe(showCardResponse -> ok(input));
    }

    private void ok(Object input) {
        retreatDoom(input);
    }

    private void retreatDoom(Object input) {
        ServicePlatform.get().getGameService().hold();
        ServicePlatform.get().getDoomOmenService().retreatDoom();
        discard(input);
        ServicePlatform.get().getGameService().release();
    }

    private void discard(Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
