package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;

public interface TestFlavorRead {

    TestFlavorType getFlavorType();

    <T> T getFlavorData();
}
