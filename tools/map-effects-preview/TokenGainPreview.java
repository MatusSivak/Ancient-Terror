import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import sk.sivak.eldritchhorror.core.view.action.GainTokenHelper;
import sk.sivak.eldritchhorror.core.view.components.ItemActor;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Real GL exercise of generated resources, their HUD destination and action cleanup. */
public class TokenGainPreview extends ApplicationAdapter {
    private Stage stage;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new TokenGainPreview(), config);
    }
    @Override public void create() {
        try {
            Sound silent = (Sound)Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class},
                    (p,m,a) -> m.getReturnType() == long.class ? 0L : null);
            Gdx.audio = (Audio)Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class[]{Audio.class}, (p,m,a) -> silent);
            VisUI.load(); stage = InfoStage.getStageSafe();
            Image background = new Image(getTexture("encounter/background/mythos.jpg"));
            background.setBounds(0,0,960,540); background.setColor(.32f,.32f,.36f,1);
            stage.getRoot().addActorAt(0, background);
            for (String texture : new String[]{CLUE_TOKEN, HEALTH_ICON, SANITY_ICON, FOCUS_TOKEN}) {
                for (int mode = 0; mode < 4; mode++) check(texture, mode);
            }
            require(tracked() == 0, "Fast-forward action leak");
            System.out.println("PASS: generated clue/health/sanity/focus, normal/fast/speed-change/large-delta, HUD count, destination and cleanup");
        } catch (Throwable error) { error.printStackTrace(); System.exit(1); }
        finally { FastForwardAction.turnOff(); Gdx.app.exit(); }
    }
    private void check(String texture, int mode) throws Exception {
        FastForwardAction.turnOff();
        ContainerBar bar = InfoStage.getInvestigatorHud().getFocusBar();
        bar.setTotalEmptyContainers(3); bar.setCurrentValue(0); bar.validate();
        Vector2 destination = new Vector2(bar.getEmptyContainerPosition(0));
        AtomicInteger done = new AtomicInteger();
        GainTokenHelper.gainTokenAndUpdateHud(1, bar, texture, "").subscribe(done::incrementAndGet);
        ItemActor token = findToken(stage.getRoot());
        require(token != null && token.getColor().a == 0, "Token appears before reveal");
        require(token.getX() + token.getOriginX() == 480 && token.getY() + token.getOriginY() == 270,
                "Reveal is not centered");
        for (int frame = 0; frame < 150; frame++) {
            if (mode == 1 || (mode == 2 && frame == 22)) FastForwardAction.turnOn();
            stage.act(mode == 3 ? 4 : 1f/60);
            if (mode == 0 && (frame == 8 || frame == 24 || frame == 42 || frame == 78))
                draw(texture.substring(texture.lastIndexOf('/') + 1).replace(".png", "") + "-" + frame);
            if (mode == 0 && frame == 24) require(done.get() == 0 && bar.getCurrentValue() == 0,
                    "HUD incremented before the token arrived");
        }
        require(done.get() == 1 && bar.getCurrentValue() == 1, "Gain completed more than once");
        require(Math.abs(token.getX()-destination.x)<.01f && Math.abs(token.getY()-destination.y)<.01f,
                "Token missed HUD destination");
        require(token.getStage() == null && tracked() == 0, "Token or actions remained after completion");
    }
    private ItemActor findToken(Group group) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof ItemActor) return (ItemActor)actor;
            if (actor instanceof Group) { ItemActor found = findToken((Group)actor); if (found != null) return found; }
        }
        return null;
    }
    private int tracked() throws Exception {
        Field field = InfoStage.getFastForwardButton().getClass().getDeclaredField("fastForwardActions");
        field.setAccessible(true); return ((Map<?,?>)field.get(InfoStage.getFastForwardButton())).size();
    }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.04f,.04f,.06f,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0,0,960,540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/token-gain-preview/"+name+".png"),pixels); png.dispose(); pixels.dispose();
    }
    private static void require(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
}
