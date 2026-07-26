package sk.sivak.eldritchhorror.core.commandqueue;

/**
 * @author msivak
 */
public interface CommandQueue {

    void startInsertingAtCommand(String commandName);

    void endInsertingAtCommand();

    void addCommands(Command<?, ?>[] commands);

    void addCommand(Command<?, ?> command);

    void addQueueListener(QueueListener listener);

    void skipToCommand(String commandName);

    void clearQueue();
}
