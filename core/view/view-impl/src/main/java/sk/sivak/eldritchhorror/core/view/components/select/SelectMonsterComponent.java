package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;

import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.TICK;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getEpicMonsterTexture;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getNonEpicMonsterTexture;

public class SelectMonsterComponent extends VisTable implements SelectComponent<MonsterInfo> {

    public static final float PADDING = 5;
    public static final float DESELECT_SCALE = 0.75f;
    public static final int SCALE_SPEED = 2;
    private final Cell<Image> imageCell;
    private final Label nameLabel;
    private final ToughnessBar toughnessBar;
    private Label locationLabel;
    private MonsterInfo monsterInfo;
    private FloatAction selectDeselectAction;
    private float currentScale;
    private boolean scaling;
    private boolean selected;
    private boolean ticked = false;

    public SelectMonsterComponent(MonsterInfo monsterInfo) {
        align(Align.top);
        selected = false;
        scaling = false;
        currentScale = 1f;
        this.monsterInfo = monsterInfo;
        Texture monsterTexture;
        if (monsterInfo.isEpic()) {
            monsterTexture = getEpicMonsterTexture(monsterInfo.getClass().getSimpleName());
        } else {
            monsterTexture = getNonEpicMonsterTexture(monsterInfo.getClass().getSimpleName());
        }
        Image image = new Image(monsterTexture) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);

                if (!ticked) {
                    return;
                }
                batch.setColor(new Color(1f, 1f, 1f, 0.5f * parentAlpha));
                batch.draw(CustomAssetManager.getTexture(TICK),
                        getX() + 30,
                        getY() + 30,
                        getWidth() - 60,
                        getWidth() - 60);
            }
        };
        image.setScale(currentScale);
        image.setScaling(Scaling.fit);

        imageCell = add(image).growX().pad(PADDING).align(Align.top).height(155);
        row();
        addSeparator();

        nameLabel = createLabel(this.monsterInfo.getNameInSelectComponent(), Color.WHITE);
        add(nameLabel).pad(PADDING).align(Align.top);
        row();


        if (isOnCityOrExpedition(monsterInfo.getCurrentLocation())) {
            locationLabel = createLocationLabel(this.monsterInfo.getCurrentLocation());
            add(locationLabel).pad(PADDING).padTop(0).align(Align.top);
            row();
        }

        toughnessBar = new ToughnessBar();
        toughnessBar.init(
                monsterInfo.getToughness() == null ? 0 : monsterInfo.getToughness(),
                monsterInfo.getCurrentHealth() == null ? 0 : monsterInfo.getCurrentHealth(), 0.33f);
        add(toughnessBar).height(53 * 0.33f).pad(PADDING).padTop(0).align(Align.top);
        row();
        pack();

        setBackground(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND));

        deselectFast();
    }

    private boolean isOnCityOrExpedition(LocationId locationId) {
        return locationId != null && !locationId.toString().startsWith("Space");
    }

    private Label createLocationLabel(LocationId currentLocation) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.WHITE);
        Label label = new Label("(" + currentLocation.toString() + ")", labelStyle);
        label.setFontScale(0.31f);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.5f);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        imageCell.getActor().setOrigin(getWidth() / 2 - PADDING, 0);
    }

    public void select() {
        if (scaling) {
            selectDeselectAction.finish();
            clearActions();
        }
        selected = true;
        scaling = true;
        selectDeselectAction = new FloatAction(currentScale, 1f) {
            @Override
            protected void update(float percent) {
                super.update(percent);
                updateFloatAction(getValue());
            }

            @Override
            protected void end() {
                scaling = false;
            }
        };
        float duration = (1f - currentScale) * SCALE_SPEED;
        selectDeselectAction.setDuration(duration);
        selectDeselectAction.setInterpolation(Interpolation.swingOut);
        addAction(selectDeselectAction);
    }

    private void deselectFast() {
        selected = false;
        scaling = false;
        updateFloatAction(DESELECT_SCALE);
    }

    public void deselect() {
        if (scaling) {
            clearActions();
            selectDeselectAction.finish();
        }
        selected = false;
        scaling = true;
        selectDeselectAction = new FloatAction(currentScale, DESELECT_SCALE) {
            @Override
            protected void update(float percent) {
                super.update(percent);
                updateFloatAction(getValue());
            }

            @Override
            protected void end() {
                scaling = false;
            }
        };
        float duration = (currentScale - DESELECT_SCALE) * SCALE_SPEED;
        selectDeselectAction.setDuration(duration);
        addAction(selectDeselectAction);
    }

    private void updateFloatAction(float value) {
        currentScale = value;
        imageCell.getActor().setScale(value);
        float colorValue = 1 - 2 * (1 - value);
        Color color = new Color(colorValue, colorValue, colorValue, 1f);
        imageCell.getActor().setColor(color);
        nameLabel.setColor(color);
        if (locationLabel != null) {
            locationLabel.setColor(color);
        }
        toughnessBar.setColor(color);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public MonsterInfo getKey() {
        return getMonsterInfo();
    }

    public boolean isSelected() {
        return selected;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public void showTicked() {
        ticked = true;
    }

    @Override
    public int getInTableWidth(int listSize) {
        return 165;
    }

    @Override
    public int getInTableHeight(int listSize) {
        return 265;
    }

    @Override
    public void reselect() {

    }

    @Override
    public void setOtherSelectComponents(List<SelectComponent<MonsterInfo>> otherSelectComponents) {

    }
}
