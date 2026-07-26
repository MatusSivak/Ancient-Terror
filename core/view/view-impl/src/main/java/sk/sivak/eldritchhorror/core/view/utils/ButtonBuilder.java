package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.shader.GrayscaleShader;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

/**
 * @author msivak
 */
public class ButtonBuilder {

    public static final Color GREEN = new Color(0x22b14cff);
    public static final Color RED = Color.RED;
    public static final Color GRAY = Color.GRAY;
    public static final Color WHITE = Color.LIGHT_GRAY;

    public static TextButton buildButton(String title) {
        TextureRegionDrawable drawableUp = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.SQUARE_BUTTON_UP);
        TextureRegionDrawable drawableDown = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.SQUARE_BUTTON_DOWN);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                drawableUp,
                drawableDown,
                null,
                getBitmapFont(FONT_ADLER)
        );
        float buttonHeightRatio = 0.09f;
        style.fontColor = Color.DARK_GRAY;

        TextButton button = new TextButton(title, style) {

            @Override
            public void setBackground(Drawable background) {
                if (getBackground() != background) {
                    if (background == drawableDown) {
                        padBottom(0);
                        padTop(getHeight() * 0.05f);
                    } else {
                        padBottom(getHeight() * 0.05f);
                        padTop(0);
                    }
                    invalidate();
                }
                super.setBackground(background);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (isDisabled()) {
                    batch.setShader(GrayscaleShader.get());
                    super.draw(batch, parentAlpha);
                    batch.setShader(null);
                } else {
                    super.draw(batch, parentAlpha);
                }
            }

            @Override
            public void setDisabled(boolean isDisabled) {
                if (isDisabled() == isDisabled) {
                    return;
                }
                if (isDisabled) {
                    getStyle().down = drawableUp;
                } else {
                    getStyle().down = drawableDown;
                }
                super.setDisabled(isDisabled);
            }


            @Override
            public void setScale(float scaleX, float scaleY) {
                if (isDisabled()) {
                    scaleX = 1f;
                    scaleY = 1f;
                }
                super.setScale(scaleX, scaleY);

            }
        };

        float buttonScale = VIEWPORT_HEIGHT * buttonHeightRatio / button.getHeight();
        button.setWidth(button.getWidth() * buttonScale);
        button.setHeight(button.getHeight() * buttonScale);
        button.padBottom(8 * buttonScale);

        button.setOrigin(button.getWidth() / 2, button.getHeight() / 2);
        button.setTransform(true);
        if (!Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled")) {
            button.addAction(createRepeatScaleAction(button));
        }
        button.getLabel().setFontScale(0.5f);
        return button;
    }

    private static RepeatAction createRepeatScaleAction(TextButton button) {
        return Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                createScaleAction(button, 0.8f), createScaleAction(button, 1f)
        ));
    }

    private static ScaleToAction createScaleAction(TextButton button, float scale) {
        ScaleToAction scaleToAction = new ScaleToAction();
        scaleToAction.setActor(button);
        scaleToAction.setInterpolation(Interpolation.sine);
        scaleToAction.setDuration(ViewProperties.FADING_EFFECT_DURATION);
        scaleToAction.setScale(scale);
        return scaleToAction;
    }
}
