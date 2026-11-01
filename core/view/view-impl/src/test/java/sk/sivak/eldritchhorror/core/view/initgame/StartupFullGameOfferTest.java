package sk.sivak.eldritchhorror.core.view.initgame;

import org.junit.Test;
import static org.junit.Assert.*;

public class StartupFullGameOfferTest {
    @Test public void offeredOnlyToReturningPlayersWithoutFullGameOncePerLaunch() {
        assertTrue(InitGameViewImpl.shouldOfferFullGameOnLaunch(false, true, false));
        assertFalse(InitGameViewImpl.shouldOfferFullGameOnLaunch(true, true, false));
        assertFalse(InitGameViewImpl.shouldOfferFullGameOnLaunch(false, false, false));
        assertFalse(InitGameViewImpl.shouldOfferFullGameOnLaunch(false, true, true));
    }
}
