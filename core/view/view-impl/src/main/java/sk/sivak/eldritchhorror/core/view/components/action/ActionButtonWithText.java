package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class ActionButtonWithText extends Table {

    private final Cell<ActionButton> actionButtonCell;
    private final Label label;
    private final Cell<Label> textCell;
    private ActionButton actionButton;

    public ActionButtonWithText(ActionButtonData actionButtonData) {
        align(Align.top);
        actionButton = ActionButton.build(actionButtonData);
        actionButtonCell = add(actionButton);
        row();
        label = createLabel(actionButtonData.getActionName(), Color.WHITE);
        textCell = add(label);
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
        actionButtonCell.width(getWidth());
        actionButtonCell.height(getWidth());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        try {
            batch.setColor(new Color(1f, 1f, 1f, parentAlpha));
            float actorsHeight = actionButtonCell.getActorHeight() + 40;
            float offsetY = getHeight() - actorsHeight;
            batch.draw(getTexture(GRAY_BACKGROUND), getX(), getY() + offsetY - 5, getPrefWidth(), actorsHeight + 5);
            super.draw(batch, parentAlpha);
        } catch (ArrayIndexOutOfBoundsException ex) {
            // lets ignore this one
        }
    }

    public ActionButton getActionButton() {
        return actionButton;
    }
}
