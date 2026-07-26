package sk.sivak.eldritchhorror.core.view.components.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import java8.features.function.Consumer;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class LabelTable extends VisTable {

    private LabelTable() {
    }

    public static LabelTable createAndShowTable(float delay, String labelText, Texture texture) {

        Consumer<Table> consumer = table -> {
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = getBitmapFont(FONT_MINYA);
            labelStyle.fontColor = Color.YELLOW;

            Label resultLabel = new Label(labelText, labelStyle);
            resultLabel.setAlignment(Align.center, Align.center);
            resultLabel.setFontScale(0.5f);

            Image image = new Image(texture);
            image.setScaling(Scaling.fit);
            table.add(image)
                    .height(resultLabel.getPrefHeight() + 4)
                    .width(resultLabel.getPrefHeight() + 4)
                    .padTop(5).padLeft(5).padBottom(5);
            table.add(resultLabel).pad(5);
        };
        return createAndShowTable(delay, consumer, 0);
    }

    public static LabelTable createAndShowTable(float delay, String labelText, String portraitTextureName, List<Texture> textures, int minWidth) {

        Consumer<Table> consumer = table -> {
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = getBitmapFont(FONT_MINYA);
            labelStyle.fontColor = Color.YELLOW;

            Label resultLabel = new Label(labelText, labelStyle);
            resultLabel.setAlignment(Align.center, Align.center);
            resultLabel.setFontScale(0.5f);

            Image image = new Image(CustomAssetManager.getTexture(portraitTextureName));
            image.setScaling(Scaling.fit);
            table.add(image).align(Align.top).height(95).width(95).padTop(5).padBottom(75);

            for (Texture texture : textures) {
                if (texture != null) {
                    Image otherImage = new Image(texture);
                    otherImage.setScaling(Scaling.fit);
                    table.add(otherImage)
                            .height(resultLabel.getPrefHeight() + 4)
                            .width(resultLabel.getPrefHeight() + 4)
                            .padTop(5).padLeft(5).padBottom(5);
                }
            }

            table.add(resultLabel).minWidth(minWidth).pad(5);
        };
        return createAndShowTable(delay, consumer, minWidth > 0 ? -95/2 : 0);
    }

    public static LabelTable createAndShowTable(float delay, String labelText) {

        Consumer<Table> consumer = table -> {
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = getBitmapFont(FONT_MINYA);
            labelStyle.fontColor = Color.YELLOW;

            Label resultLabel = new Label(labelText, labelStyle);
            resultLabel.setAlignment(Align.center, Align.center);
            resultLabel.setFontScale(0.5f);

            table.add(resultLabel).pad(5);
        };
        return createAndShowTable(delay, consumer, 0);
    }

    private static LabelTable createAndShowTable(float delay, Consumer<Table> prepareTableFunction, int offsetX) {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        LabelTable table = new LabelTable() {
            @Override
            public float getHeight() {
               return 32f;
            }
        };

        prepareTableFunction.accept(table);
        table.pack();
        table.setBackground(getTextureRegionDrawable(GRAY_BACKGROUND));

        table.setPosition(VIEWPORT_WIDTH / 2f - table.getWidth() / 2 + offsetX, InfoStage.getBottomHeight());

        InfoStage.addSmallActorToInfoStage(table);
        table.setColor(new Color(1f, 1f, 1f, 0f));
        table.addAction(Actions.alpha(1, delay));
        InfoStage.setBottomHeight(InfoStage.getBottomHeight() + table.getHeight() + 5);

        return table;
    }
}
