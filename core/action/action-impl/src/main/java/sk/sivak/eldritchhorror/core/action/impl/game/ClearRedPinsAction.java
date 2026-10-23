package sk.sivak.eldritchhorror.core.action.impl.game;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class ClearRedPinsAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(ClearRedPinsAction.class);

    private final LocationId locationId;

    public ClearRedPinsAction() {
        this(null);
    }

    /** Clears only the pin at {@code locationId}; {@code null} clears all pins of the current mystery. */
    public ClearRedPinsAction(LocationId locationId) {
        this.locationId = locationId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            if (locationId == null) {
                ServicePlatform.get().getGameController().clearRedPins();
            } else {
                ServicePlatform.get().getGameController().clearRedPin(locationId);
            }
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
