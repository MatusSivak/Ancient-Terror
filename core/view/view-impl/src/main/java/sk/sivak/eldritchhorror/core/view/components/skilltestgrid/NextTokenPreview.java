package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class NextTokenPreview extends Group {
    private static final float TOKEN_BOUNDS_SCALE = 0.92f * 1.1f;

    private final GridTestAssets assets;
    private final Array<TextureRegion> spawnFrames;
    private final Image spawnEffect;
    private final GridSymbolActor tokenActor;
    private final Label hiddenLabel;
    private final NextTokenSpawnAnimationController animationController;
    private int displayedSpawnFrame = -1;
    private boolean hidden;

    public NextTokenPreview(GridTestAssets assets) {
        this.assets = assets;
        spawnFrames = assets.getSpawnFrames();
        animationController = new NextTokenSpawnAnimationController();

        spawnEffect = spawnFrames.size == 0
                ? null
                : new Image(new TextureRegionDrawable(spawnFrames.first()));
        if (spawnEffect != null) {
            spawnEffect.setScaling(Scaling.fit);
            spawnEffect.setVisible(false);
            addActor(spawnEffect);
        }

        tokenActor = new GridSymbolActor(assets, SymbolType.ONE);
        tokenActor.setScaling(Scaling.fit);
        tokenActor.setVisible(false);
        addActor(tokenActor);
        hiddenLabel = new Label(
                "?",
                new Label.LabelStyle(
                        CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_BLACK_CHANCERY),
                        Color.WHITE
                )
        );
        hiddenLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        hiddenLabel.setFontScale(2f);
        hiddenLabel.setVisible(false);
        addActor(hiddenLabel);
        setTouchable(Touchable.disabled);
    }

    public boolean setNextToken(SymbolType nextToken) {
        if (!animationController.setNextToken(nextToken)) {
            return false;
        }

        if (nextToken == null) {
            tokenActor.setVisible(false);
            hiddenLabel.setVisible(false);
            hideSpawnEffect();
            return true;
        }

        tokenActor.setSymbolType(assets, nextToken);
        tokenActor.setVisible(!hidden);
        hiddenLabel.setVisible(hidden);
        displayedSpawnFrame = -1;
        applyAnimationState();
        return true;
    }

    public void clearNextToken() {
        setNextToken(null);
    }

    public void setHidden(boolean hidden) {
        boolean revealing = this.hidden && !hidden;
        this.hidden = hidden;
        if (revealing) {
            animationController.restartCurrentAnimation();
        }
        applyAnimationState();
    }

    boolean isHidden() {
        return hidden;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animationController.update(delta);
        applyAnimationState();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (spawnEffect != null) {
            spawnEffect.setBounds(0f, 0f, getWidth(), getHeight());
        }

        float tokenWidth = getWidth() * TOKEN_BOUNDS_SCALE;
        float tokenHeight = getHeight() * TOKEN_BOUNDS_SCALE;
        tokenActor.setBounds(
                (getWidth() - tokenWidth) / 2f,
                (getHeight() - tokenHeight) / 2f,
                tokenWidth,
                tokenHeight
        );
        tokenActor.setOrigin(tokenWidth / 2f, tokenHeight / 2f);
        hiddenLabel.setBounds(0f, 0f, getWidth(), getHeight());
    }

    private void applyAnimationState() {
        if (animationController.getCurrentNextToken() == null) {
            tokenActor.setVisible(false);
            hiddenLabel.setVisible(false);
            hideSpawnEffect();
            return;
        }

        if (hidden) {
            tokenActor.setVisible(false);
            hiddenLabel.setVisible(true);
            hideSpawnEffect();
            return;
        }

        hiddenLabel.setVisible(false);
        tokenActor.setVisible(true);
        tokenActor.getColor().a = animationController.getTokenAlpha();
        float scale = animationController.getTokenScale();
        tokenActor.setScale(scale);

        if (spawnEffect == null || animationController.getPhase() == NextTokenSpawnAnimationController.Phase.IDLE) {
            hideSpawnEffect();
            return;
        }

        int frame = Math.min(
                spawnFrames.size - 1,
                (int) (animationController.getEffectProgress() * spawnFrames.size)
        );
        if (frame != displayedSpawnFrame) {
            spawnEffect.setDrawable(new TextureRegionDrawable(spawnFrames.get(frame)));
            displayedSpawnFrame = frame;
        }
        spawnEffect.getColor().a = animationController.getEffectAlpha();
        spawnEffect.setVisible(true);
    }

    private void hideSpawnEffect() {
        if (spawnEffect != null) {
            spawnEffect.setVisible(false);
            spawnEffect.getColor().a = 0f;
        }
    }
}
