package sk.sivak.eldritchhorror.core.view.handler;

import sk.sivak.eldritchhorror.core.view.ScreenType;

/**
 * @author msivak
 */
public interface ChangeScreenHandler {

    void changeScreen(ScreenType screenType);

    void resetInitGameView();

    /**
     * Shows the main menu after leaving a game, offering Continue when a save exists.
     */
    void showMainMenu();
}
