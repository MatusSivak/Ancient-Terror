package sk.sivak.eldritchhorror.core.commandqueue;

/**
 * @author msivak
 */
public interface QueueListener {

    void onEmptyQueue();

    void onCommandAdded(Command command);

    void onException(Throwable throwable);
}
