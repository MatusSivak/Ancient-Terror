package sk.sivak.eldritchhorror.core.action.impl.initgame;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

/**
 * @author msivak
 */
public class InitInvestigatorsAction extends AbstractDirectEventAction<InvestigatorId[]> {

    public InitInvestigatorsAction() {
        super(DirectEvent.INIT_INVESTIGATORS);
    }
}
