package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java8.features.function.Supplier;
import java8.features.util.IterableUtils;
import java8.features.util.MapUtils;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.map.MapScrollListener;
import sk.sivak.eldritchhorror.core.view.map.MapZoomListener;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java8.features.util.MapUtils.computeIfAbsent;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_ALLOWED_OFFSET;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * @author msivak
 */
public class MapStage {

    private static MapStage instance;
    private final MapScrollListener mapScrollListener;
    private final Group mapShadowLayer;
    private final Group touchBlockerLayer;


    private MapDragListener mapDragListener;
    private MapZoomListener mapZoomListener;
    private FrameBuffer frameBuffer;
    private SpriteBatch frameBufferSpriteBatch;
    private Stage stage;
    private Stage curtainStage;
    private Group clueLayer;
    private Group redPinLayer;
    private Group stormLayer;
    private Group vortexLayer;
    private Group mapLayer;
    private Group epicMonsterLayer;
    private Group reckoningMonsterLayer;
    private Group gateLayer;
    private Group monsterLayer;
    private Group defeatedInvestigatorLayer;
    private Group investigatorLayer;
    private Group expeditionLayer;
    private Group locationHighlightLayer;
    private Group curtainLayer;
    private Group cityInfoLayer;
    private Group lostInTimeAndSpaceLayer;
    private Map<MapKey, List<Actor>> idActorMap = new HashMap<>();

    private MapStage() {
        frameBufferSpriteBatch = new SpriteBatch();
        curtainStage = new Stage(new FitViewport(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT));
        stage = new Stage(new FitViewport(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT)) {
            @Override
            public void draw() {
                int viewportWidth = getViewport().getScreenWidth();
                int viewportHeight = getViewport().getScreenHeight();
                if (viewportWidth <= 0 || viewportHeight <= 0) {
                    super.draw();
                    return;
                }
                if (frameBuffer == null
                        || frameBuffer.getWidth() != viewportWidth
                        || frameBuffer.getHeight() != viewportHeight) {
                    if (frameBuffer != null) {
                        frameBuffer.dispose();
                    }
                    frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                            viewportWidth,
                            viewportHeight, true);
                }

                frameBuffer.begin();
                Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                super.draw();
                frameBuffer.end(
                        getViewport().getLeftGutterWidth(),
                        getViewport().getBottomGutterHeight(),
                        getViewport().getScreenWidth(),
                        getViewport().getScreenHeight());
                TextureRegion textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
                textureRegion.flip(false, true);

                getBatch().begin();
                getBatch().setColor(new Color(0,0,0,
                        getMapLayer().getColor().a == 1 ? 1 : 0));
                getBatch().draw(CustomAssetManager.getTexture(CustomAssetManager.BLANK_MAP),
                        getCamera().position.x - MAP_WIDTH/2f,0,
                        MAP_WIDTH,
                        MAP_HEIGHT
                );
                getBatch().end();

                frameBufferSpriteBatch.begin();
                frameBufferSpriteBatch.setColor(Color.WHITE);
                frameBufferSpriteBatch.draw(textureRegion,
                        0,
                        0,
                        Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                frameBufferSpriteBatch.end();
            }
        };
        mapLayer = new Group() {
            @Override
            public String toString() {
                return "MapLayer";
            }
        };
        clueLayer = new Group() {
            @Override
            public String toString() {
                return "ClueLayer";
            }
        };
        redPinLayer = new Group() {
            @Override
            public String toString() {
                return "RedPinLayer";
            }
        };
        stormLayer = new Group() {
            @Override
            public String toString() {
                return "StormLayer";
            }
        };
        vortexLayer = new Group() {
            @Override
            public String toString() {
                return "VortexLayer";
            }
        };
        reckoningMonsterLayer = new Group() {
            @Override
            public String toString() {
                return "ReckoningMonsterLayer";
            }
        };
        monsterLayer = new Group() {
            @Override
            public String toString() {
                return "MonsterLayer";
            }
        };
        epicMonsterLayer = new Group() {
            @Override
            public String toString() {
                return "EpicMonsterLayer";
            }
        };
        gateLayer = new Group() {
            @Override
            public String toString() {
                return "GateLayer";
            }
        };
        investigatorLayer = new Group() {
            @Override
            public String toString() {
                return "InvestigatorLayer";
            }
        };
        defeatedInvestigatorLayer = new Group() {
            @Override
            public String toString() {
                return "DefeatedInvestigatorLayer";
            }
        };
        expeditionLayer = new Group() {
            @Override
            public String toString() {
                return "ExpeditionLayer";
            }
        };
        locationHighlightLayer = new Group() {
            @Override
            public String toString() {
                return "LocationHighlightLayer";
            }
        };
        curtainLayer = new Group() {
            @Override
            public String toString() {
                return "CurtainLayer";
            }
        };
        cityInfoLayer = new Group() {
            @Override
            public String toString() {
                return "CityInfoLayer";
            }
        };
        touchBlockerLayer = new Group() {
            @Override
            public String toString() {
                return "TouchBlockerLayer";
            }
        };
        lostInTimeAndSpaceLayer = new Group() {
            @Override
            public String toString() {
                return "LostInTimeAndSpaceLayer";
            }
        };
        mapShadowLayer = new Group() {

            @Override
            public String toString() {
                return "MapShadowLayer";
            }

            @Override
            public void act(float delta) {
                setX(getCamera().position.x - MAP_WIDTH/2f);
                super.act(delta);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
                super.draw(batch, parentAlpha);
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
        };
        mapShadowLayer.setTouchable(Touchable.disabled);

        stage.addActor(mapLayer);
        stage.addActor(expeditionLayer);
        stage.addActor(gateLayer);
        stage.addActor(stormLayer);
        stage.addActor(vortexLayer);
        stage.addActor(defeatedInvestigatorLayer);
        stage.addActor(investigatorLayer);
        stage.addActor(epicMonsterLayer);
        stage.addActor(monsterLayer);
        stage.addActor(reckoningMonsterLayer);
        stage.addActor(clueLayer);
        stage.addActor(redPinLayer);
        stage.addActor(locationHighlightLayer);
        stage.addActor(cityInfoLayer);
        stage.addActor(lostInTimeAndSpaceLayer);
        stage.addActor(mapShadowLayer);
        stage.addActor(touchBlockerLayer);

        curtainStage.addActor(curtainLayer);

        Image blankMapImage = new Image(CustomAssetManager.getTexture(CustomAssetManager.BLANK_MAP));
        blankMapImage.setSize(MAP_WIDTH, MAP_HEIGHT);
        mapShadowLayer.addActor(blankMapImage);

        mapDragListener = new MapDragListener();
        mapZoomListener = new MapZoomListener();
        mapScrollListener = new MapScrollListener();
        stage.addListener(mapDragListener);
        stage.addListener(mapZoomListener);
        stage.addListener(mapScrollListener);


    }

    public static void addDragAndZoomListeners() {
        get().stage.addListener(get().mapDragListener);
        get().stage.addListener(get().mapZoomListener);
        get().stage.addListener(get().mapScrollListener);
    }

    public static void removeDragAndZoomListeners() {
        get().stage.removeListener(get().mapDragListener);
        get().stage.removeListener(get().mapZoomListener);
        get().stage.removeListener(get().mapScrollListener);
    }

    public static void darkenWorld() {
        darkenWorld(0.5f);
    }

    public static void darkenWorld(float alpha) {
        Image curtain;
        if (get().curtainLayer.getChildren().size > 0) {
            curtain = (Image) get().curtainLayer.getChildren().get(0);
            curtain.clearActions();
        } else {
            curtain = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        }
        curtain.setPosition(-MAP_WIDTH/2f, 0);
        curtain.setTouchable(Touchable.disabled);
        curtain.setSize(MAP_WIDTH * 2, MAP_HEIGHT);
        curtain.setColor(Color.BLACK);
        curtain.getColor().a = 0f;
        get().curtainLayer.addActor(curtain);
        curtain.addAction(new FastForwardAction<>(Actions.alpha(alpha, 1)));
    }

    public static void brightenWorld() {
        if (get().curtainLayer.getChildren().size == 0) {
            return;
        }
        Actor curtain = get().curtainLayer.getChildren().get(0);
        curtain.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.alpha(0f, 1),
                Actions.run(curtain::remove)
        )));
    }

    private static class MapDragListener extends DragListener {
        private float dragStartX;
        private float dragStartY;

        public MapDragListener() {
            setTapSquareSize(0);
        }

        @Override
        public void dragStart(InputEvent event, float x, float y, int pointer) {
            dragStartX = Gdx.input.getX() - MapStage.getStage().getViewport().getLeftGutterWidth();
            dragStartY = -Gdx.input.getY() - MapStage.getStage().getViewport().getBottomGutterHeight();
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer) {
        }

        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            int inputX = Gdx.input.getX();
            int inputY = Gdx.input.getY();
            if (getActiveTouches() != 1) {
                this.dragStartX = inputX - MapStage.getStage().getViewport().getLeftGutterWidth();
                this.dragStartY = -inputY - MapStage.getStage().getViewport().getBottomGutterHeight();
                return;
            } else {
                x = inputX - MapStage.getStage().getViewport().getLeftGutterWidth();
                y = -inputY - MapStage.getStage().getViewport().getBottomGutterHeight();
            }


            OrthographicCamera camera = ((OrthographicCamera) getStage().getCamera());
            Vector3 cameraPosition = camera.position;
            float xMultiplier = (float) VIEWPORT_WIDTH / MapStage.getStage().getViewport().getScreenWidth();
            float yMultiplier = (float) VIEWPORT_HEIGHT / MapStage.getStage().getViewport().getScreenHeight();
            float newCameraX = cameraPosition.x + Math.round((dragStartX - x) * xMultiplier) * camera.zoom;
            float newCameraY = cameraPosition.y + Math.round((dragStartY - y) * yMultiplier) * camera.zoom;

            if (newCameraX < 0) {
                newCameraX += MAP_WIDTH;
            } else if (newCameraX > MAP_WIDTH) {
                newCameraX -= MAP_WIDTH;
            }

            cameraPosition.x = newCameraX;
            if (
                    newCameraY - camera.viewportHeight * camera.zoom / 2 >= -MAP_ALLOWED_OFFSET &&
                            newCameraY + camera.viewportHeight * camera.zoom / 2 <= (MAP_HEIGHT + MAP_ALLOWED_OFFSET)) {
                cameraPosition.y = newCameraY;
            }
            this.dragStartX = x;
            this.dragStartY = y;
        }

        private int getActiveTouches() {
            int activeTouch = 0;
            for (int i = 0; i < 20; i++) {
                if (Gdx.input.isTouched(i)) activeTouch++;
            }
            return activeTouch;
        }
    }

    private static MapStage get() {
        if (instance == null) {
            instance = new MapStage();
        }
        return instance;
    }

    public static void nullifyInstance() {
        if (instance != null) {
            instance.dispose();
        }
        instance = null;
    }

    public static void resize(int width, int height) {
        get().stage.getViewport().update(width, height, false);
        get().curtainStage.getViewport().update(width, height, false);
    }

    public static void render(float delta) {
        getStage().act(delta);
        getStage().draw();
        get().curtainStage.act(delta);
        get().curtainStage.draw();
    }

    public static void setWorldVisibility(boolean visibility) {
        Supplier<Action> actionSupplier = () -> Actions.sequence(
                Actions.alpha(visibility? 1f : 0.0f, 1f),
                Actions.touchable(visibility? Touchable.enabled : Touchable.disabled)
        );
        get().mapLayer.addAction(actionSupplier.get());
        get().expeditionLayer.addAction(actionSupplier.get());
        get().gateLayer.addAction(actionSupplier.get());
        get().defeatedInvestigatorLayer.addAction(actionSupplier.get());
        get().investigatorLayer.addAction(actionSupplier.get());
        get().epicMonsterLayer.addAction(actionSupplier.get());
        get().monsterLayer.addAction(actionSupplier.get());
        get().reckoningMonsterLayer.addAction(actionSupplier.get());
        get().clueLayer.addAction(actionSupplier.get());
        get().redPinLayer.addAction(actionSupplier.get());
        get().stormLayer.addAction(actionSupplier.get());
        get().vortexLayer.addAction(actionSupplier.get());
        get().locationHighlightLayer.addAction(actionSupplier.get());
        get().cityInfoLayer.addAction(actionSupplier.get());
    }

    public static Group getMapLayer() {
        return get().mapLayer;
    }

    public static Group getExpeditionLayer() {
        return get().expeditionLayer;
    }

    public static Group getGateLayer() {
        return get().gateLayer;
    }

    public static Group getInvestigatorLayer() {
        return get().investigatorLayer;
    }

    public static Group getDefeatedInvestigatorLayer() {
        return get().defeatedInvestigatorLayer;
    }

    public static Group getMonsterLayer() {
        return get().monsterLayer;
    }

    public static Group getReckoningMonsterLayer() {
        return get().reckoningMonsterLayer;
    }

    public static Group getEpicMonsterLayer() {
        return get().epicMonsterLayer;
    }

    public static Group getClueLayer() {
        return get().clueLayer;
    }

    public static Group getRedPinLayer() {
        return get().redPinLayer;
    }

    public static Group getStormLayer() {
        return get().stormLayer;
    }

    public static Group getVortexLayer() {
        return get().vortexLayer;
    }

    public static Group getLocationHighlightLayer() {
        return get().locationHighlightLayer;
    }

    public static Group getCityInfoLayer() {
        return get().cityInfoLayer;
    }

    public static Group getLostInTimeAndSpaceLayer() {
        return get().lostInTimeAndSpaceLayer;
    }

    public static Group getTouchBlockerLayer() {
        return get().touchBlockerLayer;
    }

    public static Stage getStage() {
        return get().stage;
    }

    public static OrthographicCamera getCamera() {
        return ((OrthographicCamera) get().stage.getCamera());
    }

    private void dispose() {
        clearIdActorMap();
        stage.clear();
        curtainStage.clear();
        if (frameBuffer != null) {
            frameBuffer.dispose();
            frameBuffer = null;
        }
        if (frameBufferSpriteBatch != null) {
            frameBufferSpriteBatch.dispose();
            frameBufferSpriteBatch = null;
        }
        stage.dispose();
        curtainStage.dispose();
    }

    public static <T> void addToLayer(Actor actor, IdLayerResolver<T> idLayerResolver) {
        Group layer = idLayerResolver.getLayer();
        layer.addActor(actor);
        MapKey<T> key = new MapKey<>(layer, idLayerResolver.getId());

        computeIfAbsent(get().idActorMap, key, k -> new LinkedList<>());
        get().idActorMap.get(key).add(actor);
    }

    public static <T extends Actor> List<T> getActor(IdLayerResolver idLayerResolver) {
        return (List<T>) MapUtils.getOrDefault(get().idActorMap,
                new MapKey<>(idLayerResolver.getLayer(), idLayerResolver.getId()),
                new LinkedList<>());
    }

    public static void removeActor(IdLayerResolver idLayerResolver) {
        List<Actor> actors = getActor(idLayerResolver);
        for (Actor actor : actors) {
            actor.remove();
        }
        get().idActorMap.remove(new MapKey<>(idLayerResolver.getLayer(), idLayerResolver.getId()));
    }

    public static void clearIdActorMap() {
        for (List<Actor> actors : get().idActorMap.values()) {
            IterableUtils.forEach(actors, Actor::remove);
        }
        get().idActorMap.clear();
    }

    public static <T extends Actor> List<T> getAllActors(Group layer) {
        List<T> result = new LinkedList<T>();
        for (Map.Entry<MapKey, List<Actor>> entry : get().idActorMap.entrySet()) {
            if (!entry.getKey().layer.equals(layer)) {
                continue;
            }
            result.addAll((Collection<? extends T>) entry.getValue());
        }
        return result;
    }

    public interface IdLayerResolver<T> {

        T getId();

        Group getLayer();
    }

    private static class MapKey<T> {

        private Group layer;
        private T id;

        private MapKey(Group layer, T id) {
            this.layer = layer;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MapKey<?> mapKey = (MapKey<?>) o;

            if (!layer.equals(mapKey.layer)) {
                return false;
            }
            return id.equals(mapKey.id);
        }

        @Override
        public int hashCode() {
            int result = layer.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }
    }
}
