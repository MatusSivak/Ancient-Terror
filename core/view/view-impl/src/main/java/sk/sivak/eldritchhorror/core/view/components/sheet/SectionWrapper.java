package sk.sivak.eldritchhorror.core.view.components.sheet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class SectionWrapper extends Table {

    public static final int MINUS_WIDTH = 50;
    public static final int MINUS_HEIGHT = 50;
    private Label titleLabel;

    public SectionWrapper init(String title, Table section) {
        titleLabel = createLabel(title);
        add(titleLabel).pad(35, 55, 0, 40).width(150);
        row();
        add(section).width(section.getWidth()).pad(0, 42, 37, 27).height(section.getHeight());
        pack();

        NinePatch ninePatch = new NinePatch(CustomAssetManager.getTexture(BLACK_SQUARE), 35, 75, 35, 75) {
            @Override
            public float getMiddleHeight() {
                return 0f;
            }

            @Override
            public float getMiddleWidth() {
                return 0f;
            }
        };
        NinePatchDrawable background = new NinePatchDrawable(ninePatch);
        setBackground(background);
        return this;
    }

    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha * 0.75f);
        getBackground().draw(batch,
                x - MINUS_WIDTH, y - MINUS_HEIGHT,
                getWidth() + MINUS_WIDTH * 2, getHeight() + MINUS_HEIGHT * 2);
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    @Override
    public float getWidth() {
        float width = super.getWidth();
        return width - MINUS_WIDTH;
    }

    @Override
    public float getHeight() {
        float height = super.getHeight();
        return height - MINUS_HEIGHT;
    }

    private Label createLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.LIGHT_GRAY);
        labelStyle.background = CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
