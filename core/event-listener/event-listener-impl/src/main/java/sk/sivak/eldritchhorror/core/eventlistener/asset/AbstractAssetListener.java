package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueRead;

import java.util.List;

/**
 * @author msivak
 */
public abstract class AbstractAssetListener<T extends AssetInfo> implements AssetListener<T> {

    private static final Logger logger = LogManager.getLogger(AbstractAssetListener.class);

    protected InvestigatorId investigatorId;
    protected boolean enabled = true;
    private T assetInfo;

    @Override
    public T getAssetInfo() {
        return assetInfo;
    }

    protected abstract List<EventListener> getEventListeners();

    public void changeOwner(InvestigatorId investigatorId) {
        if (this.investigatorId == investigatorId) {
            return;
        }
        this.investigatorId = investigatorId;
        logger.info("Changing owner of asset: " + getAssetInfo().getName() + " to " + investigatorId);
    }

    public void registerWithOwner(InvestigatorId investigatorId, AssetInfo assetInfo) {
        this.investigatorId = investigatorId;
        this.assetInfo = ((T) assetInfo);
        logger.info("Registering asset : " + getAssetInfo().getName());
        register();
    }

    public void justLoadWithOwner(InvestigatorId investigatorId, AssetInfo assetInfo) {
        this.investigatorId = investigatorId;
        this.assetInfo = ((T) assetInfo);
        logger.info("Registering asset : " + getAssetInfo().getName());
        justLoad();
    }


    protected EventQueueRead getEventQueue() {
        return ServicePlatform.get().getEventQueue();
    }

    protected boolean isTestFlavorOfType(TestFlavorType testFlavorType) {
        return ServicePlatform.get().getTestFlavor().getFlavorType() == testFlavorType;
    }


    protected abstract void register();

    protected void justLoad() {
        register();
    }

    public void unregister() {
        for (EventListener eventListener : getEventListeners()) {
            getEventQueue().unregisterListener(eventListener);
        }
    }

    public boolean gainCard() {
        return true;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public InvestigatorId getOwner() {
        return investigatorId;
    }
}
