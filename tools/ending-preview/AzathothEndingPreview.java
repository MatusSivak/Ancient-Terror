import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import sk.sivak.eldritchhorror.core.view.components.tutorial.AzathothEndingDialog;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

/** Renders the real dialog without loading or modifying a saved game. */
public class AzathothEndingPreview extends ApplicationAdapter {
    private Stage stage;
    private int soundPlays;
    private int soundStops;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960;
        config.height = 540;
        config.x = -10000;
        config.y = -10000;
        config.forceExit = false;
        new LwjglApplication(new AzathothEndingPreview(), config);
    }

    @Override public void create() {
        String originalLanguage = UiText.getLanguage();
        com.badlogic.gdx.Audio originalAudio = Gdx.audio;
        try {
            com.badlogic.gdx.audio.Sound sound = (com.badlogic.gdx.audio.Sound) java.lang.reflect.Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[]{com.badlogic.gdx.audio.Sound.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("play")) { soundPlays++; return 1L; }
                        if (method.getName().equals("stop")) soundStops++;
                        return null;
                    });
            Gdx.audio = (com.badlogic.gdx.Audio) java.lang.reflect.Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[]{com.badlogic.gdx.Audio.class},
                    (proxy, method, args) -> method.getName().equals("newSound") ? sound : null);
            stage = new Stage(new FitViewport(960, 540));
            for (String language : new String[]{"en", "sk"}) {
                UiText.setLanguage(language);
                AzathothEndingDialog dialog = new AzathothEndingDialog();
                stage.addActor(dialog);
                boolean[] completed = {false};
                dialog.awaitDismissal().subscribe(() -> completed[0] = true);
                int previousPlays = soundPlays;
                int previousStops = soundStops;
                stage.act(1f);
                stage.act(1f);
                if (soundPlays != previousPlays + 1) throw new AssertionError("Opening sound must play once" );
                Gdx.gl.glClearColor(0.18f, 0.2f, 0.19f, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                stage.draw();

                Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
                PixmapIO.PNG png = new PixmapIO.PNG();
                png.setFlipY(true);
                png.write(Gdx.files.local("../build/ending-preview/azathoth-" + language + ".png"), pixels);
                png.dispose();
                pixels.dispose();
                checkLabels(dialog);
                stage.touchDown(10, 10, 0, 0);
                stage.touchUp(10, 10, 0, 0);
                if (completed[0]) throw new AssertionError("Background dismissed the ending");
                stage.touchDown(675, 465, 0, 0);
                stage.touchUp(675, 465, 0, 0);
                if (!completed[0] || dialog.getParent() != null) {
                    throw new AssertionError("Continue did not complete and remove the ending");
                }
                if (soundStops != previousStops + 1) throw new AssertionError("Sound must stop on dismissal" );
                System.out.println("PASS: " + language + " layout, background blocking, Continue and sound lifecycle");
            }
        } catch (Throwable failure) {
            failure.printStackTrace();
            UiText.setLanguage(originalLanguage);
            System.exit(1);
        } finally {
            UiText.setLanguage(originalLanguage);
            Gdx.audio = originalAudio;
            Gdx.app.exit();
        }
    }

    private void checkLabels(Group group) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof Label) {
                Label label = (Label) actor;
                label.validate();
                if (label.getPrefHeight() > label.getHeight() + 1) {
                    throw new AssertionError("Clipped text: " + label.getText()
                            + " needs " + label.getPrefHeight() + ", has " + label.getHeight());
                }
            }
            if (actor instanceof Group) checkLabels((Group) actor);
        }
    }

    @Override public void dispose() {
        stage.dispose();
    }
}
