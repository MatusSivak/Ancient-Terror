package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class ActionButtonWithText extends Table {

    public static final float CAPTION_HEIGHT = 40f;
    public static final float CAPTION_GAP = 3f;

    private final Cell<ActionButton> actionButtonCell;
    private final Label label;
    private final Cell<Label> textCell;
    private ActionButton actionButton;

    public ActionButtonWithText(ActionButtonData actionButtonData) {
        align(Align.top);
        actionButton = ActionButton.build(actionButtonData);
        actionButtonCell = add(actionButton);
        row();
        label = createLabel(actionButtonData.getActionName(), actionButtonData.isEnabled()
                ? new Color(1f, 0.95f, 0.83f, 1f) : new Color(0.74f, 0.76f, 0.72f, 1f));
        textCell = add(label).height(CAPTION_HEIGHT).padTop(CAPTION_GAP);
    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40, 0.78f), color);
        labelStyle.background = getTextureRegionDrawable(PURE_WHITE_BACKGROUND)
                .tint(new Color(0f, 0f, 0f, 0.82f));
        labelStyle.background.setLeftWidth(4f);
        labelStyle.background.setRightWidth(4f);
        labelStyle.background.setTopHeight(5f);
        labelStyle.background.setBottomHeight(5f);
        // Legacy action names contain manual line breaks; let the available width decide wrapping.
        Label label = new Label(text.replaceAll("\\s+", " ").trim(), labelStyle);
        label.setFontScale(0.32f);
        label.setWrap(true);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        actionButtonCell.width(getWidth());
        actionButtonCell.height(getWidth());
        textCell.width(getWidth());
    }

    public ActionButton getActionButton() {
        return actionButton;
    }
}
