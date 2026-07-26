package sk.sivak.eldritchhorror.core.commandqueue;

/**
 * @author msivak
 */
public interface CommandQueueFacade {

    void hold();

    void release();

    void startInsertingAtCommand(String commandName);

    void clearQueue();

    void endInsertingAtCommand();

    void execute(Command... commands);

    void skipToCommand(String commandName);
}
