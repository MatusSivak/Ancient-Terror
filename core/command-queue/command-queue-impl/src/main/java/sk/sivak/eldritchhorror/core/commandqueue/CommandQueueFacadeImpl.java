package sk.sivak.eldritchhorror.core.commandqueue;

import java8.features.function.Consumer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class CommandQueueFacadeImpl implements CommandQueueFacade {

    private static final Logger logger = LogManager.getLogger(CommandQueueFacadeImpl.class);

    private int holdingCount = 0;
    private List<Command> holdedCommands = new LinkedList<>();

    @Override
    public void hold() {
        holdingCount++;
        logger.debug("Hold (" + holdingCount + ")");
    }

    @Override
    public void release() {
        holdingCount--;
        if (holdingCount == 0) {
            logger.debug("Releasing " + holdedCommands.size() + " command(s).");
            CommandQueueImpl.get().addCommands(holdedCommands.toArray(new Command[holdedCommands.size()]));
            holdedCommands.clear();
        } else {
            logger.debug("Release (" + holdingCount + ")");
        }
    }

    @Override
    public void startInsertingAtCommand(String commandName) {
        CommandQueueImpl.get().startInsertingAtCommand(commandName);
    }

    @Override
    public void clearQueue() {
        CommandQueueImpl.get().clearQueue();
    }

    @Override
    public void endInsertingAtCommand() {
        CommandQueueImpl.get().endInsertingAtCommand();
    }

    @Override
    public void execute(Command... commands) {
        if (holdingCount > 0) {
            holdedCommands.addAll(Arrays.asList(commands));
            logger.debug("Holding " + holdedCommands.size() + " commands.");
        } else {
            CommandQueueImpl.get().addCommands(commands);
        }
    }

    @Override
    public void skipToCommand(String commandName) {
        CommandQueueImpl.get().skipToCommand(commandName);
    }
}
