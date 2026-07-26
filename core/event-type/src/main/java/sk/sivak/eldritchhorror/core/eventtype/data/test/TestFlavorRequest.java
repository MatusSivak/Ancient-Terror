package sk.sivak.eldritchhorror.core.eventtype.data.test;

import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;

public class TestFlavorRequest {
    private TestFlavorType type;
    private Object data;

    public TestFlavorRequest() {
        this.type = TestFlavorType.EMPTY;
    }

    public TestFlavorRequest(TestFlavorType type) {
        this.type = type;
    }

    public TestFlavorRequest(TestFlavorType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public TestFlavorType getType() {
        return type;
    }

    public void setType(TestFlavorType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestFlavorRequest{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
