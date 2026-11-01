import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import sk.sivak.eldritchhorror.core.constants.displayasset.RewardOfferType;
import sk.sivak.eldritchhorror.core.view.initgame.FullGamePurchaseDialog;
import sk.sivak.eldritchhorror.core.view.reward.RewardOfferDialog;

/** Renders the reward-offer and full-game dialogs offscreen to build/reward-dialog-preview. */
public class RewardDialogPreview extends ApplicationAdapter {
    private Stage stage;
    private Image backdrop;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new RewardDialogPreview(), config);
    }

    @Override public void create() {
        try {
            muteAudio();
            VisUI.load();
            stage = new Stage(new FitViewport(960, 540));
            backdrop = new Image(new Texture(Gdx.files.internal("background/splash.jpg")));
            for (RewardOfferType type : RewardOfferType.values()) {
                reset();
                RewardOfferDialog.show(stage, type, 23).subscribe();
                settle();
                draw(type == RewardOfferType.RATE_GAME ? "rate-dialog" : "video-dialog");
                Dialog dialog = findDialog();
                assertCardRatio(dialog, type);
                if (dialog.getWidth() > 960 || dialog.getHeight() > 540) throw new AssertionError("Dialog offscreen: " + type);
            }
            reset();
            FullGamePurchaseDialog.show(stage, () -> {});
            settle();
            draw("full-game-promo");
            System.out.println("PASS: reward dialogs rendered within the stage.");
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        } finally {
            Gdx.app.exit();
        }
    }

    private void reset() {
        stage.clear();
        backdrop.setSize(960, 540);
        backdrop.setColor(0.45f, 0.45f, 0.45f, 1f);
        stage.addActor(backdrop);
    }

    private void settle() { for (int frame = 0; frame < 120; frame++) stage.act(1f / 60f); }

    private void assertCardRatio(Group group, RewardOfferType type) {
        for (Actor child : group.getChildren()) {
            if (child instanceof Image && ((Image) child).getDrawable() instanceof com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
                    && ((com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable) ((Image) child).getDrawable()).getRegion().getTexture()
                    == sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture("card/card_template_locked.jpg")) {
                Image image = (Image) child;
                float texture = image.getDrawable().getMinWidth() / image.getDrawable().getMinHeight();
                float shown = image.getImageWidth() / image.getImageHeight();
                if (Math.abs(texture - shown) > 0.002f || Math.abs(image.getWidth() / image.getHeight() - texture) > 0.002f)
                    throw new AssertionError("Card aspect " + shown + " != " + texture + " in " + type);
                System.out.println("Card " + type + ": " + image.getWidth() + "x" + image.getHeight() + " ratio ok");
            }
            if (child instanceof Group) assertCardRatio((Group) child, type);
        }
    }

    private Dialog findDialog() {
        for (Actor actor : stage.getActors()) if (actor instanceof Dialog) return (Dialog) actor;
        throw new AssertionError("No dialog shown");
    }

    private void muteAudio() {
        Gdx.audio = (Audio) java.lang.reflect.Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class[]{Audio.class},
                (p, m, a) -> java.lang.reflect.Proxy.newProxyInstance(com.badlogic.gdx.audio.Sound.class.getClassLoader(),
                        new Class[]{com.badlogic.gdx.audio.Sound.class}, (s, method, args) -> {
                            if (method.getName().equals("hashCode")) return System.identityHashCode(s);
                            if (method.getName().equals("equals")) return s == args[0];
                            return method.getReturnType() == long.class ? 0L : null;
                        }));
    }

    private void draw(String name) {
        Gdx.gl.glClearColor(.06f, .09f, .07f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        try { png.write(Gdx.files.local("../build/reward-dialog-preview/" + name + ".png"), pixels); }
        catch (java.io.IOException e) { throw new RuntimeException(e); }
        png.dispose(); pixels.dispose();
    }

    @Override public void dispose() { if (stage != null) stage.dispose(); VisUI.dispose(); }
}

