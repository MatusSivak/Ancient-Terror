package sk.sivak.eldritchhorror.core.model.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.model.TestFlavorWrite;

import java.util.Stack;

public class TestFlavor implements TestFlavorWrite {

    private static final Logger logger = LogManager.getLogger(TestFlavor.class);

    private Stack<TestFlavorEntry> testFlavorEntryStack;

    @Override
    public TestFlavorType getFlavorType() {
        if (testFlavorEntryStack.isEmpty()) {
            return null;
        }
        TestFlavorEntry peek = testFlavorEntryStack.peek();
        if (peek == null) {
            return null;
        }
        return peek.testFlavorType;
    }

    @Override
    public void init() {
        testFlavorEntryStack = new Stack<>();
    }

    @Override
    public <T> T getFlavorData() {
        return (T) testFlavorEntryStack.peek().data;
    }

    @Override
    public void setEmptyFlavor() {
        setFlavor(TestFlavorType.EMPTY, null);
    }

    @Override
    public void setFlavor(TestFlavorType testFlavorType) {
        setFlavor(testFlavorType, null);
    }

    @Override
    public void setFlavor(TestFlavorType testFlavorType, Object data) {
        if (data == null) {
            logger.info("Setting " + testFlavorType + " test flavor");
        } else {
            logger.info("Setting " + testFlavorType + " test flavor with data of class " + data.getClass().getSimpleName());
        }
        testFlavorEntryStack.push(new TestFlavorEntry(data, testFlavorType));
    }

    @Override
    public void endFlavor() {
        TestFlavorEntry pop = testFlavorEntryStack.pop();
        logger.info("Unsetting " + pop.testFlavorType + " test flavor");
    }

    private class TestFlavorEntry {

        private Object data;
        private TestFlavorType testFlavorType;

        private TestFlavorEntry(Object data, TestFlavorType testFlavorType) {
            this.data = data;
            this.testFlavorType = testFlavorType;
        }
    }
}
