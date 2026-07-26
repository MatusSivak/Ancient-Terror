package sk.sivak.eldritchhorror.core.commandqueue;

import java8.features.function.Consumer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class CommandQueueImpl implements CommandQueue {

    private List<String> lastCommandsInQueue;
    private static CommandQueueImpl instance;
    private static final Logger logger = LogManager.getLogger(CommandQueueImpl.class);

    private List<Command> queue;
    private int currentIndex;

    private Consumer<Throwable> throwableConsumer;

    private List<QueueListener> queueListeners = new LinkedList<>();
    private Subscription subscription;

    private Command<Object, Object> currentCommand = null;
    private Object lastResponse = null;

    private CommandQueueImpl() {
        queue = new LinkedList<>();
        lastCommandsInQueue = new LinkedList<>();
        currentIndex = 0;
        lastResponse = null;
        currentCommand = null;

    }

    public static CommandQueueImpl get() {
        if (instance == null) {
            instance = new CommandQueueImpl();
        }
        return instance;
    }

    private int insertingPoint = 0;


    public List<String> getLastCommandsInQueue() {
        return lastCommandsInQueue;
    }

    public static void nullifyInstance() {
        instance = null;
    }

    @Override
    public void startInsertingAtCommand(String commandName) {
        for (Command command : queue) {
            if (!commandName.equals(command.getName())) {
                insertingPoint++;
                continue;
            }
            insertingPoint--;
            return;
        }
    }

    @Override
    public void endInsertingAtCommand() {
        insertingPoint = 0;
    }

    public void addCommands(Command<?, ?>[] commands) {
        int insertIndex = currentIndex;
        for (Command<?, ?> command : commands) {
            logger.debug("Adding command '" + command.getName() + "' to queue on index=" + insertIndex);
            queue.add(insertingPoint + insertIndex++, command);
            notifyOnCommandAdded(command);
        }

        initEmptyQueue(commands);
    }

    @Override
    public void addCommand(Command<?, ?> command) {
        addCommands(new Command[]{command});
    }

    private void initEmptyQueue(Command<?, ?>[] commands) {
        if (currentIndex != 0) {
            return;
        }
        logger.debug("Queue is empty, initializing...");
        increaseCurrentIndex();
        currentCommand = (Command<Object, Object>) commands[0];
        tickInternal();
//        initCommand(commands[0], null);
    }

    @Override
    public void skipToCommand(String commandName) {
        logger.info("Skipping to command '" + commandName + "'");
        Iterator<Command> iterator = queue.iterator();
        Command lastRemovedCommand = null;
        while (iterator.hasNext()) {
            Command command = iterator.next();
            if (!commandName.equals(command.getName())) {
                logger.info("Command '" + command.getName() + "' will be skipped");
                lastRemovedCommand = command;
                iterator.remove();
                continue;
            }
            if (commandName.startsWith("BEFORE_")) {
                queue.add(0, lastRemovedCommand);
            }
            return;
        }
        logger.error("Skip failed, command '" + commandName + "' not found!");
        throw new IllegalArgumentException("Skip failed, command '" + commandName + "' not found!");
    }

    public void addQueueListener(QueueListener listener) {
        queueListeners.add(listener);
    }

    private <In, Out> void logInput(Command<In, Out> command, In input) {
        if (input == null) {
            logger.info("Executing '" + command.getName() + "' without input");
        } else if (input.getClass().isArray()) {
            logger.info("Executing getget'" + command.getName() + "' with input '" + Arrays.toString((Object[]) input) + "'");
        } else {
            logger.info("Executing '" + command.getName() + "' with input '" + input + "'");
        }
        lastCommandsInQueue.add(0, command.getName());
    }

    private <In, Out> void logOutput(Command<In, Out> command, Out out) {
        if (out != null && out.getClass().isArray()) {
            logger.info("Executed '" + command.getName() + "' with output '" + Arrays.toString((Object[]) out) + "'");
        } else {
            logger.info("Executed '" + command.getName() + "' with output '" + out + "'");
        }
    }

    private void notifyOnException(Throwable ex) {
        logger.error("Exception while executing command", ex);
        for (QueueListener queueListener : queueListeners) {
            queueListener.onException(ex);
        }
        throwableConsumer.accept(ex);
    }

    private void increaseCurrentIndex() {
        currentIndex++;
        logger.debug("Increasing current index to " + currentIndex);
    }

    private void notifyOnCommandAdded(Command command) {
        for (QueueListener queueListener : queueListeners) {
            queueListener.onCommandAdded(command);
        }
    }

    private void notifyOnEmptyQueue() {
        for (QueueListener queueListener : queueListeners) {
            queueListener.onEmptyQueue();
        }
    }


    public void tick() {
        if (currentCommand != null) {
            return;
        }


        tickInternal();
    }

    private void tickInternal() {
        try {
            if (queue.size() == 0) {
                return;
            }
        currentCommand = queue.get(0);
        currentCommand.setInput(lastResponse);

            logInput(currentCommand, lastResponse);
            subscription = currentCommand.execute().subscribe(out -> {
                if (currentCommand == null) {
                    return;
                }
                if (queue.isEmpty()) {
                    return;
                }
                logOutput(currentCommand, out);
                currentCommand.doWhenCompleted(out);

                queue.remove(0);
                if (queue.isEmpty()) {
                    currentIndex = 0;
                    notifyOnEmptyQueue();
                }
                lastResponse = out;
                currentCommand = null;
                logger.debug("Queue size: " + queue.size());
            }, this::notifyOnException);
        } catch (Exception e) {
            logger.error("Error:", e);
            throwableConsumer.accept(e);
        }
    }

    public void setThrowableConsumer(Consumer<Throwable> throwableConsumer) {
        this.throwableConsumer = throwableConsumer;
    }

    @Override
    public void clearQueue() {
        queue.clear();
        subscription.unsubscribe();
        currentIndex = 0;
        insertingPoint = 0;
        instance=new CommandQueueImpl();
        instance.throwableConsumer = throwableConsumer;
        instance.queueListeners = queueListeners;
    }

}
