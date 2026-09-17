package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/** Fits the complete sheet, including wrapped effects, inside the passport page. */
final class FittedCharacterSheet extends WidgetGroup {
    private final Table sheet;

    FittedCharacterSheet(Table sheet) {
        this.sheet = sheet;
        sheet.setTransform(true);
        addActor(sheet);
    }

    @Override
    public void layout() {
        sheet.setSize(getWidth(), getHeight());
        // Wrapping depends on assigned widths; settle those before measuring height.
        for (int i = 0; i < 4; i++) {
            sheet.validate();
            sheet.setHeight(sheet.getPrefHeight());
        }
        sheet.validate();
        float scale = Math.min(1, getHeight() / Math.max(1, sheet.getHeight()));
        sheet.setOrigin(0, 0);
        sheet.setScale(scale);
        sheet.setPosition((getWidth() - sheet.getWidth() * scale) / 2,
                (getHeight() - sheet.getHeight() * scale) / 2);
    }
}
