package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class SpecialTable extends VisTable {

    private Label actionValue;
    private Label abilityValue;

    public SpecialTable() {
        pad(0);

        Label actionLabel = createLabel(get("investigator.label.action"));
        Label abilityLabel = createLabel(get("investigator.label.ability"));

        actionValue = createValue();
        abilityValue = createValue();

        addPair(actionLabel, actionValue, 5);
        row();
        addPair(abilityLabel, abilityValue, 0);
    }

    private void addPair(Label label, Label value, int padBottom) {
        add(label).align(Align.topRight).padTop(6).padRight(12).padBottom(padBottom);
        add(CharacterSheetWidgets.effectText(value)).growX().minWidth(0).padBottom(padBottom);
    }

    public void init(SpecialTableData data) {
        actionValue.setText(resolveLocalizedText(data.getAction()));
        abilityValue.setText(resolveLocalizedText(data.getAbility()));

    }

    private String resolveLocalizedText(String textOrKey) {
        if (textOrKey == null || textOrKey.isEmpty()) {
            return "";
        }
        if (!textOrKey.startsWith("investigator.")) {
            return textOrKey;
        }
        String localized = get(textOrKey);
        String missingKey = "!" + textOrKey + "!";
        if (missingKey.equals(localized)) {
            return textOrKey;
        }
        return localized;
    }

    private Label createLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setFontScale(0.45f);
        return label;
    }

    private Label createValue() {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), CharacterSheetWidgets.INK);

        Label label = new Label("0", style);
        label.setWrap(true);
        label.setAlignment(Align.topLeft);
        label.setFontScale(0.45f);
        return label;
    }

}
