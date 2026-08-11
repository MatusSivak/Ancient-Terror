package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/**
 * @author msivak
 */
public class YellowButton extends CustomButton {

    private final Image buttonBackgroundUp;
    private final Image buttonBackgroundDown;
    private Image backgroundToDraw;

    private YellowButton(Drawable imageUp, Drawable imageDown) {
        super(imageUp, imageDown);
        buttonBackgroundUp = createBackground(CustomAssetManager.LOCATION_BUTTON_UP);
        buttonBackgroundDown = createBackground(CustomAssetManager.LOCATION_BUTTON_DOWN);
        setColor(0.94f, 0.89f, 0.69f, 1f);

        buttonBackgroundUp.setOrigin(buttonBackgroundUp.getWidth() / 2, buttonBackgroundUp.getHeight() / 2);
        buttonBackgroundDown.setOrigin(buttonBackgroundDown.getWidth() / 2, buttonBackgroundDown.getHeight() / 2);

        backgroundToDraw = buttonBackgroundUp;
    }

    private Image createBackground(String textureRegionId) {
        return new Image(CustomAssetManager.getTextureRegion(textureRegionId)) {


            private static final float SCALE = 1.8f;

            @Override
            public float getWidth() {
                return YellowButton.this.getWidth() * SCALE;
            }

            @Override
            public float getHeight() {
                return YellowButton.this.getHeight() * SCALE;
            }

            @Override
            public float getX() {
                return YellowButton.this.getX() - ((SCALE - 1) * YellowButton.this.getWidth()) / 2;
            }

            @Override
            public float getY() {
                return YellowButton.this.getY() - ((SCALE - 1) * YellowButton.this.getHeight()) / 2;
            }

            @Override
            public float getScaleX() {
                return YellowButton.this.getImage().getScaleX();
            }

            @Override
            public float getScaleY() {
                return YellowButton.this.getImage().getScaleY();
            }

            @Override
            public float getRotation() {
                getLeftRotationAngle();
                return YellowButton.this.getImage().getRotation() + (getLeftRotationAngle() + getRightRotationAngle()) / -2;
            }

            @Override
            public Color getColor() {
                return YellowButton.this.getColor();
            }
        };
    }

    @Override
    public void act(float delta) {
        if (isPressed()) {
            backgroundToDraw = buttonBackgroundDown;
        } else {
            backgroundToDraw = buttonBackgroundUp;
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        backgroundToDraw.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }

    public static class TrainButton extends YellowButton {

        public TrainButton() {
            super(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.TICKET_TRAIN_DOWN),
                    CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.TICKET_TRAIN_DOWN));
        }

        @Override
        protected int getLeftRotationAngle() {
            return super.getLeftRotationAngle() - 45;
        }

        @Override
        protected int getRightRotationAngle() {
            return super.getRightRotationAngle() - 45;
        }
    }

    public static class ShipButton extends YellowButton {

        public ShipButton() {
            super(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.TICKET_SHIP_DOWN),
                    CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.TICKET_SHIP_DOWN));
        }

        @Override
        protected int getLeftRotationAngle() {
            return super.getLeftRotationAngle() - 45;
        }

        @Override
        protected int getRightRotationAngle() {
            return super.getRightRotationAngle() - 45;
        }
    }
}
