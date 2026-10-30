package sk.sivak.eldritchhorror.core.view.action;

import com.badlogic.gdx.graphics.Texture;
import org.junit.After;
import org.junit.Before;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import java.lang.reflect.Proxy;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import static org.junit.Assert.*;

public class PrePlayedTokenLossTest {
    private Files previousFiles;
    @Before public void setup() {
        previousFiles = Gdx.files;
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                (proxy, method, args) -> { throw new UnsupportedOperationException(method.getName()); });
    }
    @After public void cleanup() { PrePlayedTokenLoss.reset(); Gdx.files = previousFiles; }

    @Test public void spellInterruptionRestoresOnlyVisualLossOnce() {
        TestBar bar = new TestBar(4);
        loseHealth(bar); loseHealth(bar);
        assertEquals(2, bar.value);
        PrePlayedTokenLoss.undo();
        assertEquals(4, bar.value);
        PrePlayedTokenLoss.undo();
        assertEquals(4, bar.value);
        assertEquals(0, PrePlayedTokenLoss.consumeHealth(2));
    }

    @Test public void fleshWardAfterHudRefreshDoesNotRestoreASecondTime() {
        TestBar bar = new TestBar(4);
        loseHealth(bar);
        bar.refresh(4);
        PrePlayedTokenLoss.undo();
        assertEquals(4, bar.value);
        assertEquals(0, PrePlayedTokenLoss.consumeHealth(1));
    }

    @Test public void refreshedInjuredOrDifferentInvestigatorDoesNotReceiveOldTokens() {
        TestBar bar = new TestBar(4);
        loseHealth(bar);
        bar.refresh(3); // Same value as the preview, but different authoritative contents.
        PrePlayedTokenLoss.undo();
        assertEquals(3, bar.value);
    }

    @Test public void authoritativeHudRefreshRequiresTheRealLossAnimation() {
        TestBar bar = new TestBar(4);
        loseHealth(bar);
        bar.refresh(4);
        assertEquals(0, PrePlayedTokenLoss.consumeHealth(1));
    }

    @Test public void uninterruptedDamageIsConsumedOnceAndHealthAndSanityStayIndependent() {
        TestBar health = new TestBar(4), sanity = new TestBar(6);
        loseHealth(health); loseHealth(health);
        PrePlayedTokenLoss.recordSanity(sanity); sanity.value--;
        assertEquals(2, PrePlayedTokenLoss.consumeHealth(2));
        assertEquals(0, PrePlayedTokenLoss.consumeHealth(2));
        PrePlayedTokenLoss.undo();
        assertEquals(2, health.value);
        assertEquals(6, sanity.value);
    }

    @Test public void unexecutedVisualLossCannotRestoreOrSuppressDamage() {
        TestBar bar = new TestBar(4);
        PrePlayedTokenLoss.recordHealth(bar);
        PrePlayedTokenLoss.undo();
        assertEquals(4, bar.value);
        assertEquals(0, PrePlayedTokenLoss.consumeHealth(1));
    }

    private static void loseHealth(TestBar bar) {
        PrePlayedTokenLoss.recordHealth(bar); bar.value--;
    }

    /** No textures/GL needed to exercise the ownership and rollback rules. */
    private static final class TestBar extends ContainerBar {
        int value;
        long generation;
        TestBar(int value) { super((Texture) null); this.value = value; }
        void refresh(int value) { generation++; this.value = value; }
        @Override public int getCurrentValue() { return value; }
        @Override public void setCurrentValue(int value) { this.value = value; }
        @Override public long getContentsGeneration() { return generation; }
    }
}
