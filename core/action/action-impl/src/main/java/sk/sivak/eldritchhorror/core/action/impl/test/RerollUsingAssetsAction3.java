package sk.sivak.eldritchhorror.core.action.impl.test;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class RerollUsingAssetsAction3 extends AbstractDirectEventAction<TestData> {

    public RerollUsingAssetsAction3() {
        super(DirectEvent.REROLL_USING_ASSETS_3);
    }
}
