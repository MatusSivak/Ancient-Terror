package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTable;
import static sk.sivak.eldritchhorror.core.view.components.sheet.investigator.CharacterSheetWidgets.*;

final class CharacterSkillsTable extends VisTable {
    private final Label loreValue = createValue();
    private final Label influenceValue = createValue();
    private final Label observationValue = createValue();
    private final Label strengthValue = createValue();
    private final Label willValue = createValue();

    public CharacterSkillsTable() {
        defaults().uniformX().growX().minWidth(0);
        for (String asset : new String[]{"glyphs/lore.png", "glyphs/influence.png", "glyphs/observation.png",
                "glyphs/strength.png", "glyphs/will.png"}) {
            add(icon(asset)).size(SKILL_GLYPH_SIZE).padBottom(6);
        }
        row();
        for (Label value : new Label[]{loreValue, influenceValue, observationValue, strengthValue, willValue}) {
            add(value);
        }
    }
    public void init(StatsTableData data) {
        fillStatValue(loreValue, data.getLore(), data.getLoreBonus());
        fillStatValue(influenceValue, data.getInfluence(), data.getInfluenceBonus());
        fillStatValue(observationValue, data.getObservation(), data.getObservationBonus());
        fillStatValue(strengthValue, data.getStrength(), data.getStrengthBonus());
        fillStatValue(willValue, data.getWill(), data.getWillBonus());
    }

    protected void fillStatValue(Label value, int base, int bonus) {
        value.setText(bonus > 0 ? base + " [#83C989]+" + bonus + "[]" : Integer.toString(base));
    }

    private Label createValue() {
        Label value = text("0", 18, INK);
        value.getStyle().font.getData().markupEnabled = true;
        return value;
    }
}
