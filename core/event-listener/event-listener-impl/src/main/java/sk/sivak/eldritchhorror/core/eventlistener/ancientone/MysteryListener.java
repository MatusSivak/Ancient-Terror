package sk.sivak.eldritchhorror.core.eventlistener.ancientone;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

public interface MysteryListener {
    void register();
    void advanceActiveMystery();

    void unregister();

    void justRegisterListeners(int progress);

    void justAddRedPins();
}
