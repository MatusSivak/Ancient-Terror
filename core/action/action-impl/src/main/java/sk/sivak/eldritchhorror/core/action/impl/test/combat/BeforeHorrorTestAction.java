package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

/**
 * @author msivak
 */
public class BeforeHorrorTestAction extends AbstractDirectEventAction<CombatData> {

    public BeforeHorrorTestAction() {
        super(DirectEvent.BEFORE_HORROR_TEST);
    }
}
