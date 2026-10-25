package sk.sivak.eldritchhorror.core.eventlistener.back.condition;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import static org.junit.Assert.*;

public class ConditionBackListenersTest {
    @Test public void everyConditionBackHasAnExplicitHandler() throws Exception {
        int count = 0;
        for (ConditionId id : ConditionId.values()) {
            for (int index = 1; ; index++) {
                Class<?> back;
                try { back = Class.forName(id.getBackConditionClassName(index)); }
                catch (ClassNotFoundException endOfDeck) { break; }
                AbstractConditionInfo condition = (AbstractConditionInfo) Class.forName(id.getConditionClassName()).newInstance();
                condition.setConditionBack((ConditionBack) back.newInstance());
                AbstractConditionBackListener handler = ConditionBackListeners.create(condition);
                assertSame(condition, handler.getConditionInfo());
                assertEquals(back.getSimpleName().replace("ConditionBack", "ConditionBackListener")
                        .replace("SpaceConditionBackListener", "SpaceBackListener"), handler.getClass().getSimpleName());
                count++;
            }
        }
        assertEquals(71, count);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownBackIsReportedInsteadOfSilentlySkippingItsRules() {
        sk.sivak.eldritchhorror.core.constants.condition.paranoia.ParanoiaCondition condition =
                new sk.sivak.eldritchhorror.core.constants.condition.paranoia.ParanoiaCondition();
        condition.setConditionBack(new sk.sivak.eldritchhorror.core.constants.condition.paranoia.ParanoiaConditionBack1() {
            @Override public int getConditionBackId() { return 99; }
        });
        ConditionBackListeners.create(condition);
    }
}
