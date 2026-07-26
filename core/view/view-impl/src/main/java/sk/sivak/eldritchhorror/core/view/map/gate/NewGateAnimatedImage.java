package sk.sivak.eldritchhorror.core.view.map.gate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class NewGateAnimatedImage extends Group {

    private final Image haloImage;
    private final AnimatedImage gateAnimatedImage;
    private final Image gradientImage;
    private final SpriteBatch spriteBatch;
    private final GateColor gateColor;
    private FrameBuffer frameBuffer;
    private TextureRegion textureRegion;
    private float parentAlpha = 1f;

    public NewGateAnimatedImage(GateColor gateColor, OmenColor omenColor) {
        this.gateColor = gateColor;
        setTouchable(Touchable.disabled);
        gateAnimatedImage = new AnimatedImage(new Animation<>(0.04f, CustomAssetManager.getGateAnimation(), Animation.PlayMode.LOOP));
        gradientImage = new Image(CustomAssetManager.getTexture("gate/close_gradient.png"));
        haloImage = createHaloImage(gateColor);

        gateAnimatedImage.setScale(0);
        haloImage.setScale(0);
        gradientImage.setScale(0);


        updateGateTransparency(omenColor);

        haloImage.addAction(createPulseAction(0.9f, 1f));
        gateAnimatedImage.addAction(createPulseAction(0.85f, 0.95f));

        addActor(gateAnimatedImage);
        addActor(haloImage);
        addActor(gradientImage);


        spriteBatch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                512,
                512, true);
    }

    public void updateGateTransparency(OmenColor omenColor) {
        if (omenColor != null && gateColor.equals(omenColor.toGateColor())) {
            gateAnimatedImage.getColor().a = 1.0f;
            gateAnimatedImage.setFrameDuration(0.04f);
        } else {
            gateAnimatedImage.getColor().a = 0.75f;
            gateAnimatedImage.setFrameDuration(0.08f);
        }
    }

    public Completable closeGate() {
        return Completable.create(onSub -> {
            haloImage.clearActions();
            gateAnimatedImage.clearActions();
            addAction(Actions.sizeTo(getWidth() * 2f, getHeight() * 2f, 1.5f));
            addAction(Actions.moveBy(- getWidth()/2f, -getHeight()/2f, 1.5f));
            gradientImage.setOrigin(getWidth()/2f, getHeight()/2f);
            gradientImage.addAction(Actions.sequence(
                    Actions.scaleTo(5f, 5f, 2f, Interpolation.exp5In),
                    Actions.run(() -> {
                        gradientImage.remove();
                        haloImage.remove();
                        gateAnimatedImage.remove();
                        onSub.onCompleted();
                        remove();
                    })
            ));
        });
    }

    private RepeatAction createPulseAction(float minScale, float maxScale) {
        return Actions.repeat(
                RepeatAction.FOREVER, Actions.sequence(
                        Actions.scaleTo(minScale, minScale, 0.75f, Interpolation.sineIn),
                        Actions.scaleTo(maxScale, maxScale, 0.75f, Interpolation.sineOut)
                )
        );
    }

    private Image createHaloImage(GateColor gateColor) {
        Image haloImage = new Image(CustomAssetManager.getTexture(CustomAssetManager.GATE_GRADIENT));
        switch (gateColor) {
            case RED:
                haloImage.setColor(new Color(1, 0, 0, 0.5f));
                break;
            case GREEN:
                haloImage.setColor(new Color(0, 1, 0, 0.5f));
                break;
            case BLUE:
                haloImage.setColor(new Color(0, 0, 1, 0.5f));
                break;
        }
        return haloImage;

    }

    @Override
    public void setOrigin(float originX, float originY) {
        super.setOrigin(originX, originY);
        for (Actor child : getChildren()) {
            child.setOrigin(originX, originY);
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        for (Actor child : getChildren()) {
            child.setSize(getWidth(), getHeight());
        }
        gradientImage.setOrigin(getWidth()/2f, gradientImage.getHeight()/2f);
        OrthographicCamera camera = new OrthographicCamera(getWidth(), getHeight());
        camera.position.x = getWidth() / 2f;
        camera.position.y = getHeight() / 2f;
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getParent()==null || getParent().getStage() == null) {
            return;
        }
        Viewport viewport = getParent().getStage().getViewport();
        frameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        gateAnimatedImage.draw(spriteBatch, parentAlpha);
        haloImage.draw(spriteBatch, parentAlpha);
        spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        gradientImage.draw(spriteBatch, 1f);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.end();
        frameBuffer.end(
                viewport.getLeftGutterWidth(),
                viewport.getBottomGutterHeight(),
                viewport.getScreenWidth(),
                viewport.getScreenHeight());

        textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture(),
                frameBuffer.getWidth(),
                frameBuffer.getHeight());
        textureRegion.flip(false, true);
        textureRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.parentAlpha = parentAlpha;
        if (textureRegion == null) {
            return;
        }
        batch.setColor(Color.WHITE);
        batch.draw(textureRegion, getX(),getY(),getWidth(),getHeight());
    }
}
