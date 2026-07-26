package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.PrivateCareAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class PrivateCareListener extends AbstractAssetListener<PrivateCareAsset> {

    private static final Logger logger = LogManager.getLogger(PrivateCareListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand((input) -> {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            showCardRequest.setTitle("Restore Health and Sanity");
            ServicePlatform.get().getGameService().showCard(showCardRequest)
                    .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
        });
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    private void onAnswer(ShowCardResponse showCardResponse, Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);

        int currentHealth = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentHealth();
        int maxHealth = ServicePlatform.get().getInvestigators().getActiveInvestigator().getInfo().getMaxHealth();
        int healthGained = maxHealth - currentHealth;
        ServicePlatform.get().getTokenService().gainHealth(healthGained);

        int currentSanity = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentSanity();
        int maxSanity = ServicePlatform.get().getInvestigators().getActiveInvestigator().getInfo().getMaxSanity();
        int sanityGained = maxSanity - currentSanity;
        ServicePlatform.get().getTokenService().gainSanity(sanityGained);

        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
