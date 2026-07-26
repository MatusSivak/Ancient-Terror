package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class BeforeDamageTestAction extends AbstractDirectEventAction<CombatData> {

    public BeforeDamageTestAction() {
        super(DirectEvent.BEFORE_DAMAGE_TEST);
    }
}
