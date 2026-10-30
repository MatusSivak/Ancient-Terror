import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.sivak.eldritchhorror.core.constants.monster.CultistMonster;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.map.monster.EpicMonsterImage;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterImage;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterImageEffect;
import sk.sivak.eldritchhorror.core.view.monster.MonsterViewImpl;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

public class MonsterDefeatPreview extends ApplicationAdapter {
    private Stage stage;
    private final java.util.List<String> audioEvents = new java.util.ArrayList<>();

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000;
        config.forceExit = false;
        new LwjglApplication(new MonsterDefeatPreview(), config);
    }

    @Override public void create() {
        try {
            Field assets = CustomAssetManager.class.getDeclaredField("instance");
            assets.setAccessible(true);
            assets.set(null, new CustomAssetManager() {
                @Override public synchronized boolean isLoaded(String name, Class type) {
                    return type == com.badlogic.gdx.audio.Sound.class || super.isLoaded(name, type);
                }
                @Override public synchronized <T> T get(String name, Class<T> type) {
                    if (type != com.badlogic.gdx.audio.Sound.class) return super.get(name, type);
                    return type.cast(java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                            (p,m,a) -> {
                                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                                if (m.getName().equals("equals")) return p == a[0];
                                if (m.getName().equals("play")) { audioEvents.add(name); return 1L; }
                                return null;
                            }));
                }
            });
            VisUI.load();
            stage = InfoStage.getStageSafe();
            Image background = new Image(CustomAssetManager.getTexture("encounter/background/combat.jpg"));
            background.setSize(960, 540);
            background.setColor(.38f, .38f, .38f, 1f);
            stage.getRoot().addActorAt(0, background);
            for (boolean epic : new boolean[]{false, true}) {
                check(epic ? "epic" : "regular", epic, false, false, false, false);
                check(epic ? "epic-fast" : "regular-fast", epic, true, false, false, false);
            }
            check("ambush", false, false, true, false, false);
            check("ambush-fast", false, true, true, false, false);
            check("speed-change", true, false, false, true, false);
            check("interrupted", true, false, false, false, true);
            check("tall", false, false, false, false, false);
            check("wide", false, false, false, false, false);
            checkRealAmbush(false);
            checkRealAmbush(true);
            System.out.println("PASS: zoom/hold, fragments, regular/epic/tall/wide, ambush, fast-forward, speed change, callbacks and GPU cleanup");
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        } finally {
            FastForwardAction.turnOff();
            Gdx.app.exit();
        }
    }

    private void check(String name, boolean epic, boolean fast, boolean ambush, boolean switchSpeed, boolean interrupt) throws Exception {
        audioEvents.clear();
        if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
        int actorsBefore = stage.getActors().size;
        int buffersBefore = bufferCount();
        Texture texture = epic ? CustomAssetManager.getEpicMonsterTexture("YigMonster")
                : CustomAssetManager.getNonEpicMonsterTexture("SkeletonMonster");
        Image monster = epic ? new EpicMonsterImage(null, null, false, new Vector2(), texture)
                : new MonsterImage(null, null, true, false, null, texture);
        if (name.equals("tall")) monster.setSize(108, 216);
        if (name.equals("wide")) monster.setSize(216, 108);
        monster.setOrigin(monster.getWidth() / 2, monster.getHeight() / 2);
        monster.setPosition(180, 120);
        float originalWidth = monster.getWidth(), originalHeight = monster.getHeight();
        monster.setScale(ambush ? 0f : 1f / .75f);
        monster.getColor().a = ambush ? 0f : 1f;
        stage.addActor(monster);
        AtomicInteger completed = new AtomicInteger();
        MonsterImageEffect.tearAppart(monster).subscribe(completed::incrementAndGet);
        require(audioEvents.size() == (fast ? 0 : 1), name + ": takeoff cue missing or duplicated");
        if (!fast) require(audioEvents.get(0).equals("sounds/monster_defeat_fly.wav"), "Wrong takeoff sound");
        Group animation = stage.getRoot().findActor("monster-defeat");
        require(animation != null, name + ": missing animation");
        float startSize = originalHeight * (ambush ? 1 : 1f / .75f);
        boolean fullSizeSeen = false, piecesSeen = false;
        for (int frame = 0; frame < 150 && completed.get() == 0; frame++) {
            if (fast || (switchSpeed && frame >= 51)) FastForwardAction.turnOn();
            else FastForwardAction.turnOff();
            stage.act(1f / 60f);
            if (frame == 44 && !fast) {
                require(!audioEvents.contains("sounds/monster_defeat.wav"), name + ": explosion sounded before shattering");
                require(monster.isVisible(), name + ": tears before the enlarged portrait can be seen");
                require(monster.getHeight() * monster.getScaleY() > (name.equals("tall") ? 540f * .70f : startSize * 1.8f),
                        name + ": zoom is barely visible");
                require(monster.getColor().a > .99f, name + ": ambush not fully visible");
                fullSizeSeen = true;
                draw(name + "-enlarged");
            }
            Actor left = animation.findActor("defeat-fragment-0");
            if (left != null) {
                require(java.util.Collections.frequency(audioEvents, "sounds/monster_defeat.wav")
                        == (fast || switchSpeed ? 0 : 1), name + ": explosion cue timing/count");
                piecesSeen = true;
                require(monster.getWidth() == originalWidth && monster.getHeight() == originalHeight,
                        name + ": snapshot changed the original dimensions");
                Actor right = animation.findActor("defeat-fragment-7");
                require(animation.findActor("defeat-fragment-31") != null, name + ": expected 32 shards");
                require(right.getX() > left.getX(), name + ": fragments do not separate");
                if (interrupt) {
                    require(bufferCount() == buffersBefore + 1, "Expected one shared temporary snapshot");
                    animation.remove();
                    require(bufferCount() == buffersBefore, "Early removal leaked GPU snapshots");
                    require(completed.get() == 0, "Interrupted sequence reported a completed defeat");
                    require(stage.getActors().size == actorsBefore, "Early removal left actors");
                    require(trackedActions() == 0, "Early removal left a fast-forward action");
                    return;
                }
            }
            if (frame == 67 && !fast && !switchSpeed) draw(name + "-split");
            if ((name.equals("regular") || name.equals("epic")) && frame % 2 == 0) {
                draw(name + "/frame-" + String.format("%03d", frame / 2));
            }
        }
        require(completed.get() == 1, name + ": completion count " + completed.get());
        require(piecesSeen, name + ": no fragments were rendered");
        require(fast || fullSizeSeen, name + ": no enlarged hold");
        require(monster.getStage() == null && stage.getActors().size == actorsBefore, name + ": actors remain");
        require(bufferCount() == buffersBefore, name + ": GPU snapshots leaked");
        require(trackedActions() == 0, name + ": fast-forward action leaked");
        System.out.println("PASS: " + name);
    }

    private void checkRealAmbush(boolean fast) throws Exception {
        AtomicInteger completed = new AtomicInteger();
        new MonsterViewImpl().defeatMonster(new CultistMonster()).subscribe(completed::incrementAndGet);
        for (int frame = 0; frame < 180 && completed.get() == 0; frame++) {
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            stage.act(1f / 60f);
        }
        require(completed.get() == 1, "Actual ambush caller did not complete");
        require(stage.getRoot().findActor("monster-defeat") == null, "Ambush animation remained");
        require(trackedActions() == 0, "Ambush left unsynchronized actions");
    }

    private int trackedActions() throws Exception {
        Field field = InfoStage.getFastForwardButton().getClass().getDeclaredField("fastForwardActions");
        field.setAccessible(true);
        return ((Map<?, ?>) field.get(InfoStage.getFastForwardButton())).size();
    }

    private static int bufferCount() {
        Matcher counts = Pattern.compile("\\d+").matcher(FrameBuffer.getManagedStatus());
        int total = 0;
        while (counts.find()) total += Integer.parseInt(counts.group());
        return total;
    }

    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.08f, .1f, .1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/monster-defeat-preview/" + name + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
