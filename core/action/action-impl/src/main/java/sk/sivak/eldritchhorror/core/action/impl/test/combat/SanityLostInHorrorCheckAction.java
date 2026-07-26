package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

/**
 * @author msivak
 */
public class SanityLostInHorrorCheckAction extends AbstractDirectEventAction<CombatData> {

    public SanityLostInHorrorCheckAction() {
        super(DirectEvent.SANITY_LOST_IN_HORROR_CHECK);
    }
}
