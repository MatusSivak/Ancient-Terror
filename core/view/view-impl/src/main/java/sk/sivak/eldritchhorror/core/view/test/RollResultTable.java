package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTable;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class RollResultTable extends VisTable {

    public static RollResultTable createSuccessfulTable() {
        return createTable(get("roll.successful"), new Color(0x00ff00ff));
    }

    public static RollResultTable createNotSuccessfulTable() {
        return createTable(get("roll.notSuccessful"), Color.YELLOW);
    }

    public static RollResultTable createFailedTable() {
        return createTable(get("roll.failed"), new Color(0xff0000ff));
    }

    private static RollResultTable createTable(String resultText, Color color) {
        RollResultTable table = new RollResultTable();

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = getBitmapFont(FONT_MINYA);
        headerLabelStyle.fontColor = Color.WHITE;

        Label headerLabel = new Label(get("roll.outcome"), headerLabelStyle);
        headerLabel.setFontScale(0.5f);

        Label.LabelStyle resultLabelStyle = new Label.LabelStyle();
        resultLabelStyle.font = getBitmapFont(FONT_MINYA);
        resultLabelStyle.fontColor = color;

        Label resultLabel = new Label(resultText, resultLabelStyle);
        resultLabel.setFontScale(0.5f);

        table.add(headerLabel).pad(5).row();
        table.add(resultLabel).pad(5).padTop(0);
        table.pack();
        table.setBackground(getTextureRegionDrawable(GRAY_BACKGROUND));
        return table;
    }
}
