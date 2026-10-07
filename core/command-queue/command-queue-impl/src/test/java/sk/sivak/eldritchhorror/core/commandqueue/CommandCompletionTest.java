package sk.sivak.eldritchhorror.core.commandqueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.SingleSubscriber;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CommandCompletionTest {
    private CommandQueueImpl queue;
    private final List<Throwable> errors = new ArrayList<>();

    @Before public void setUp() {
        CommandQueueImpl.nullifyInstance();
        queue = CommandQueueImpl.get();
        queue.setThrowableConsumer(errors::add);
    }

    @After public void tearDown() { CommandQueueImpl.nullifyInstance(); }

    @Test public void lateEncounterSelectionCannotCompletePendingTest() {
        PendingCommand encounter = new PendingCommand("SELECT_ENCOUNTER");
        PendingCommand test = new PendingCommand("CONFIRM_TEST_RESULT");
        PendingCommand afterTest = new PendingCommand("AFTER_CONFIRM_TEST_RESULT");
        queue.addCommands(new Command[]{encounter, test, afterTest});
        encounter.subscriber.onSuccess("selected encounter");
        queue.tick();

        // A second UI callback belongs to the old selection, even while a test is pending.
        encounter.subscriber.onSuccess("skip encounter");
        queue.tick();
        assertNull("The pending test must not be removed by a stale callback", afterTest.subscriber);
        test.subscriber.onSuccess(42);
        queue.tick();
        assertEquals(42, afterTest.input);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    private static class PendingCommand extends AbstractCommand<Object, Object> {
        private final String name;
        private SingleSubscriber<? super Object> subscriber;
        PendingCommand(String name) { this.name = name; }
        @Override public String getName() { return name; }
        @Override public Single<Object> execute() { return Single.create(s -> subscriber = s); }
    }
}
