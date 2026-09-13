package sk.sivak.eldritchhorror.core.view.components.sheet.ancientone;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

final class AncientOneStyles {
    static final int RULE_FONT_SIZE = 32;

    static final Color BRONZE = Color.valueOf("76603D");

    static Label title(String text) {
        Label label = new Label(text, new Label.LabelStyle(CustomAssetManager.getBitmapFontNew(
                CustomAssetManager.NEW_FONT_CINZEL), Color.valueOf("362A1C")));
        label.setFontScale(0.65f);
        label.setAlignment(com.badlogic.gdx.utils.Align.center);
        return label;
    }

    static Actor divider() {
        Image line = new Image(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND));
        line.setColor(BRONZE);
        return line;
    }

    static void addCloseButton(Group card) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 16);
        style.fontColor = Color.valueOf("362A1C");
        style.overFontColor = Color.valueOf("8C292D");
        TextButton close = new TextButton(UiText.get("ancientOne.close"), style);
        close.setBounds(card.getWidth() - 108, card.getHeight() - 43, 68, 30);
        close.addListener(new ClickListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override public void clicked(InputEvent event, float x, float y) {
                event.stop();
                BigActorsManager.displayOrHideAncientOne();
            }
        });
        card.addActor(close);
    }

    private AncientOneStyles() { }

    static Drawable ruleBackground() {
        Drawable background = new BaseDrawable();
        background.setMinWidth(0f);
        background.setMinHeight(0f);
        background.setLeftWidth(8f);
        background.setRightWidth(8f);
        background.setTopHeight(6f);
        background.setBottomHeight(6f);
        return background;
    }
}
