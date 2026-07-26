package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class SpecialTable extends VisTable {

    private Label actionValue;
    private Label abilityValue;

    public SpecialTable() {
        pad(5);

        Label actionLabel = createLabel("Action: ");
        Label abilityLabel = createLabel("Ability: ");

        actionValue = createValue();
        abilityValue = createValue();

        addPair(actionLabel, actionValue, 5);
        row();
        addPair(abilityLabel, abilityValue, 0);
    }

    private void addPair(Label label, Label value, int padBottom) {
        add(label).align(Align.right).padRight(5).padBottom(padBottom);
        add(value).align(Align.left).width(625).height(53).padBottom(padBottom);
    }

    public void init(SpecialTableData data) {
        actionValue.setText(data.getAction());
        abilityValue.setText(data.getAbility());
        pack();
//        setBackground(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND));
    }

    private Label createLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createValue() {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.BLACK);
        style.background = new ValueFieldNinePatch();

        Label label = new Label("0", style);
        label.setWrap(true);
        label.setAlignment(Align.topLeft);
        label.setFontScale(0.5f);
        return label;
    }


}
