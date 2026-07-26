package sk.sivak.eldritchhorror.core.binder;

import sk.sivak.eldritchhorror.core.controller.ControllerImpl;
import sk.sivak.eldritchhorror.core.controller.provider.ControllerProvider;
import sk.sivak.eldritchhorror.core.model.ModelWrite;
import sk.sivak.eldritchhorror.core.model.provider.ModelProvider;

/**
 * @author msivak
 */
public class ControllerModelBinder {

    private ControllerProvider controllerProvider;
    private ModelProvider modelProvider;

    public void setControllerProvider(ControllerProvider controllerProvider) {
        this.controllerProvider = controllerProvider;
    }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void bind() {
        ControllerImpl controller = controllerProvider.getController();
        ModelWrite model = modelProvider.getModel();
        controller.setModel(model);

    }
}
