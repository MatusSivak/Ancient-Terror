package sk.sivak.eldritchhorror.core.eventlistener.ancientone;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

import java.util.Collections;
import java.util.List;

public abstract class AbstractMysteryListener implements MysteryListener {

    protected MysteryCardInfo mysteryCardInfo;

    private boolean spawnRedPins = true;
    public AbstractMysteryListener(MysteryCardInfo mysteryCardInfo) {
        this.mysteryCardInfo = mysteryCardInfo;
        this.mysteryCardInfo.setProgressSupplier(this::getProgress);
        if (this.mysteryCardInfo.getPinLocations() == null) {
            spawnRedPins = false;
            this.mysteryCardInfo.setPinLocationsSupplier(this::getPinLocations);
        }
    }

    protected List<LocationId> getPinLocations() {
        return Collections.emptyList();
    }

    @Override
    public void register() {
        ServicePlatform.get().getGameService().hold();
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
        if (spawnRedPins) {
            ServicePlatform.get().getGameService().spawnRedPins();
        }
        ServicePlatform.get().getGameService().release();
    }

    @Override
    public abstract void unregister();

    protected abstract int getProgress();
}
