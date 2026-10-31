package sk.sivak.eldritchhorror.core.action.impl.action;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.basic.SkipActionListener;

import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SelectActionActionTest {

    @Test public void skipsTheTurnWhenEveryOtherActionIsDisabled() {
        AbstractActionPhaseAction skip = enabled(new SkipActionListener().new SkipAction(), true);
        ActionPhaseAction leaveJail = enabled(new Other(), false);
        assertSame(skip, SelectActionAction.automaticChoice(Arrays.asList(leaveJail, skip)));
    }

    @Test public void asksWhenAnotherActionIsAvailable() {
        AbstractActionPhaseAction skip = enabled(new SkipActionListener().new SkipAction(), true);
        assertNull(SelectActionAction.automaticChoice(Arrays.asList(enabled(new Other(), true), skip)));
    }

    @Test public void asksWhenTheOnlyEnabledActionIsNotSkip() {
        AbstractActionPhaseAction skip = enabled(new SkipActionListener().new SkipAction(), false);
        assertNull(SelectActionAction.automaticChoice(Arrays.asList(enabled(new Other(), true), skip)));
    }

    @Test public void aSingleEnabledActionIsChosen() {
        ActionPhaseAction only = enabled(new Other(), true);
        assertSame(only, SelectActionAction.automaticChoice(Arrays.asList(only)));
    }

    private static AbstractActionPhaseAction enabled(AbstractActionPhaseAction action, boolean enabled) {
        action.setDisabled(!enabled);
        return action;
    }

    private static class Other extends AbstractActionPhaseAction {
        @Override public void execute() { }
    }
}
