package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class RollResultTable extends VisTable {

    public static RollResultTable createSuccessfulTable() {
        return createTable(get("roll.successful"), Color.valueOf("8FD694"));
    }

    public static RollResultTable createNotSuccessfulTable() {
        return createTable(get("roll.notSuccessful"), Color.valueOf("E9C46A"));
    }

    public static RollResultTable createFailedTable() {
        return createTable(get("roll.failed"), Color.valueOf("E57373"));
    }

    private static RollResultTable createTable(String resultText, Color color) {
        RollResultTable table = new RollResultTable();

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40);
        headerLabelStyle.fontColor = Color.valueOf("B5BDB4");

        Label headerLabel = new Label(get("roll.outcome"), headerLabelStyle);
        headerLabel.setFontScale(0.36f);

        Label.LabelStyle resultLabelStyle = new Label.LabelStyle();
        resultLabelStyle.font = getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40);
        resultLabelStyle.fontColor = color;

        Label resultLabel = new Label(resultText, resultLabelStyle);
        resultLabel.setFontScale(0.55f);

        table.setBackground(SelectionPanelStyle.panel("121B1DEE", "87734E", 8, 22));
        table.add(headerLabel).row();
        table.add(resultLabel).padTop(2);
        table.pack();
        return table;
    }
}
