package sk.sivak.eldritchhorror.core.action.impl.test;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class UpdateScoreUsingAssets extends AbstractDirectEventAction<TestData> {

    public UpdateScoreUsingAssets() {
        super(DirectEvent.UPDATE_SCORE_USING_ASSETS);
    }
}
