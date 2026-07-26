package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTable;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class TestResultTable extends VisTable {

    public static TestResultTable createPassedTable() {
        return createTable("Passed", new Color(0x00ff00ff));
    }

    public static TestResultTable createFailedTable() {
        return createTable("Failed", new Color(0xff4040ff));
    }

    public static TestResultTable createScoreTable(int score) {
        return createTable("Score: " + score, Color.YELLOW);
    }

    private static TestResultTable createTable(String resultText, Color color) {
        TestResultTable table = new TestResultTable();

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = getBitmapFont(FONT_MINYA);
        headerLabelStyle.fontColor = Color.WHITE;

        Label headerLabel = new Label("Test Outcome", headerLabelStyle);
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
