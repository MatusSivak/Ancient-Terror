package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import java8.features.util.IterableUtils;
import rx.schedulers.Schedulers;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper;

import java.util.HashMap;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/**
 * @author msivak
 */
public class CompositeMap {

    public static final Color WHITE = new Color(1f, 1f, 1f, 1f);

    public void init() {
        CustomAssetManager.getTextureAsync(CustomAssetManager.MAP).subscribe(texture -> {
            SingleMap mapLeft = new SingleMap(texture);
            SingleMap mapCenter = new SingleMap(texture);
            SingleMap mapRight = new SingleMap(texture);

            MapStage.getMapLayer().addActor(mapLeft);
            MapStage.getMapLayer().addActor(mapCenter);
            MapStage.getMapLayer().addActor(mapRight);

            initCityInfoLabels();

            mapLeft.setX(-mapCenter.getWidth());
            mapRight.setX(mapCenter.getWidth());
        });
    }

    public void hideCityInfoLabels() {
        MapStage.getCityInfoLayer().setVisible(false);
    }

    public void showCityInfoLabels() {
        MapStage.getCityInfoLayer().setVisible(true);
    }

    private void initCityInfoLabels() {
        Map<LocationId, String> locationLabelMap = new HashMap<>();
        locationLabelMap.put(LocationId.SAN_FRANCISCO, "Improve\nObservation");
        locationLabelMap.put(LocationId.ARKHAM, "Gain\nIncantation\nSpells");
        locationLabelMap.put(LocationId.BUENOS_AIRES, "Gain\nRitual Spells");
        locationLabelMap.put(LocationId.LONDON, "Spawn\nClues");
        locationLabelMap.put(LocationId.ROME, "Improve\nWill");
        locationLabelMap.put(LocationId.ISTANBUL, "Improve\nInfluence");
        locationLabelMap.put(LocationId.TOKYO, "Defeat\nMonsters");
        locationLabelMap.put(LocationId.SHANGHAI, "Improve\nLore");
        locationLabelMap.put(LocationId.SYDNEY, "Improve\nStrength");
        IterableUtils.forEach(locationLabelMap.entrySet(), entry -> {
            LocationId location = entry.getKey();
            Vector2[] labelPositions = LocationPositionResolver.resolveAll(location);
            CityInfoLabelLayerResolver cityInfoLabelLayerResolver = new CityInfoLabelLayerResolver(location);
            for (Vector2 labelPosition : labelPositions) {
                Label label = createCityInfoLabel(entry.getValue());
                label.setPosition(labelPosition.x - label.getWidth() / 2, labelPosition.y - 65 - label.getHeight());
                MapStage.addToLayer(label, cityInfoLabelLayerResolver);
                MapStage.getCityInfoLayer().addActor(label);
            }
        });
    }

    private Label createCityInfoLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.LIGHT_GRAY);
        labelStyle.background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(new Color(1f,1f,1f,batch.getColor().a * 0.75f));
                super.draw(batch, x, y, width, height);
            }
        };
        Label label = new Label(text, labelStyle) {
            @Override
            public void act(float delta) {
                super.act(delta);
                float distance = (float) DistanceHelper.getDistance(new Vector2(
                        getX() + getWidth() / 2,
                        getY() + 65 / 2f + getHeight()), getStage().getCamera().position);
                changeVisibilityBasedOnDistance(distance);
            }

            private void changeVisibilityBasedOnDistance(float distance) {
                float alpha = Math.max(0, Math.min(1, (-2 / 150f) * distance + 2));
                getColor().a = alpha;
            }
        };
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        label.setTouchable(Touchable.disabled);
        label.setHeight(label.getPrefHeight() + 5);
        label.setWidth(label.getPrefWidth() + 5);
        return label;
    }

    private class CityInfoLabelLayerResolver implements MapStage.IdLayerResolver<LocationId> {

        private LocationId locationId;

        public CityInfoLabelLayerResolver(LocationId locationId) {
            this.locationId = locationId;
        }

        @Override
        public LocationId getId() {
            return locationId;
        }

        @Override
        public Group getLayer() {
            return MapStage.getCityInfoLayer();
        }
    }
}
