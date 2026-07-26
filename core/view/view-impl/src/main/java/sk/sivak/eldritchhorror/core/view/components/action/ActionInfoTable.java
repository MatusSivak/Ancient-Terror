package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class ActionInfoTable extends VisTable {

    private Label actionNameLabel;
    private Label descriptionLabel;
    private Label additionalInfoLabel;
    private Label disabledLabel;
    private Label notRecommendedLabel;

    public ActionInfoTable() {
        align(Align.top);
    }

    public void init(ActionPhaseAction actionPhaseAction) {
        clear();
        actionNameLabel = createLabel(actionPhaseAction.getName().replace('\n', ' '), Color.GREEN);
        descriptionLabel = createLabel(actionPhaseAction.getGeneralDescription(), Color.WHITE);
        additionalInfoLabel = createLabel(actionPhaseAction.getAdditionalInfo(), Color.YELLOW);
        disabledLabel = createDisabledLabel(actionPhaseAction.getDisabledReason());
        notRecommendedLabel = createNotRecommendedLabel(actionPhaseAction.getNotRecommendedReason());

        add(actionNameLabel).pad(10);
        row();
        addSeparator();

        add(descriptionLabel).pad(5).growX();
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center, Align.center);
        row();

        if (additionalInfoLabel != null) {
            additionalInfoLabel.setWrap(true);
            additionalInfoLabel.setAlignment(Align.center, Align.center);
            add(additionalInfoLabel).pad(5).growX();
            row();
        }

        if (notRecommendedLabel != null) {
            notRecommendedLabel.setWrap(true);
            notRecommendedLabel.setAlignment(Align.center, Align.center);
            add(notRecommendedLabel).pad(5).growX();
            row();
        }

        if (disabledLabel != null) {
            disabledLabel.setWrap(true);
            disabledLabel.setAlignment(Align.bottom, Align.center);
            add(disabledLabel).pad(5).growX().growY().align(Align.bottom);
            row();
        }

        setBackground(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.GRAY_BACKGROUND));
    }

    private Label createDisabledLabel(String disabledReason) {
        return createLabel(disabledReason, new Color(1f, 0.25f, 0.25f, 1f));
    }

    private Label createNotRecommendedLabel(String notRecommendedReason) {
        return createLabel(notRecommendedReason, Color.ORANGE);
    }

    private Label createLabel(String text, Color color) {
        if (text == null) {
            return null;
        }
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setFontScale(0.5f);
        return label;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f * parentAlpha));
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX() - 5, getY() - 5, getWidth() + 10, getHeight() + 10);
        super.draw(batch, parentAlpha);
    }
}
