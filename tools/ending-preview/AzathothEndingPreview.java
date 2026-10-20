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
import sk.sivak.eldritchhorror.core.view.components.tutorial.AncientOneEndingDialog;
import sk.sivak.eldritchhorror.core.view.components.tutorial.YigEndingDialog;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.initgame.InitGameViewImpl;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

/** Renders the real dialog without loading or modifying a saved game. */
public class AzathothEndingPreview extends ApplicationAdapter {
    private Stage stage;
    private int soundPlays;
    private int soundStops;
    private int soundDisposals;
    private String playedSoundPath;

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
            Gdx.audio = (com.badlogic.gdx.Audio) java.lang.reflect.Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[]{com.badlogic.gdx.Audio.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("newSound"))
                            throw new AssertionError("Ending must not depend on asynchronous SoundPool loading");
                        if (!method.getName().equals("newMusic")) return null;
                        String path = ((com.badlogic.gdx.files.FileHandle)args[0]).path();
                        if (!((com.badlogic.gdx.files.FileHandle)args[0]).exists()) throw new AssertionError("Missing sound: " + path);
                        return java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                                new Class<?>[]{com.badlogic.gdx.audio.Music.class}, (p,m,a) -> {
                                    if (m.getName().equals("play")) { playedSoundPath=path; soundPlays++; }
                                    if (m.getName().equals("stop")) soundStops++;
                                    if (m.getName().equals("dispose")) soundDisposals++;
                                    if (m.getName().equals("setLooping") && (Boolean)a[0])
                                        throw new AssertionError("Ending cue must not loop");
                                    return null;
                                });
                    });
            stage = InfoStage.getStageSafe();
            InitGameViewImpl view = new InitGameViewImpl();
            for (String ancient : new String[]{"azathoth", "yig"}) {
            for (String language : new String[]{"en", "sk"}) {
                setPreviewLanguage(language);
                int[] completed = {0};
                view.displayChalkboard("ancientOne." + ancient + ".endGame", 480, 270)
                        .subscribe(() -> completed[0]++);
                AncientOneEndingDialog dialog = (AncientOneEndingDialog) InfoStage.getChalkboardLayer().getChildren().peek();
                if (ancient.equals("yig") && !(dialog instanceof YigEndingDialog))
                    throw new AssertionError("Yig defeat did not route to its illustrated dialog");
                int previousPlays = soundPlays;
                int previousStops = soundStops;
                int previousDisposals = soundDisposals;
                stage.act(1f);
                stage.act(1f);
                if (soundPlays != previousPlays + 1) throw new AssertionError("Opening sound must play once" );
                if (!"sounds/ancient_one_ending.wav".equals(playedSoundPath))
                    throw new AssertionError("Defeat played wrong cue: " + playedSoundPath);
                Gdx.gl.glClearColor(0.18f, 0.2f, 0.19f, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                stage.draw();

                Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
                PixmapIO.PNG png = new PixmapIO.PNG();
                png.setFlipY(true);
                png.write(Gdx.files.local("../build/ending-preview/" + ancient + "-" + language + ".png"), pixels);
                png.dispose();
                pixels.dispose();
                checkLabels(dialog);
                stage.touchDown(10, 10, 0, 0);
                stage.touchUp(10, 10, 0, 0);
                if (completed[0] != 0) throw new AssertionError("Background dismissed the ending");
                stage.touchDown(675, 465, 0, 0);
                stage.touchUp(675, 465, 0, 0);
                if (completed[0] != 1 || dialog.getParent() != null) {
                    throw new AssertionError("Continue did not complete and remove the ending");
                }
                if (soundStops != previousStops + 1) throw new AssertionError("Sound must stop on dismissal" );
                if (soundDisposals != previousDisposals + 1) throw new AssertionError("Music must be disposed on dismissal");
                stage.touchDown(675, 465, 0, 0);
                stage.touchUp(675, 465, 0, 0);
                if (completed[0] != 1) throw new AssertionError("Ending completed twice");
                view.hideChalkboard();
                System.out.println("PASS: " + ancient + " " + language + " layout, background blocking, Continue and sound lifecycle");
            }
            }
        } catch (Throwable failure) {
            failure.printStackTrace();
            setPreviewLanguage(originalLanguage);
            System.exit(1);
        } finally {
            setPreviewLanguage(originalLanguage);
            Gdx.audio = originalAudio;
            Gdx.app.exit();
        }
    }

    private void setPreviewLanguage(String language) {
        com.badlogic.gdx.Application app = Gdx.app;
        try { Gdx.app = null; UiText.setLanguage(language); }
        finally { Gdx.app = app; }
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
