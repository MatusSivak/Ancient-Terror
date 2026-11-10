import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import sk.sivak.eldritchhorror.core.constants.monster.GhoulMonster;
import sk.sivak.eldritchhorror.core.constants.monster.GoatSpawnMonster;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.combat.MonsterCombatTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.game.PrepareBoardView;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.map.gate.GateUtils;
import sk.sivak.eldritchhorror.core.view.map.gate.NewGateAnimatedImage;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Real GPU checks and deterministic film strips, without loading or modifying a saved game. */
public class GateCombatPreview extends ApplicationAdapter {
    private Stage stage;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000;
        config.forceExit = false;
        new LwjglApplication(new GateCombatPreview(), config);
    }

    @Override public void create() {
        try {
            Sound silent = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class<?>[]{Sound.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                        if (method.getName().equals("equals")) return proxy == args[0];
                        return method.getReturnType() == long.class ? 0L : null;
                    });
            Gdx.audio = (Audio) Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class<?>[]{Audio.class},
                    (proxy, method, args) -> silent);
            VisUI.load();
            stage = InfoStage.getStageSafe();
            Image background = new Image(CustomAssetManager.getTexture("encounter/background/combat.jpg"));
            background.setSize(960, 540);
            background.setColor(.38f, .38f, .38f, 1f);
            stage.getRoot().addActorAt(0, background);
            for (GateColor color : GateColor.values()) {
                checkGate(color, false, false);
                checkGate(color, true, false);
            }
            checkGate(GateColor.GREEN, false, true);
            checkWrappedGates();
            System.out.println("PASS: gate colours, continuous spin, wrapping/zoom, lazy close, fast-forward and cleanup");
            if (Boolean.getBoolean("preview.gatesOnly")) return;
            checkCombat(false);
            checkCombat(true);
            System.out.println("PASS: gate colours, layered snapshots, wrapping/zoom, lazy close, fast-forward, cleanup; combat queues, bounds, targets, lunge, shadows and interruption");
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        } finally {
            FastForwardAction.turnOff();
            Gdx.app.exit();
        }
    }

    private void checkGate(GateColor color, boolean fast, boolean interrupt) throws Exception {
        int actors = stage.getActors().size, buffers = buffers();
        NewGateAnimatedImage gate = gate(color);
        gate.setPosition(210, 170);
        gate.setScale(.8f);
        stage.addActor(gate);
        AtomicInteger done = new AtomicInteger();
        gate.closeGate().subscribe(done::incrementAndGet);
        Group effect = stage.getRoot().findActor("gate-close");
        Actor portrait = effect.findActor("closing-gate-portrait");
        require(Math.abs(portrait.getScaleX() - .8f) < .005f, "Gate jumped during stage transfer");
        boolean misted = effect.findActor("gate-mist") != null;
        Actor[] spinningLayers = {(Actor) field(effect, "core"), (Actor) field(effect, "halo")};
        float[] previousRotation = {spinningLayers[0].getRotation(), spinningLayers[1].getRotation()};
        float[] previousStep = new float[2];
        for (int frame = 0; frame < 180 && done.get() == 0; frame++) {
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            stage.act(1f / 60f);
            for (int layer = 0; layer < spinningLayers.length; layer++) {
                float step = Math.abs(spinningLayers[layer].getRotation() - previousRotation[layer]);
                if (!fast && frame >= 24 && frame <= 30) {
                    require(step > previousStep[layer] * .85f && step < previousStep[layer] * 1.15f,
                            "Gate spin changed abruptly at the collapse transition");
                }
                previousRotation[layer] = spinningLayers[layer].getRotation();
                previousStep[layer] = step;
            }
            if (frame == 20 && !fast) {
                require(portrait.isVisible() && portrait.getParent() == effect, "Gate vanished before dissipating");
                draw("gate-" + color + "-shudder");
            }
            if (interrupt && frame == 40) {
                effect.remove();
                require(done.get() == 0, "Interrupted close completed");
                break;
            }
            if ((frame == 60 || frame == 95) && !fast) draw("gate-" + color + "-dissipate-" + frame);
            if (color == GateColor.GREEN && !fast && !interrupt && frame % 2 == 0) draw("gate/frame-" + String.format("%03d", frame / 2));
        }
        require(misted && done.get() == (interrupt ? 0 : 1), "Gate close did not finish exactly once");
        require(buffers() == buffers && stage.getActors().size == actors && tracked() == 0, "Gate leaked actors, buffers or fast-forward actions");
    }

    private NewGateAnimatedImage gate(GateColor color) {
        NewGateAnimatedImage gate = new NewGateAnimatedImage(color, OmenColor.GREEN);
        gate.setSize(135, 135);
        for (int i = 0; i < 60; i++) gate.act(1f / 60f);
        return gate;
    }

    private void checkWrappedGates() throws Exception {
        Stage map = MapStage.getStage();
        map.getViewport().update(960, 540, false);
        MoveCameraToLocationHelper.setEnabled(false);
        try {
            for (int wrap = -1; wrap <= 1; wrap++) {
                Vector2 location = LocationPositionResolver.resolve(LocationId.ARKHAM);
                GateUtils.GateIdLayerResolver resolver = new GateUtils.GateIdLayerResolver(LocationId.ARKHAM);
                for (int copy = -1; copy <= 1; copy++) {
                    NewGateAnimatedImage gate = gate(GateColor.BLUE);
                    gate.setPosition(location.x + copy * ViewProperties.MAP_WIDTH - 67.5f, location.y - 67.5f);
                    MapStage.addToLayer(gate, resolver);
                }
                MapStage.getCamera().position.set(location.x + wrap * ViewProperties.MAP_WIDTH, location.y, 0);
                MapStage.getCamera().zoom = 1.25f;
                MapStage.getCamera().update();
                Completable close = new PrepareBoardView(null).closeGate(LocationId.ARKHAM);
                require(MapStage.getGateLayer().getChildren().size == 3, "Constructing close removed gates eagerly");
                AtomicInteger done = new AtomicInteger();
                close.subscribe(done::incrementAndGet);
                Actor portrait = MapStage.getGateLayer().findActor("closing-gate-portrait");
                Vector2 center = portrait.localToStageCoordinates(new Vector2(portrait.getWidth() / 2, portrait.getHeight() / 2));
                require(center.dst(location.x + wrap * ViewProperties.MAP_WIDTH, location.y) <= 1.1f, "Closing the wrong map copy: " + center);
                require(Math.abs(portrait.getScaleX() - 1f) < .005f, "Closing portrait changed scale on the map");
                for (int frame = 0; frame < 180; frame++) map.act(1f / 60f);
                require(done.get() == 1 && MapStage.getActor(resolver).isEmpty(), "Wrapped gate registry not cleared");
            }
        } finally {
            MoveCameraToLocationHelper.setEnabled(true);
        }
    }

    private void checkCombat(boolean epic) throws Exception {
        MathUtils.random.setSeed(3840);
        MonsterCombatTable table = new MonsterCombatTable();
        InfoStage.addSmallActorToInfoStage(table);
        MonsterCombatTableData data = new MonsterCombatTableData();
        data.setMonsterClassName(epic ? "YigMonster" : "SkeletonMonster");
        data.setMonsterName(epic ? "Yig" : "Skeleton"); data.setMonsterEpic(epic);
        data.setHorror(3); data.setDamage(3); data.setToughness(6); data.setCurrentHealth(6);
        data.getEncounteredMonsters().add(new GhoulMonster());
        data.getWaitingMonsters().add(new GoatSpawnMonster());
        table.init(data);
        require(table.getMonsterImage().getScaleX() < 0, "Monster did not enter facing the investigator");
        table.highlightDamageAndToughness();
        tick(45);
        Image image = table.getMonsterImage();
        Actor shadow = (Actor) field(table, "groundShadow");
        Image previous = null, next = null;
        for (Actor actor : image.getParent().getChildren()) {
            if (!(actor instanceof Image) || actor == image
                    || actor.getClass() == shadow.getClass()) continue;
            if (actor.getX() + actor.getWidth() / 2f < image.getX() + image.getWidth() / 2f) previous = (Image) actor;
            else next = (Image) actor;
        }
        require(previous != null && next != null, "Missing encounter queue");
        float startX = image.getX(), previousX = previous.getX(), nextX = next.getX();
        for (int frame = 0; frame < 360; frame++) {
            stage.act(1f / 60f);
            require(image.getRotation() == 0 && previous.getRotation() == 0 && next.getRotation() == 0,
                    "A queued monster rotated");
            require(Math.abs(image.getX() - startX) < 7.1f && Math.abs(previous.getX() - previousX) < 5.7f
                    && Math.abs(next.getX() - nextX) < 5.7f, "A monster left its place");
            require(previous.getZIndex() < image.getZIndex() && next.getZIndex() < image.getZIndex(), "Opponent is behind queue");
            checkBounds(table);
        }
        draw(epic ? "epic-queue" : "combat-queue");
        require(shadow.getParent() == image.getParent() && shadow.getZIndex() < image.getZIndex(), "Shadow is not behind monster");
        Method body = MonsterCombatTable.class.getDeclaredMethod("getMonsterBodyCenter"); body.setAccessible(true);
        Vector2 actual = image.localToStageCoordinates(new Vector2(image.getWidth() / 2, image.getHeight() / 2));
        require(actual.dst((Vector2) body.invoke(table)) < .01f, "Fireball target ignores perspective");
        Method scatter = MonsterCombatTable.class.getDeclaredMethod("randomBodyPoint", Vector2.class);
        scatter.setAccessible(true);
        com.badlogic.gdx.math.RandomXS128 random = (com.badlogic.gdx.math.RandomXS128) MathUtils.random;
        long random0 = random.getState(0), random1 = random.getState(1);
        for (int i = 0; i < 30; i++) {
            Vector2 point = (Vector2) scatter.invoke(table, actual);
            require(Float.isFinite(point.x) && Float.isFinite(point.y)
                    && Math.abs(point.x - actual.x) <= image.getImageWidth() * Math.abs(image.getScaleX()) * .18f,
                    "Mirrored bullet scatter left the artwork");
        }
        random.setState(random0, random1);
        float base = image.getScaleY();
        Method attack = MonsterCombatTable.class.getDeclaredMethod("playAttackReaction"); attack.setAccessible(true);
        attack.invoke(table);
        tick(7);
        require(image.getScaleY() > base * 1.08f, "Attack did not lunge from current depth");
        tick(30);
        require(Math.abs(image.getScaleY() - base) < .01f, "Attack reset the depth scale");
        AtomicInteger done = new AtomicInteger();
        table.destroyMonsterHealth(1, new Vector2(130, 80), null).subscribe(done::incrementAndGet);
        for (int frame = 0; frame < 600 && done.get() == 0; frame++) {
            stage.act(1f / 60f);
        }
        require(done.get() == 1, "Perspective health hit never completed");
        table.setInterrupted(true); tick(60);
        require(!image.isVisible() && !shadow.isVisible(), "Interrupted shadow remains visible");
        table.setInterrupted(false); tick(60);
        require(image.isVisible() && shadow.isVisible(), "Combat depth did not resume");
        table.remove(); tick(60);
        require(image.getStage() == null && shadow.getStage() == null && table.getStatusPlate().getStage() == null, "Combat actors leaked");
    }

    private void checkBounds(MonsterCombatTable table) {
        Image image = table.getMonsterImage();
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -minX, maxY = -minY;
        for (float x : new float[]{image.getImageX(), image.getImageX() + image.getImageWidth()}) {
            for (float y : new float[]{image.getImageY(), image.getImageY() + image.getImageHeight()}) {
                Vector2 point = image.localToStageCoordinates(new Vector2(x, y));
                minX = Math.min(minX, point.x); minY = Math.min(minY, point.y);
                maxX = Math.max(maxX, point.x); maxY = Math.max(maxY, point.y);
            }
        }
        require(minX > 0 && minY > 0 && maxX < MonsterCombatTable.SAFE_RIGHT && maxY < 540, "Scaled artwork left safe area");
        require(minX >= 480f, "Scaled artwork overlaps investigator side");
        require(table.getStatusPlate().getY() >= maxY && table.getStatusPlate().getTop() <= 540, "Plate does not follow projected artwork");
    }

    private void tick(int frames) { for (int i = 0; i < frames; i++) stage.act(1f / 60f); }
    private static Object field(Object owner, String name) throws Exception {
        Field field = owner.getClass().getDeclaredField(name); field.setAccessible(true); return field.get(owner);
    }
    private int tracked() throws Exception { return ((Map<?, ?>) field(InfoStage.getFastForwardButton(), "fastForwardActions")).size(); }
    private static int buffers() {
        Matcher counts = Pattern.compile("\\d+").matcher(FrameBuffer.getManagedStatus());
        int total = 0; while (counts.find()) total += Integer.parseInt(counts.group()); return total;
    }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.08f, .1f, .1f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/gate-combat-preview/" + name + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
