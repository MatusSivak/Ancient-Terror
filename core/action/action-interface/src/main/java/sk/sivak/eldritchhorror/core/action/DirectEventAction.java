package sk.sivak.eldritchhorror.core.action;

import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

/**
 * @author msivak
 */
public interface DirectEventAction<InOut> extends Action<InOut, InOut> {

    DirectEvent getDirectEvent();
}
