package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java8.features.stream.Stream;
import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.Path;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FADING_EFFECT_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.LOCATION_BUTTON_DOWN;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.LOCATION_BUTTON_UP;
import static sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper.getDistance;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

/**
 * @author msivak
 */
public class TravelHelper {

    public static final int BORDER = 200;
    private TextButton stopButton;
    private LabelTable stopTravelingTable;

    public void selectLocation(LocationId currentLocationId,
                               List<LocationInfo.Connection> connections,
                               boolean optional,
                               SingleSubscriber<? super LocationInfo.Connection> subscriber) {

        Vector2 currentPosition = LocationPositionResolver.resolve(currentLocationId);

        List<ConnectionPosition> connectionPositions = new LinkedList<>();
        List<ConnectionPosition> allConnectionPositions = new LinkedList<>();
        for (LocationInfo.Connection connection : connections) {
            connectionPositions.add(findNearestPosition(connection, currentPosition));
        }
        for (LocationInfo.Connection connection : connections) {
            allConnectionPositions.addAll(toConnectionPosition(connection, currentPosition));
        }
        ConnectionPosition currentPositionAsConnectionPosition = findNearestPosition(createConnectionFromCurrentLocation(currentLocationId), currentPosition);
        connectionPositions.add(currentPositionAsConnectionPosition);

        Bounds bounds = createBounds(Stream.map(connectionPositions, connectionPosition -> connectionPosition.position));

        float zoomY = (bounds.getHeight() + BORDER) / MapStage.getCamera().viewportHeight;
        float zoomX = (bounds.getWidth() + BORDER) / MapStage.getCamera().viewportWidth;

        Completable.create(onComplete -> {
            MoveCameraToLocationHelper.moveCameraToPosition(new Vector2(bounds.centerX(), bounds.centerY()), onComplete, Math.max(zoomX, zoomY));
        }).subscribe();
        showLocationButtons(subscriber, allConnectionPositions);
        if (optional) {
            showCancelTravel(subscriber);
        }
    }

    private void showCancelTravel(SingleSubscriber<? super LocationInfo.Connection> subscriber) {
        stopButton = ButtonBuilder.buildButton("Stop");
        addClickListener(stopButton, () -> {
            hideTableAndButtons();
            subscriber.onSuccess(null);
        });
        stopButton.getStyle().fontColor = new Color(0x800000ff);
        InfoStage.setBottomHeight(InfoStage.getBottomHeight() + stopButton.getHeight() + 5);
        stopTravelingTable = LabelTable.createAndShowTable(0, "Stop traveling?");
        InfoStage.showButton(stopButton);
    }

    private void hideTableAndButtons() {
        InfoStage.setBottomHeight(5);
        MapStage.getLocationHighlightLayer().clearChildren();
        if (stopButton != null) {
            InfoStage.hideActor(stopButton);
        }
        if (stopTravelingTable != null) {
            InfoStage.hideActor(stopTravelingTable);
        }
    }

    private LocationInfo.Connection createConnectionFromCurrentLocation(LocationId currentLocationId) {
        return new LocationInfo.Connection() {

            @Override
            public LocationId getLocationId() {
                return currentLocationId;
            }

            @Override
            public PathType getPathType() {
                return null;
            }
        };
    }

    private void showLocationButtons(SingleSubscriber<? super LocationInfo.Connection> subscriber, List<ConnectionPosition> connectionPositions) {
        for (ConnectionPosition connectionPosition : connectionPositions) {
            Image locationButton = new LocationButton(connectionPosition, subscriber);
            addFadeInOutActions(locationButton);
            MapStage.getLocationHighlightLayer().addActor(locationButton);
        }
    }


    private void addFadeInOutActions(Actor actor) {
        AlphaAction fadeInAction = new AlphaAction();
        fadeInAction.setAlpha(0.9f);
        fadeInAction.setDuration(FADING_EFFECT_DURATION);
        fadeInAction.setActor(actor);
        fadeInAction.setInterpolation(Interpolation.sine);

        AlphaAction fadeOutAction = new AlphaAction();
        fadeOutAction.setAlpha(0.5f);
        fadeOutAction.setDuration(FADING_EFFECT_DURATION);
        fadeOutAction.setActor(actor);
        fadeOutAction.setInterpolation(Interpolation.sine);

        actor.addAction(sequence(fadeInAction, fadeOutAction, Actions.run(() -> {
            addFadeInOutActions(actor);
        })));

    }

    private Bounds createBounds(Collection<Vector2> connectionPositions) {
        Bounds bounds = new Bounds();
        for (Vector2 connectionPosition : connectionPositions) {
            if (connectionPosition.x < bounds.left) {
                bounds.left = connectionPosition.x;
            }
            if (connectionPosition.x > bounds.right) {
                bounds.right = connectionPosition.x;
            }
            if (connectionPosition.y > bounds.top) {
                bounds.top = connectionPosition.y;
            }
            if (connectionPosition.y < bounds.bottom) {
                bounds.bottom = connectionPosition.y;
            }
        }
        return bounds;
    }

    private ConnectionPosition findNearestPosition(LocationInfo.Connection connection, Vector2 currentPosition) {
        Vector2[] positions = LocationPositionResolver.resolveAll(connection.getLocationId());
        ConnectionPosition connectionPosition = new ConnectionPosition(connection);
        for (Vector2 position : positions) {
            double distance = getDistance(currentPosition, position);
            if (distance < connectionPosition.distance) {
                connectionPosition.distance = distance;
                connectionPosition.position = position;
            }
        }
        return connectionPosition;
    }

    private List<ConnectionPosition> toConnectionPosition(LocationInfo.Connection connection, Vector2 currentPosition) {
        Vector2[] positions = LocationPositionResolver.resolveAll(connection.getLocationId());

        LinkedList<ConnectionPosition> connectionPositions = new LinkedList<>();
        for (Vector2 position : positions) {
            ConnectionPosition connectionPosition = new ConnectionPosition(connection);
            connectionPosition.distance = getDistance(currentPosition, position);
            connectionPosition.position = position;
            connectionPositions.add(connectionPosition);
        }

        return connectionPositions;
    }

    public void selectLocation(List<LocationId> locations, SingleSubscriber<? super LocationInfo.Connection> onSub) {
        List<Vector2> locationPositions = new LinkedList<>();

        for (LocationId location : locations) {
            locationPositions.add(LocationPositionResolver.resolveAll(location)[1]);
        }

        Bounds bounds = createBounds(locationPositions);

        float zoom;
        if (locationPositions.size() == 1) {
            zoom = ViewProperties.ZOOMED_MIN_VIEWPORT_HEIGHT / MapStage.getCamera().viewportHeight;
        } else {
            float zoomY = (bounds.getHeight() + BORDER) / MapStage.getCamera().viewportHeight;
            float zoomX = (bounds.getWidth() + BORDER) / MapStage.getCamera().viewportWidth;
            zoom = Math.max(zoomX, zoomY);
        }


        Completable.create(onComplete -> {
            MoveCameraToLocationHelper.moveCameraToPosition(new Vector2(bounds.centerX(), bounds.centerY()), onComplete, zoom);
        }).subscribe();

        List<ConnectionPosition> connectionPositions = new LinkedList<>();
        for (int i = 0; i < locations.size(); i++) {
            ConnectionPosition connectionPosition = new ConnectionPosition(new Path(locations.get(i), PathType.WALK));
            Vector2 locationPosition = locationPositions.get(i);
            connectionPosition.position = locationPosition;
            connectionPositions.add(connectionPosition);
            ConnectionPosition left = new ConnectionPosition(new Path(locations.get(i), PathType.WALK));
            left.position = new Vector2(locationPosition.x - MAP_WIDTH, locationPosition.y);
            connectionPositions.add(left);
            ConnectionPosition right = new ConnectionPosition(new Path(locations.get(i), PathType.WALK));
            right.position = new Vector2(locationPosition.x + MAP_WIDTH, locationPosition.y);
            connectionPositions.add(right);
        }
        showLocationButtons(onSub, connectionPositions);

    }

    private class LocationButton extends Image {

        private TextureRegionDrawable upImage;
        private TextureRegionDrawable downImage;

        public LocationButton(ConnectionPosition connectionPosition, SingleSubscriber<? super LocationInfo.Connection> subscriber) {

            upImage = CustomAssetManager.getTextureRegionDrawable(LOCATION_BUTTON_UP);
            downImage = CustomAssetManager.getTextureRegionDrawable(LOCATION_BUTTON_DOWN);

            setDrawable(upImage);
            setWidth(upImage.getMinWidth() * 0.65f);
            setHeight(upImage.getMinHeight() * 0.65f);
            setColor(0.94f, 0.89f, 0.69f, 1f);
            setOrigin(getWidth() / 2, getHeight() / 2);
            setX(connectionPosition.position.x - getWidth() / 2);
            setY(connectionPosition.position.y - getHeight() / 2);

            addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    subscriber.onSuccess(connectionPosition.connection);
                    hideTableAndButtons();
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    boolean b = super.touchDown(event, x, y, pointer, button);
                    changeImage();

                    return b;
                }

                private void changeImage() {
                    if (isOver()) {
                        setDrawable(downImage);
                    } else {
                        setDrawable(upImage);
                    }
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    super.touchDragged(event, x, y, pointer);
                    changeImage();
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    setDrawable(upImage);
                }
            });
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            rotateBy(3);
        }
    }

    private class Bounds {
        private float left = Float.MAX_VALUE;
        private float right = Float.MIN_VALUE;
        private float top = Float.MIN_VALUE;
        private float bottom = Float.MAX_VALUE;

        private float getHeight() {
            return top - bottom;
        }

        private float getWidth() {
            return right - left;
        }

        private float centerX() {
            return (left + right) / 2;
        }

        private float centerY() {
            return (top + bottom) / 2;
        }

        @Override
        public String toString() {
            return "Bounds{" +
                    "left=" + left +
                    ", right=" + right +
                    ", top=" + top +
                    ", bottom=" + bottom +
                    '}';
        }
    }

    private class ConnectionPosition {
        private LocationInfo.Connection connection;
        private Vector2 position;
        private double distance;

        private ConnectionPosition(LocationInfo.Connection connection) {
            this.connection = connection;
            this.distance = Double.MAX_VALUE;
        }

    }
}
