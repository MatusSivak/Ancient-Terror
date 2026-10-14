package sk.sivak.eldritchhorror.core.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.game.CrashReportDialog;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

public class GlobalThrowableHandler {

    private final Skin skin;
    private boolean exceptionHandled = false;

    public GlobalThrowableHandler(Skin skin) {
        this.skin = skin;
    }

    public void handleThrowable(Throwable t) {
        sk.sivak.eldritchhorror.core.view.firebase.CrashReports.report(t);
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
        new CrashReportDialog(() -> GoogleServicesHolder.getAnalyticsTracker()
                .uncaughtException(Thread.currentThread(), t)).show(InfoStage.getStageSafe());
    }
}
