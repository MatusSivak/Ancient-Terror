package sk.sivak.eldritchhorror.core.action.impl.test;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

public class SetTestFlavorAction implements Action<Object, Object> {

    private Object input;
    private TestFlavorRequest testFlavorRequest;

    public SetTestFlavorAction(TestFlavorRequest testFlavorRequest) {
        this.testFlavorRequest = testFlavorRequest;
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            if (testFlavorRequest.getType() == TestFlavorType.EMPTY) {
                ServicePlatform.get().getTestFlavor().setEmptyFlavor();
            } else if (testFlavorRequest.getData() == null) {
                ServicePlatform.get().getTestFlavor().setFlavor(testFlavorRequest.getType());
            } else {
                ServicePlatform.get().getTestFlavor().setFlavor(testFlavorRequest.getType(), testFlavorRequest.getData());
            }
            onSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
