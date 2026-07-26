package sk.sivak.eldritchhorror.core.action.impl.test;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class RegisterBonusDiceAction extends AbstractDirectEventAction<TestData> {

    public RegisterBonusDiceAction() {
        super(DirectEvent.REGISTER_BONUS_DICE);
    }
}
