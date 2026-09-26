package sk.sivak.eldritchhorror.core.model;

import org.junit.Test;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId.*;
import static sk.sivak.eldritchhorror.core.model.BackgroundModelRead.BackgroundType.*;

public class BackgroundModelTest {
    @Test public void removesAllLostBackgroundsWithoutRemovingOtherEncounters() {
        BackgroundModel model = new BackgroundModel();
        model.init();
        BackgroundData location = new BackgroundData(LOCATION, null, "location");
        BackgroundData combat = new BackgroundData(COMBAT, null, "combat");
        model.pushBackground(THE_SPY, location);
        model.pushBackground(THE_SPY, new BackgroundData(LOST_IN_TIME_AND_SPACE, null, "lost"));
        model.pushBackground(THE_SPY, combat);
        model.pushBackground(THE_SPY, new BackgroundData(LOST_IN_TIME_AND_SPACE, null, "lost"));
        model.removeBackgrounds(THE_SPY, LOST_IN_TIME_AND_SPACE);
        assertSame(combat, model.getCurrentBackground());
        assertSame(combat, model.peekBackground(THE_SPY));
        model.popBackground(THE_SPY);
        assertSame(location, model.peekBackground(THE_SPY));
        model.popBackground(THE_SPY);
        assertNull(model.peekBackground(THE_SPY));
    }

    @Test public void repeatedLostTurnsLeaveNoSpaceBackgroundAfterReturn() {
        BackgroundModel model = new BackgroundModel();
        model.init();
        for (int turn = 0; turn < 3; turn++) {
            model.pushBackground(THE_SPY, new BackgroundData(LOST_IN_TIME_AND_SPACE, null, "lost"));
            model.removeBackgrounds(THE_SPY, LOST_IN_TIME_AND_SPACE);
            assertNull(model.peekBackground(THE_SPY));
            assertNull(model.getCurrentBackground());
        }
    }

    @Test public void cleanupDoesNotChangeAnotherInvestigatorsCurrentBackground() {
        BackgroundModel model = new BackgroundModel();
        model.init();
        model.pushBackground(THE_SPY, new BackgroundData(LOST_IN_TIME_AND_SPACE, null, "lost"));
        BackgroundData other = new BackgroundData(LOCATION, null, "location");
        model.pushBackground(THE_SAILOR, other);
        model.removeBackgrounds(THE_SPY, LOST_IN_TIME_AND_SPACE);
        model.removeBackgrounds(THE_SPY, LOST_IN_TIME_AND_SPACE);
        assertSame(other, model.getCurrentBackground());
        assertSame(other, model.peekBackground(THE_SAILOR));
        assertNull(model.peekBackground(THE_SPY));
    }
}
