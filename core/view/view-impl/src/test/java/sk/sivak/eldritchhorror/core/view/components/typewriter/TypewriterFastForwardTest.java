package sk.sivak.eldritchhorror.core.view.components.typewriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.SingleSubscriber;
import rx.observers.TestSubscriber;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static org.junit.Assert.assertEquals;

public class TypewriterFastForwardTest {
    private boolean previousFastForward;
    private final CapturingQuestionTyper typer = new CapturingQuestionTyper();

    @Before public void setUp() {
        previousFastForward = FastForwardAction.isOn();
        FastForwardAction.turnOn();
    }

    @After public void tearDown() {
        if (previousFastForward) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
    }

    @Test public void okConfirmsWithoutRenderingButtons() {
        assertConfirmed("OK");
        assertEquals(0, typer.displayCount);
        assertEquals(0, typer.autoPressedCount);
    }

    @Test public void testButtonsAreShownAndAutoPressed() {
        typer.typeQuestion(new String[]{"Test"}).subscribe(new TestSubscriber<>());
        for (Stat stat : Stat.values()) {
            typer.typeQuestion(new String[]{stat.prettyString()}).subscribe(new TestSubscriber<>());
            typer.typeQuestion(new String[]{"[#GOOD]" + stat.prettyString() + "+2[]"}).subscribe(new TestSubscriber<>());
            typer.typeQuestion(new String[]{"[#BAD]" + stat.prettyString() + "-1[]"}).subscribe(new TestSubscriber<>());
        }
        assertEquals(1 + Stat.values().length * 3, typer.autoPressedCount);
        assertEquals(0, typer.displayCount);
    }

    @Test public void choicesAndOtherSingleButtonsStillWaitForInput() {
        assertWaiting("OK", "Cancel");
        assertWaiting("Lore", "Will");
        assertWaiting("Submit");
        assertWaiting("Test", "Decline");
        assertEquals(4, typer.displayCount);
    }

    @Test public void normalSpeedStillWaitsForConfirmation() {
        FastForwardAction.turnOff();
        assertWaiting("OK");
        assertWaiting("[#BAD]Will-1[]");
        assertEquals(2, typer.displayCount);
    }

    @Test public void fastForwardIsCheckedWhenPromptIsSubscribed() {
        FastForwardAction.turnOff();
        Single<Integer> prompt = typer.typeQuestion(new String[]{"OK"});
        FastForwardAction.turnOn();
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        prompt.subscribe(subscriber);
        subscriber.assertValue(0);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        assertEquals(0, typer.displayCount);
    }

    private void assertConfirmed(String text) {
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        typer.typeQuestion(new String[]{text}).subscribe(subscriber);
        subscriber.assertValue(0);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
    }

    private void assertWaiting(String... texts) {
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        typer.typeQuestion(texts).subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNotCompleted();
        subscriber.assertNoErrors();
    }

    private static class CapturingQuestionTyper extends TypewriterQuestionTyper {
        int displayCount;
        int autoPressedCount;

        CapturingQuestionTyper() { super(null); }

        @Override void displayQuestion(String[] texts, SingleSubscriber<? super Integer> subscriber) {
            displayCount++;
        }

        @Override void displayAutoPressedQuestion(String[] texts, SingleSubscriber<? super Integer> subscriber) {
            autoPressedCount++;
        }
    }
}
