package sk.sivak.eldritchhorror.core.action.impl.test;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.AddOneToDieResultData;

/**
 * @author msivak
 */
public class AddOneToDieResultEventAction extends AbstractDirectEventAction<AddOneToDieResultData> {

    public AddOneToDieResultEventAction() {
        super(DirectEvent.ADD_ONE_TO_DIE_RESULT);
    }
}
