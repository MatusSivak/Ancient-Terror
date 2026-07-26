package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class BackgroundBioTable extends VisTable {

    private Label bioBackgroundValue;

    public BackgroundBioTable() {
        pad(5);

        bioBackgroundValue = createValue();
        add(bioBackgroundValue).align(Align.left).width(695).height(250);
    }

    public void init(String bioBackground) {
        bioBackgroundValue.setText(bioBackground);
        pack();
    }

    private Label createValue() {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.BLACK);
        style.background = new ValueFieldNinePatch();

        Label label = new Label("0", style);
        label.setWrap(true);
        label.setAlignment(Align.center, Align.center);
        label.setFontScale(0.5f);
        return label;
    }
}
