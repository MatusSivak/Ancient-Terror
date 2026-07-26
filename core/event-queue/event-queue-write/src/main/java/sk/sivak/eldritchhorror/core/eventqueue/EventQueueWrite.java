package sk.sivak.eldritchhorror.core.eventqueue;

import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

import java.util.List;

/**
 * @author msivak
 */
public interface EventQueueWrite extends EventQueueRead {

    <DATA> void fireBeforeEvent(BeforeAfterEvent beforeAfterEvent, DATA data);

    <DATA> void fireAfterEvent(BeforeAfterEvent beforeAfterEvent, DATA data);

    <DATA> void fireDirectEvent(DirectEvent directEvent, DATA data);

    void init();
}
