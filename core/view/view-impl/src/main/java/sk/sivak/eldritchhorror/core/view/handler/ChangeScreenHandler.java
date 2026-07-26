package sk.sivak.eldritchhorror.core.view.handler;

import sk.sivak.eldritchhorror.core.view.ScreenType;

/**
 * @author msivak
 */
public interface ChangeScreenHandler {

    void changeScreen(ScreenType screenType);

    void resetInitGameView();
}
