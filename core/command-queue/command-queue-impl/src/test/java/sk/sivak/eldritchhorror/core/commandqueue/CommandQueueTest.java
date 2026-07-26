package sk.sivak.eldritchhorror.core.commandqueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.SingleSubscriber;

/**
 * @author msivak
 */
public class CommandQueueTest {

    private static final Logger logger = LogManager.getLogger(CommandQueueTest.class);

    private boolean finished = false;
    private CommandQueueImpl commandQueue;

    @Before
    public void init() {
        CommandQueueImpl.nullifyInstance();
        commandQueue = CommandQueueImpl.get();
        commandQueue.setThrowableConsumer(ex -> logger.error("Command queue test error", ex));
        commandQueue.addQueueListener(new QueueListener() {

            @Override
            public void onEmptyQueue() {
                finished = true;
            }

            @Override
            public void onCommandAdded(Command command) {
                finished = false;
            }

            @Override
            public void onException(Throwable throwable) {
                finished = true;
            }
        });
    }

    @Test
    public void simpleAddSyncCommands() {
        Command<String, String> c1 = new SimpleSyncCommand<>("C1", "Output-1");
        Command<String, String> c2 = new SimpleSyncCommand<>("C2", "Output-2");
        Command<String, String> c3 = new SimpleSyncCommand<>("C3", "Output-3");

        c3.addOnCompletedListener(new SingleSubscriber<String>() {

            @Override
            public void onSuccess(String s) {
                logger.debug("Success!");
            }

            @Override
            public void onError(Throwable error) {
                logger.debug("Error!");
            }
        });

        commandQueue.addCommand(c1);
        commandQueue.addCommands(new Command[]{c2, c3});
    }

    @Test
    public void multipleAddSyncCommands() {
        Command<String, String> c1 = new SimpleSyncCommand<>("C1", "Output-1");
        Command<String, String> c2 = new SimpleSyncCommand<>("C2", "Output-2");
        Command<String, String> c3 = new SimpleSyncCommand<>("C3", "Output-3");

        commandQueue.addCommands(new Command[]{c1, c2, c3});

        Command<String, String> c4 = new SimpleSyncCommand<>("C4", "Output-4");
        Command<String, String> c5 = new SimpleSyncCommand<>("C5", "Output-5");
        Command<String, String> c6 = new SimpleSyncCommand<>("C6", "Output-6");

        commandQueue.addCommands(new Command[]{c4, c5, c6});
    }

    @Test
    public void simpleAddAsyncCommands() {
        Command<String, String> c1 = new SimpleASyncCommand<>("C1", "Output-1");
        Command<String, String> c2 = new SimpleASyncCommand<>("C2", "Output-2");
        Command<String, String> c3 = new SimpleASyncCommand<>("C3", "Output-3");

        commandQueue.addCommands(new Command[]{c1, c2, c3});
    }

    @Test
    public void multipleAddAsyncCommands() {
        Command<String, String> c1 = new SimpleASyncCommand<>("C1", "Output-1");
        Command<String, String> c2 = new SimpleASyncCommand<>("C2", "Output-2");
        Command<String, String> c3 = new SimpleASyncCommand<>("C3", "Output-3");

        commandQueue.addCommands(new Command[]{c1, c2, c3});

        Command<String, String> c4 = new SimpleASyncCommand<>("C4", "Output-4");
        Command<String, String> c5 = new SimpleASyncCommand<>("C5", "Output-5");
        Command<String, String> c6 = new SimpleASyncCommand<>("C6", "Output-6");

        commandQueue.addCommands(new Command[]{c4, c5, c6});
    }

    @Test
    public void skipToCommandTest() {
        Command<String, String> c1 = new SimpleSyncCommand<>("C1", "Output-1");
        Command<String, String> c2 = new SimpleSyncCommand<>("C2", "Output-2");
        SimpleSkipCommand<String> skip = new SimpleSkipCommand<>("CSKIP1");
        skip.setCommandQueue(commandQueue);
        skip.setNextCommandName("C4");
        Command<String, String> c3 = new SimpleSyncCommand<>("C3", "Output-3");
        Command<String, String> c4 = new SimpleSyncCommand<>("C4", "Output-4");
        Command<String, String> c5 = new SimpleSyncCommand<>("C5", "Output-5");

        commandQueue.addCommands(new Command[]{c1, c2, skip, c3, c4, c5});
    }

    @Test
    public void skipToCommandFailTest() {
        Command<String, String> c1 = new SimpleSyncCommand<>("C1", "Output-1");
        Command<String, String> c2 = new SimpleSyncCommand<>("C2", "Output-2");
        SimpleSkipCommand<String> skip = new SimpleSkipCommand<>("CSKIP1");
        skip.setCommandQueue(commandQueue);
        skip.setNextCommandName("CX");
        Command<String, String> c3 = new SimpleSyncCommand<>("C3", "Output-3");
        Command<String, String> c4 = new SimpleSyncCommand<>("C4", "Output-4");
        Command<String, String> c5 = new SimpleSyncCommand<>("C5", "Output-5");

        commandQueue.addCommands(new Command[]{c1, c2, skip, c3, c4, c5});
    }

    @After
    public void waitUntilFinished() {
        while (!finished) {
            commandQueue.tick();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Queue is empty");
    }
}
