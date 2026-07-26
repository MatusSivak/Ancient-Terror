package sk.sivak.eldritchhorror.core.view.components.phase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class PhaseLabel extends Image {

    private PhaseLabel(TextureRegion region) {
        super(region);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(0f);
        setPosition(ViewProperties.VIEWPORT_WIDTH / 2 - getWidth() / 2,
                ViewProperties.VIEWPORT_HEIGHT / 2 - getHeight() / 2);
    }

    public static PhaseLabel buildActionPhaseLabel() {
        return new PhaseLabel(CustomAssetManager.getTextureRegion(CustomAssetManager.PHASE_ACTION));
    }

    public static PhaseLabel buildEncounterPhaseLabel() {
        return new PhaseLabel(CustomAssetManager.getTextureRegion(CustomAssetManager.PHASE_ENCOUNTER));
    }

    public static PhaseLabel buildMythosPhaseLabel() {
        return new PhaseLabel(CustomAssetManager.getTextureRegion(CustomAssetManager.PHASE_MYTHOS));
    }

    public static PhaseLabel buildNewRoundPhaseLabel() {
        return new PhaseLabel(CustomAssetManager.getTextureRegion(CustomAssetManager.PHASE_NEW_ROUND));
    }

    public Completable display() {
        return Completable.create(this::display);
    }

    private void display(CompletableSubscriber onSub) {
        Image curtain = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        curtain.setSize(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT);
        curtain.setColor(Color.BLACK);
        curtain.getColor().a = 0f;
        getParent().addActorBefore(this, curtain);
        curtain.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.alpha(0.5f, 1),
                Actions.delay(1f),
                Actions.alpha(0f, 1),
                Actions.run(curtain::remove)
        )));
        float targetScale = (ViewProperties.VIEWPORT_WIDTH * 0.9f) / getWidth();
        float targetScale2 = (ViewProperties.VIEWPORT_WIDTH * 0.45f) / getWidth();
        HudButtons.getInstance().hide().subscribe();
        HudButtons.getTrackHud().hide().subscribe();
        InfoStage.getInvestigatorHud().hide().subscribe();
        addAction(
                sequence(
                        new FastForwardAction(scaleTo(targetScale, targetScale, 1f, Interpolation.sine)),
                        new FastForwardAction(delay(1f)),
                        run(onSub::onCompleted),
                        run(() -> {
                            HudButtons.getInstance().show().subscribe();
                            HudButtons.getTrackHud().show().subscribe();
                            InfoStage.getInvestigatorHud().show().subscribe();
                        }),
                        new FastForwardAction(parallel(
                                scaleTo(targetScale2, targetScale2, 1f, Interpolation.sine),
                                alpha(0, 1f)
                        )),
                        run(this::remove)
                )
        );
    }
}
