package sk.sivak.eldritchhorror.core.action;

import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public interface HookableAction<In, Out> extends Action<In, Out> {

    BeforeAfterEvent getBeforeAfterEvent();
}
