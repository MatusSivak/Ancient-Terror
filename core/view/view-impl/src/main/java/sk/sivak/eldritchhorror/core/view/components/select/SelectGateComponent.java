package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.map.gate.NewGateAnimatedImage;

import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class SelectGateComponent extends VisTable implements SelectComponent<GateInfo> {

    public static final float PADDING = 20;
    public static final float SCALE_SPEED = 0.0125f; // 0.0125 * 40 = 0.5s

    private final GateInfo gateInfo;
    private boolean scaling;
    private FloatAction selectDeselectAction;
    private boolean selected;
    private static final int MAX_GATE_SIZE = 160;
    private static final int MIN_GATE_SIZE = 120;
    private float currentGateSize = MAX_GATE_SIZE;
    private final Cell<NewGateAnimatedImage> imageCell;
    private final Label nameLabel;
    private boolean ticked = false;

    public SelectGateComponent(GateInfo gateInfo) {
        align(Align.top);
        NewGateAnimatedImage gateImage = new NewGateAnimatedImage(gateInfo.getGateColor(), null);
        gateImage.setTouchable(Touchable.enabled);
        this.gateInfo = gateInfo;
        scaling = false;
        selected = false;
        imageCell = add(gateImage).align(Align.center).pad(5 + PADDING).width(currentGateSize).height(currentGateSize);
        row();
        nameLabel = createLabel(gateInfo.getLocationId().toString(), Color.WHITE);
        add(nameLabel).padBottom(5).align(Align.bottom);

        pack();
        setBackground(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND));
        deselectFast();
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public GateInfo getKey() {
        return gateInfo;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public void select() {
        if (scaling) {
            selectDeselectAction.finish();
            clearActions();
        }
        selected = true;
        scaling = true;
        imageCell.getActor().updateGateTransparency(OmenColor.valueOf(gateInfo.getGateColor().name()));
        selectDeselectAction = new FloatAction(currentGateSize, MAX_GATE_SIZE) {
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
        float duration = (MAX_GATE_SIZE - currentGateSize) * SCALE_SPEED;
        selectDeselectAction.setDuration(duration);
        selectDeselectAction.setInterpolation(Interpolation.swingOut);
        addAction(selectDeselectAction);
    }

    private void deselectFast() {
        selected = false;
        scaling = false;
        updateFloatAction(MIN_GATE_SIZE);
    }

    private void updateFloatAction(float value) {
        currentGateSize = value;
        imageCell.width(currentGateSize).pad(5 + (160 - currentGateSize)/2f).height(currentGateSize);
        validate();
        invalidate();
        invalidateHierarchy();
        float colorValue = (value-MIN_GATE_SIZE)/80f + 0.5f;
        Color color = new Color(colorValue, colorValue, colorValue, 1f);
        imageCell.getActor().setColor(color);
        nameLabel.setColor(color);
    }

    @Override
    public void deselect() {
        if (scaling) {
            clearActions();
            selectDeselectAction.finish();
        }
        imageCell.getActor().updateGateTransparency(null);
        selected = false;
        scaling = true;
        selectDeselectAction = new FloatAction(currentGateSize, MIN_GATE_SIZE) {
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
        float duration = (currentGateSize - MIN_GATE_SIZE) * SCALE_SPEED;
        selectDeselectAction.setDuration(duration);
        addAction(selectDeselectAction);
    }

    @Override
    public void showTicked() {
        ticked = true;
    }

    @Override
    public int getInTableWidth(int listSize) {
        return MAX_GATE_SIZE + 10;
    }

    @Override
    public int getInTableHeight(int listSize) {
        return MAX_GATE_SIZE + 33;
    }

    @Override
    public void reselect() {

    }

    @Override
    public void setOtherSelectComponents(List<SelectComponent<GateInfo>> otherSelectComponents) {

    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.5f);
        label.setAlignment(Align.bottom, Align.center);
        return label;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        imageCell.getActor().setOrigin(currentGateSize/2f,currentGateSize/2f);
    }
}
