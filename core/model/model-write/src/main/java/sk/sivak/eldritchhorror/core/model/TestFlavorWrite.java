package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;

public interface TestFlavorWrite extends TestFlavorRead {

    void init();

    void setEmptyFlavor();

    void setFlavor(TestFlavorType testFlavorType);

    void setFlavor(TestFlavorType testFlavorType, Object data);

    void endFlavor();
}
