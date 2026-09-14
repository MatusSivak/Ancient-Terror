package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class ActionInfoTable extends VisTable {

    private static final Color TITLE_COLOR = new Color(0.9f, 0.76f, 0.48f, 1f);
    private static final Color BODY_COLOR = new Color(0.93f, 0.9f, 0.82f, 1f);
    private static final Color DETAIL_COLOR = new Color(0.86f, 0.8f, 0.65f, 1f);

    public ActionInfoTable() {
        setVisible(false);
        align(Align.top);
        pad(14f);
    }

    public void init(ActionPhaseAction actionPhaseAction) {
        clear();
        Label actionNameLabel = createLabel(actionPhaseAction.getName().replace('\n', ' '), TITLE_COLOR);
        actionNameLabel.setFontScale(0.48f);
        add(actionNameLabel).growX().padBottom(12f);
        row();
        Image divider = new Image(getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(
                new Color(0.62f, 0.49f, 0.28f, 0.65f)));
        add(divider).height(1f).growX().padBottom(12f);
        row();

        Table content = new Table();
        content.top().left();
        Label descriptionLabel = createLabel(actionPhaseAction.getGeneralDescription(), BODY_COLOR);
        if (descriptionLabel != null) {
            content.add(descriptionLabel).growX().padBottom(14f);
            content.row();
        }

        Label additionalInfoLabel = createLabel(actionPhaseAction.getAdditionalInfo(), DETAIL_COLOR);
        if (additionalInfoLabel != null) {
            Table details = new Table();
            details.setBackground(getTextureRegionDrawable(PURE_WHITE_BACKGROUND)
                    .tint(new Color(0f, 0f, 0f, 0.35f)));
            details.add(additionalInfoLabel).growX().pad(10f);
            content.add(details).growX().padBottom(12f);
            content.row();
        }

        Label notRecommendedLabel = createNotRecommendedLabel(actionPhaseAction.getNotRecommendedReason());
        if (notRecommendedLabel != null) {
            content.add(notRecommendedLabel).growX().padBottom(10f);
            content.row();
        }

        Label disabledLabel = createDisabledLabel(actionPhaseAction.getDisabledReason());
        if (disabledLabel != null) {
            content.add(disabledLabel).growX().padBottom(4f);
            content.row();
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setOverscroll(false, false);
        // The button grid determines panel height; long localized content stays accessible.
        add(scrollPane).grow().minHeight(0f).prefHeight(0f);
        setBackground(getTextureRegionDrawable(GRAY_BACKGROUND)
                .tint(new Color(0.48f, 0.5f, 0.46f, 1f)));
        setVisible(true);
    }

    private Label createDisabledLabel(String disabledReason) {
        return createLabel(resolveLocalizedText(disabledReason), new Color(0.95f, 0.59f, 0.51f, 1f));
    }

    private Label createNotRecommendedLabel(String notRecommendedReason) {
        return createLabel(notRecommendedReason, new Color(0.93f, 0.73f, 0.43f, 1f));
    }

    private Label createLabel(String text, Color color) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40, 0.9f), color);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.topLeft, Align.left);
        label.setWrap(true);
        label.setFontScale(0.4f);
        return label;
    }

    private String resolveLocalizedText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        String localized = UiText.get(text);
        String missingKey = "!" + text + "!";
        if (missingKey.equals(localized)) {
            return text;
        }
        return localized;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float previousColor = batch.getPackedColor();
        float alpha = parentAlpha * getColor().a;
        batch.setColor(0.48f, 0.38f, 0.22f, 0.85f * alpha);
        // Keep the frame inside the layout bounds so both panels share the same edges.
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY(), getWidth(), 1f);
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY() + getHeight() - 1f, getWidth(), 1f);
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY() + 1f, 1f, getHeight() - 2f);
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX() + getWidth() - 1f, getY() + 1f, 1f, getHeight() - 2f);
        batch.setColor(previousColor);
    }
}
