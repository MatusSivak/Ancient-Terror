package sk.sivak.eldritchhorror.core.constants.log;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhaseLogTest {

    @After
    public void tearDown() {
        PhaseLog.clear();
    }

    @Test
    public void startPhaseDropsPreviousPhaseLines() {
        PhaseLog.startPhase("ACTION");
        PhaseLog.append("moved");
        PhaseLog.startPhase("MYTHOS");
        PhaseLog.append("drew card");

        assertEquals("=== Phase started: MYTHOS ===\ndrew card\n", PhaseLog.snapshot());
    }

    @Test
    public void keepsOnlyTheNewestLinesWithinLimit() {
        PhaseLog.clear();
        String line = new String(new char[1023]).replace('\0', 'x');
        for (int i = 0; i < 200; i++) PhaseLog.append("#" + i + line);

        String text = PhaseLog.snapshot();
        assertTrue(text.length() <= PhaseLog.MAX_CHARS);
        assertTrue(text.endsWith("#199" + line + "\n"));
        assertFalse(text.startsWith("#0x"));
    }
}
