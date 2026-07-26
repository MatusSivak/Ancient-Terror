package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

/**
 * @author msivak
 */
public class JustAfterDamageTestAction extends AbstractDirectEventAction<CombatData> {

    public JustAfterDamageTestAction() {
        super(DirectEvent.JUST_AFTER_DAMAGE_TEST);
    }
}
