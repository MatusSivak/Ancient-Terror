package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import java8.features.function.Consumer;
import java8.features.function.Function;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class SelectComponentsComplexTable<Key> extends Table {

    private final SelectComponentsTable<Key> selectComponentsTable;
    private final Label titleLabel;
    private Key selectedKey;

    SelectComponentsComplexTable() {
        selectComponentsTable = new SelectComponentsTable<Key>();
        titleLabel = createLabel("XXX TITLE", Color.GREEN);
        add(titleLabel).pad(5).padBottom(0);
        row();
        ScrollPane scrollPane = new ScrollPane(selectComponentsTable);
        add(scrollPane).maxWidth(ViewProperties.VIEWPORT_WIDTH - 120).pad(5);
        row();
        pack();

        selectComponentsTable.addObserver(new SelectObserver());
    }

    public void setBackgroundColorFn(Function<Float, Color> backgroundColorFn) {
        this.selectComponentsTable.setBackgroundColorFn(backgroundColorFn);
    }

    public void setBackgroundTexture(String backgroundTexture) {
        this.selectComponentsTable.setBackgroundTexture(backgroundTexture);
    }

    public void disable(Key key) {
        selectComponentsTable.disable(key);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    private class SelectObserver implements Consumer<Key> {

        @Override
        public void accept(Key key) {
            selectedKey = key;
            if (key == null) {
                hideOkButton();
            } else {
                showOkButton();
            }
        }
    }

    protected void hideOkButton() {
    }

    protected void showOkButton() {
    }


    public Key getSelectedKey() {
        return selectedKey;
    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.5f);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    public void init(List<Key> availableKeys) {
        selectComponentsTable.init(availableKeys);
        pack();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f * getColor().a * parentAlpha));
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY(), getPrefWidth(), getPrefHeight());

        Vector2 titleLabelVector = titleLabel.localToStageCoordinates(new Vector2());
        batch.setColor(new Color(0.0f, 0.0f, 0.0f,  0.5f* getColor().a * parentAlpha));
        batch.draw(getTexture(PURE_WHITE_BACKGROUND),
                getX() + 5,
                titleLabelVector.y,
                getWidth() - 10,
                titleLabel.getHeight());

        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }
}
