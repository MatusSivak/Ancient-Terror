package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class StatsTable extends VisTable {

    private Label loreValue;
    private Label influenceValue;
    private Label observationValue;
    private Label strengthValue;
    private Label willValue;

    public StatsTable() {
        pad(5);

        Label loreLabel = createLabel(get("investigator.label.lore"));
        Label influenceLabel = createLabel(get("investigator.label.influence"));
        Label observationLabel = createLabel(get("investigator.label.observation"));
        Label strengthLabel = createLabel(get("investigator.label.strength"));
        Label willLabel = createLabel(get("investigator.label.will"));

        loreValue = createValue();
        influenceValue = createValue();
        observationValue = createValue();
        strengthValue = createValue();
        willValue = createValue();

        addStatPair(loreLabel, loreValue);
        addStatPair(influenceLabel, influenceValue);
        addStatPair(observationLabel, observationValue);
        addStatPair(strengthLabel, strengthValue);
        addStatPair(willLabel, willValue);
    }

    protected void addStatPair(Label label, Label value) {
        add(label).align(Align.right).padRight(5).padBottom(5);
        add(value).align(Align.left).width(65).padBottom(5);
        row();
    }

    public void init(StatsTableData data) {
        fillStatValue(loreValue, data.getLore(), data.getLoreBonus());
        fillStatValue(influenceValue, data.getInfluence(), data.getInfluenceBonus());
        fillStatValue(observationValue, data.getObservation(), data.getObservationBonus());
        fillStatValue(strengthValue, data.getStrength(), data.getStrengthBonus());
        fillStatValue(willValue, data.getWill(), data.getWillBonus());
        pack();
    }

    protected void fillStatValue(Label value, int base, int bonus) {
        if (bonus > 0) {
            value.setText("[BLACK]" + base + "[#229B3C] +" + bonus + "[]");
        } else {
            value.setText("[BLACK]" + base + "[]");
        }
    }

    private Label createLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createValue() {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFont(FONT_GOBLIN_ONE), Color.WHITE);
        style.background = new ValueFieldNinePatch();

        Label label = new Label("0", style);
        style.font.getData().markupEnabled = true;
        label.setAlignment(Align.left);
        label.setFontScale(0.25f);
        return label;
    }


}
