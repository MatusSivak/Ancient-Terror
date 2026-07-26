package sk.sivak.eldritchhorror.core.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaHooks;
import rx.plugins.RxJavaPlugins;
import sk.sivak.eldritchhorror.core.controller.Controller;

/**
 * @author msivak
 */
public class ViewImpl implements View {

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
