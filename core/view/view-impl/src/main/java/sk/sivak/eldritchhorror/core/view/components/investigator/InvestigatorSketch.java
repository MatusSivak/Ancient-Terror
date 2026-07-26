package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Color;
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
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.select.SelectComponent;

import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class InvestigatorSketch extends VisTable implements SelectComponent<InvestigatorId> {

    public static final float PADDING = 5;
    public static final float DESELECT_SCALE = 0.75f;
    public static final int SCALE_SPEED = 2;
    private final Cell<Image> imageCell;
    private final Label nameLabel;
    private InvestigatorId investigatorId;
    private FloatAction selectDeselectAction;
    private float currentScale;
    private boolean scaling;
    private boolean selected;
    private boolean ticked = false;

    public InvestigatorSketch(InvestigatorId investigatorId) {
        selected = false;
        scaling = false;
        currentScale = 1f;
        this.investigatorId = investigatorId;
        Image image = new Image(CustomAssetManager.getInvestigatorTexture(investigatorId)) {
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
        nameLabel = createLabel(investigatorId.toString(), Color.WHITE);

        imageCell = add(image).grow().pad(PADDING).align(Align.center);
        row();
        addSeparator();
        add(nameLabel).pad(PADDING);

        pack();

        setBackground(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND));

        deselectFast();
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

    public void deselectFast() {
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
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public InvestigatorId getKey() {
        return getInvestigatorId();
    }

    public boolean isSelected() {
        return selected;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void showTicked() {
        ticked = true;
    }

    @Override
    public int getInTableWidth(int listSize) {
        return 175;
    }

    @Override
    public int getInTableHeight(int listSize) {
        return 220;
    }

    @Override
    public void reselect() {

    }

    @Override
    public void setOtherSelectComponents(List<SelectComponent<InvestigatorId>> otherSelectComponents) {

    }
}
