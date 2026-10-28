package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper;
import sk.sivak.eldritchhorror.core.view.utils.MarkupText;

import java.util.HashMap;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

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
        locationLabelMap.put(LocationId.SAN_FRANCISCO, get("map.cityInfo.sanFrancisco"));
        locationLabelMap.put(LocationId.ARKHAM, get("map.cityInfo.arkham"));
        locationLabelMap.put(LocationId.BUENOS_AIRES, get("map.cityInfo.buenosAires"));
        locationLabelMap.put(LocationId.LONDON, get("map.cityInfo.london"));
        locationLabelMap.put(LocationId.ROME, get("map.cityInfo.rome"));
        locationLabelMap.put(LocationId.ISTANBUL, get("map.cityInfo.istanbul"));
        locationLabelMap.put(LocationId.TOKYO, get("map.cityInfo.tokyo"));
        locationLabelMap.put(LocationId.SHANGHAI, get("map.cityInfo.shanghai"));
        locationLabelMap.put(LocationId.SYDNEY, get("map.cityInfo.sydney"));
        locationLabelMap.put(LocationId.THE_AMAZON, "");
        locationLabelMap.put(LocationId.THE_HEART_OF_AFRICA, "");
        locationLabelMap.put(LocationId.THE_PYRAMIDS, "");
        locationLabelMap.put(LocationId.ANTARCTICA, "");
        locationLabelMap.put(LocationId.THE_HIMALAYAS, "");
        locationLabelMap.put(LocationId.TUNGUSKA, "");
        IterableUtils.forEach(locationLabelMap.entrySet(), entry -> {
            LocationId location = entry.getKey();
            String text = get("map.locationName." + location.name());
            Vector2[] labelPositions = LocationPositionResolver.resolveAll(location);
            CityInfoLabelLayerResolver cityInfoLabelLayerResolver = new CityInfoLabelLayerResolver(location);
            for (Vector2 labelPosition : labelPositions) {
                Table label = createCityInfoLabel(text, entry.getValue(), labelPosition);
                label.setPosition(labelPosition.x - label.getWidth() / 2, labelPosition.y - 34 - label.getHeight());
                MapStage.addToLayer(label, cityInfoLabelLayerResolver);
            }
        });
    }

    private Table createCityInfoLabel(String text, String effect, Vector2 location) {
        Table label = new Table() {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (getStage() == null) return;
                // Keep camera proximity independent of the label's height and typography.
                float distance = (float) DistanceHelper.getDistance(
                        new Vector2(location.x, location.y - 32.5f), getStage().getCamera().position);
                getColor().a = Math.max(0, Math.min(1, (-2 / 150f) * distance + 2));
            }
        };
        TextureRegionDrawable background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                float previous = batch.getPackedColor();
                batch.setColor(1f, 1f, 1f, batch.getColor().a * 0.75f);
                super.draw(batch, x, y, width, height);
                batch.setColor(previous);
            }
        };
        background.setMinWidth(0);
        background.setMinHeight(0);
        label.setBackground(background);
        Label title = new Label(text, new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 42), Color.valueOf("EAD6A6")));
        title.setAlignment(Align.center);
        title.setFontScale(.40f);
        label.pad(5, 9, 5, 9);
        label.add(title);
        if (!effect.isEmpty()) {
            Label description = new Label(MarkupText.replaceColoredGlyphKeywords(effect.replace('\n', ' ')),
                    new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 42), Color.LIGHT_GRAY));
            description.getStyle().font.getData().markupEnabled = true;
            description.setFontScale(.34f);
            description.setAlignment(Align.center);
            label.row();
            label.add(description).padTop(3);
        }
        label.setTouchable(Touchable.disabled);
        label.pack();
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
