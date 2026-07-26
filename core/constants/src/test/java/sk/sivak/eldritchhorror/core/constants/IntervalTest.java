package sk.sivak.eldritchhorror.core.constants;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntervalTest {

    @Test
    public void justValue() {
        Interval interval = Interval.build("1");
        assertEquals("1-1", interval.toString());
        assertFalse(interval.test(0));
        assertTrue(interval.test(1));
        assertFalse(interval.test(2));

    }

    @Test
    public void simpleInterval() {
        Interval interval = Interval.build("2-3");
        assertEquals("2-3", interval.toString());
        assertFalse(interval.test(1));
        assertTrue(interval.test(2));
        assertTrue(interval.test(3));
        assertFalse(interval.test(4));
    }

    @Test
    public void openInterval() {
        Interval interval = Interval.build("4+");
        assertEquals("4+", interval.toString());
        assertFalse(interval.test(3));
        assertTrue(interval.test(4));
        assertTrue(interval.test(5));
    }
}