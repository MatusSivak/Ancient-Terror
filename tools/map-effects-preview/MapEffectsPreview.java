import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.game.*;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;
import sk.sivak.eldritchhorror.core.view.map.*;
import sk.sivak.eldritchhorror.core.view.map.gate.NewGateAnimatedImage;
import sk.sivak.eldritchhorror.core.view.map.clue.ClueImage;
import sk.sivak.eldritchhorror.core.view.map.helper.*;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterImage;
import sk.sivak.eldritchhorror.core.view.map.monster.EpicMonsterImage;
import sk.sivak.eldritchhorror.core.view.utils.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Offscreen GL checks of camera fading, glyph labels, omen transitions and wrapped spawn lifecycles. */
public class MapEffectsPreview extends ApplicationAdapter {
    private Stage stage;
    private int scenario;
    private final AtomicInteger starts = new AtomicInteger(), finishes = new AtomicInteger();
    private final MapStage.IdLayerResolver<String> resolver = new MapStage.IdLayerResolver<String>() {
        public String getId() { return "preview"; }
        public Group getLayer() { return MapStage.getClueLayer(); }
    };

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new MapEffectsPreview(), config);
    }

    public void create() {
        try {
            Sound silent = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class},
                    (p,m,a) -> m.getReturnType() == long.class ? 0L : null);
            Gdx.audio = (Audio) Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class[]{Audio.class}, (p,m,a) -> silent);
            VisUI.load(); InfoStage.getStageSafe(); MapStage.resize(960, 540); stage = MapStage.getStage();
            for (int i = -1; i <= 1; i++) {
                Image map = new Image(getTexture(MAP)); map.setX(i * MAP_WIDTH); MapStage.getMapLayer().addActor(map);
            }
            checkLabels(); checkOmen(); startSpawn();
        } catch (Throwable error) { fail(error); }
    }

    private void checkLabels() throws Exception {
        Method init = CompositeMap.class.getDeclaredMethod("initCityInfoLabels"); init.setAccessible(true);
        String language = UiText.getLanguage();
        try {
            for (String lang : new String[]{"en", "sk"}) {
                UiText.setLanguage(lang); MapStage.clearIdActorMap(); init.invoke(new CompositeMap());
                require(MapStage.getCityInfoLayer().getChildren().size == 45, "Expected three copies of 15 labels");
                for (LocationId id : new LocationId[]{LocationId.SAN_FRANCISCO, LocationId.ARKHAM, LocationId.THE_HEART_OF_AFRICA}) {
                    Vector2 position = LocationPositionResolver.resolve(id); camera(position.x, position.y);
                    stage.act(.01f); int visible = 0;
                    for (Actor actor : MapStage.getCityInfoLayer().getChildren()) {
                        if (actor.getColor().a != 1) continue;
                        visible++;
                        Table label = (Table) actor;
                        require(label.getWidth() < 270 && label.getHeight() < 80, "Background texture inflated label size");
                        require(Math.abs(label.getTop() - (position.y - 34)) < .01f, "Label offset");
                        require(label.getTouchable() == Touchable.disabled, "Label intercepts map clicks");
                        if (id == LocationId.SAN_FRANCISCO) {
                            String effect = ((Label)label.getChildren().get(1)).getText().toString();
                            require(!effect.contains("Observation") && effect.indexOf(FontGlyphEnricher.getGlyph("Observation")) >= 0,
                                    "Effect keyword was not converted");
                        }
                    }
                    require(visible >= 1, "Camera-centered label is hidden"); draw(lang + "-" + id);
                    camera(position.x + 300, position.y); stage.act(.01f);
                    for (Actor actor : MapStage.getCityInfoLayer().getChildren()) {
                        if (Math.abs(actor.getX() + actor.getWidth()/2 - position.x) < .01f)
                            require(actor.getColor().a == 0, "Label stays visible away from location");
                    }
                }
            }
        } finally { UiText.setLanguage(language); }
        MapStage.clearIdActorMap();
    }

    private void checkOmen() throws Exception {
        Vector2 location = LocationPositionResolver.resolve(LocationId.ARKHAM); camera(location.x, location.y);
        NewGateAnimatedImage gate = new NewGateAnimatedImage(GateColor.BLUE, OmenColor.GREEN);
        gate.setBounds(location.x - 67.5f, location.y - 67.5f, 135, 135); MapStage.getGateLayer().addActor(gate);
        for (OmenColor omen : new OmenColor[]{OmenColor.BLUE, OmenColor.BLUE, null, OmenColor.GREEN, OmenColor.BLUE}) {
            gate.updateGateTransparency(omen);
            for (int i = 0; i < 36; i++) stage.act(1f/60);
            require(gate.findActor("matching-omen-halo") == null && gate.getChildren().size == 2,
                    "Omen update added a matching halo");
        }
        draw("omen-match"); gate.remove();
    }

    private void startSpawn() {
        starts.set(0); finishes.set(0);
        Vector2 location = LocationPositionResolver.resolve(LocationId.ARKHAM);
        camera(location.x, location.y);
        // Exercise zoomed-out and vertically offset cameras as well as the normal close-up.
        if (scenario == 4) { MapStage.getCamera().zoom = 1.5f; MapStage.getCamera().position.y += 160; }
        if (scenario % 3 == 2) {
            NewGateAnimatedImage gate = new NewGateAnimatedImage(GateColor.BLUE, OmenColor.BLUE);
            gate.setBounds(location.x - 67.5f, location.y - 67.5f, 135, 135);
            MapStage.getGateLayer().addActor(gate);
            gate.act(1f);
        }
        new Spawner().spawn(center -> {
            Actor actor;
            if (scenario % 3 == 0) { actor = new ClueImage(getTexture(CLUE_TOKEN)); actor.setSize(45,45); actor.setOrigin(22.5f,22.5f); }
            else if (scenario % 3 == 1) actor = new InvestigatorImage(InvestigatorId.THE_SAILOR, null);
            else if (scenario < 3) actor = new MonsterImage(null, null, true, false, null, getNonEpicMonsterTexture("SkeletonMonster"));
            else actor = new EpicMonsterImage(null, null, false, location, getNonEpicMonsterTexture("SkeletonMonster"));
            return actor;
        }, new Spawner.SpawnConfigImpl(0,0,0,1) {
            @Override public void preSpawnConfig(Actor left, Actor center, Actor right) {
                setSpawning(true, left, center, right);
            }
            @Override public void postSpawnConfig(Actor left, Actor center, Actor right) {
                setSpawning(false, left, center, right);
            }
            @Override public Spawner.SpawnStyle getSpawnStyle() {
                return new Spawner.SpawnStyle[]{Spawner.SpawnStyle.CLUE, Spawner.SpawnStyle.INVESTIGATOR,
                        Spawner.SpawnStyle.MONSTER}[scenario % 3];
            }
        }, resolver, LocationId.ARKHAM, starts::incrementAndGet)
                .subscribe(finishes::incrementAndGet, MapEffectsPreview::fail);
        Gdx.app.postRunnable(() -> { try { checkSpawn(); } catch (Throwable error) { fail(error); } });
    }

    private void checkSpawn() throws Exception {
        java.util.List<Actor> copies = MapStage.getActor(resolver);
        require(copies.size() == 3 && MapStage.getSpawnEffectLayer().getChildren().size == 0, "Smoke appeared before landing");
        if (scenario >= 3) FastForwardAction.turnOn();
        Vector2 position = LocationPositionResolver.resolve(LocationId.ARKHAM);
        for (Actor token : copies) {
            if (scenario % 3 == 2) {
                require(Math.abs(token.getY() + token.getOriginY() - position.y) < .01f
                        && token.getColor().a == 0 && token.getScaleX() < .05f, "Monster must start inside the gate");
            } else if (scenario % 3 == 1) {
                float visibleTop = MapStage.getCamera().position.y
                        + MapStage.getCamera().viewportHeight * MapStage.getCamera().zoom / 2f;
                require(token.getY() - token.getOriginY() * .2f > visibleTop, "Investigator appeared inside the viewport");
            } else require(token.getY() + token.getOriginY() > position.y + 150, "Clue must fall toward the map");
        }
        for (int frame = 0; frame < 180; frame++) {
            stage.act(1f/60);
            if (frame > 70 && scenario % 3 == 2) {
                for (Actor actor : copies) require(Math.abs(actor.getScaleX() - 1) < .1f,
                        "Idle breathing retained the tiny spawn scale");
            }
            if (frame == 12 && scenario < 3) {
                if (scenario == 0) require(copies.get(1).getScaleY() < copies.get(1).getScaleX(), "Clue lacks foreshortening");
                if (scenario == 2) require(copies.get(1).getScaleX() < .6f
                        && copies.get(1).getY() + copies.get(1).getOriginY() < position.y + 20,
                        "Monster dropped from above instead of emerging");
                draw("spawn-" + scenario); camera(position.x - MAP_WIDTH, position.y); draw("spawn-left-" + scenario);
                camera(position.x, position.y);
            }
            if (frame == 33 && scenario < 3) {
                require(MapStage.getSpawnEffectLayer().getChildren().size == (scenario == 2 ? 0 : 3), "Unexpected landing smoke");
                draw("landing-" + scenario);
            }
        }
        require(starts.get() == 1 && finishes.get() == 1, "Spawn callbacks must run once");
        for (Actor actor : copies) require(Math.abs(actor.getScaleX() - 1) < .1f && actor.getColor().a == 1,
                "Spawn did not settle (allowing the monster's idle breathing)");
        for (Actor actor : copies) require(Math.abs(actor.getY() + actor.getOriginY() - position.y) < .01f,
                "Token missed its landing location");
        require(MapStage.getSpawnEffectLayer().getChildren().size == 0, "Smoke remained after spawn");
        MapStage.removeActor(resolver); FastForwardAction.turnOff();
        MapStage.getGateLayer().clearChildren();
        Actor interrupted = new Image(getTexture(CLUE_TOKEN)); stage.addActor(interrupted);
        MapStage.getSpawnEffectLayer().addActor(new MapSpawnEffect(interrupted)); interrupted.remove(); stage.act(.01f);
        require(MapStage.getSpawnEffectLayer().getChildren().size == 0, "Removed token left smoke behind");
        Field tracked = InfoStage.getFastForwardButton().getClass().getDeclaredField("fastForwardActions"); tracked.setAccessible(true);
        require(((Map<?,?>)tracked.get(InfoStage.getFastForwardButton())).isEmpty(), "Animation actions leaked");
        if (++scenario < 6) startSpawn();
        else { System.out.println("PASS: EN/SK labels, glyphs, camera fade, omen transitions, clue/investigator/monster spawns, wrapped copies, callbacks, fast-forward and cleanup"); Gdx.app.exit(); }
    }

    private static void setSpawning(boolean active, Actor... actors) {
        for (Actor actor : actors) {
            if (actor instanceof MonsterImage) ((MonsterImage) actor).setSpawnAnimationActive(active);
            if (actor instanceof EpicMonsterImage) ((EpicMonsterImage) actor).setSpawnAnimationActive(active);
        }
    }

    private void camera(float x, float y) { MapStage.getCamera().position.set(x,y,0); MapStage.getCamera().zoom=.65f; MapStage.getCamera().update(); }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.04f,.04f,.06f,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0,0,960,540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/map-effects-preview/" + name + ".png"),pixels); png.dispose(); pixels.dispose();
    }
    private static void require(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
    private static void fail(Throwable error) { error.printStackTrace(); System.exit(1); }
}
