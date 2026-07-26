package sk.sivak.eldritchhorror.core.action.impl.test;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class RegisterUsableAssetsAction extends AbstractDirectEventAction<TestData> {

    public RegisterUsableAssetsAction() {
        super(DirectEvent.REGISTER_USABLE_ASSETS);
    }

    @Override
    public void beforeOnSuccess() {
        ServicePlatform.get().getService().addEventCommand(in -> {
            input.removeDisabledUsableAssets();
        });
    }
}
