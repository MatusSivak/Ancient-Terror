package sk.sivak.eldritchhorror.core.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.components.tutorial.Chalkboard;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

public class GlobalThrowableHandler {

    private final Skin skin;
    private boolean exceptionHandled = false;

    public GlobalThrowableHandler(Skin skin) {
        this.skin = skin;
    }

    public void handleThrowable(Throwable t) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            t.printStackTrace(System.err);
            return;
        }
        if (exceptionHandled) {
            return;
        }
        exceptionHandled = true;

        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.EXCEPTION, t.toString());
        sendNote(t);
    }

    private void sendNote(Throwable t) {
        Chalkboard chalkboard = new Chalkboard(null);
        chalkboard.setPosition(
                ViewProperties.VIEWPORT_WIDTH / 2f - chalkboard.getWidth() / 2f,
                ViewProperties.VIEWPORT_HEIGHT / 2f - chalkboard.getHeight() / 2f);
        InfoStage.getChalkboardLayer().addActor(chalkboard);
        chalkboard.display("[RED]Ooops![]\n \nSomething went wrong.\nI will fix it ASAP.\n \nSorry :(")
                .subscribe(() -> GoogleServicesHolder.getAnalyticsTracker().uncaughtException(Thread.currentThread(), t));
    }
}
