package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class BioTable extends VisTable {

    private final Image photo;

    private Label nameValue;
    private Label professionValue;

    public BioTable() {
        pad(5);

        Label photoLabel = createLabel("Photo: ");
        Label nameLabel = createLabel("Name: ");
        Label professionLabel = createLabel("Profession: ");

        photo = createPhoto();
        nameValue = createValue();
        professionValue = createValue();

        int photoHeight = 139;
        add(photoLabel).align(Align.right).padRight(5).padBottom(5);
        add(photo).padBottom(5).width(photoHeight * 0.9f).height(photoHeight);
        row();
        addPair(nameLabel, nameValue, 5);
        row();
        addPair(professionLabel, professionValue, 0);

    }

    private Image createPhoto() {
        Image image = new Image();
        image.setScaling(Scaling.fit);
        return image;
    }

    private void addPair(Label label, Label value, int padBottom) {
        add(label).align(Align.right).padRight(5).padBottom(padBottom);
        add(value).align(Align.left).width(180).padBottom(padBottom);
    }

    public void init(BioTableData data) {
        nameValue.setText(data.getName());
        professionValue.setText(data.getInvestigatorId().toString());
        photo.setDrawable(CustomAssetManager.getInvestigatorDrawable(data.getInvestigatorId()));

        pack();
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
        label.setAlignment(Align.left);
        label.setFontScale(0.5f);
        return label;
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        super.drawBackground(batch, parentAlpha * 0.5f, x, y);
    }
}
