package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class TokensTable extends VisTable {

    private static final String SLASH_SPACER = "/";
    private static final Color COLOR_HEALTH = new Color(0xe02323ff);
    private static final Color COLOR_SANITY = new Color(0x4286f4ff);

    private Label healthValue;
    private Label sanityValue;
    private Label focusValue;
    private Label clueValue;
    private Label shipValue;
    private Label trainValue;

    public TokensTable() {
        pad(5);

        Label healthLabel = createLabel(get("investigator.label.health"));
        healthLabel.getStyle().fontColor = COLOR_HEALTH;
        Label sanityLabel = createLabel(get("investigator.label.sanity"));
        sanityLabel.getStyle().fontColor = COLOR_SANITY;
        Label clueLabel = createLabel(get("investigator.label.clues"));
        Label focusLabel = createLabel(get("investigator.label.focus"));
        Label shipLabel = createLabel(get("investigator.label.shipTickets"));
        Label trainLabel = createLabel(get("investigator.label.trainTickets"));

        healthValue = createValue();
        healthValue.getStyle().fontColor = COLOR_HEALTH;
        sanityValue = createValue();
        sanityValue.getStyle().fontColor = COLOR_SANITY;
        clueValue = createValue();
        focusValue = createValue();
        shipValue = createValue();
        trainValue = createValue();

        addRow(healthLabel, healthValue);
        addRow(sanityLabel, sanityValue);
        addRow(clueLabel, clueValue);
        addRow(focusLabel, focusValue);
        addRow(shipLabel, shipValue);
        addRow(trainLabel, trainValue);

    }

    private void addRow(Label label, Label value) {
        add(label).align(Align.right).padRight(5).padBottom(5);
        add(value).align(Align.left).width(65).padBottom(5);
        row();
    }

    public void init(TokensTableData data) {
        healthValue.setText(data.getCurrentHealth() + SLASH_SPACER + data.getMaxHealth());
        sanityValue.setText(data.getCurrentSanity() + SLASH_SPACER + data.getMaxSanity());
        focusValue.setText("" + data.getFocus());
        clueValue.setText("" + data.getClue());
        shipValue.setText("" + data.getShip());
        trainValue.setText("" + data.getTrain());
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
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_GOBLIN_ONE), Color.BLACK);
        labelStyle.background = new ValueFieldNinePatch();
        Label label = new Label("0", labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.25f);
        return label;
    }


}
