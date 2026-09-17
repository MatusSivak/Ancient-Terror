package sk.sivak.eldritchhorror.core.view.components.sheet;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class DisplayHide {
    private final BigActorsManager.BigActorKey bigActorKey;
    private boolean displayed = false;

    private Image backdrop;
    private Actor actor;
    private OnScreenActors.ActorKey actorKey;
    private float displayedY;
    private Float displayedX;
    private float displayedScale = 1f;

    private Action0 beforeDisplayAction = () -> {
    };
    private Action0 beforeHideAction = () -> {
    };
    private Action0 afterHideAction = () -> {
    };
    private Action0 afterDisplayAction = () -> {
    };

    public DisplayHide(Actor actor, BigActorsManager.BigActorKey bigActorKey) {
        this.actor = actor;
        this.bigActorKey = bigActorKey;
    }

    public void setActorKey(OnScreenActors.ActorKey actorKey) {
        this.actorKey = actorKey;
    }

    public void setDisplayedY(float displayedY) {
        this.displayedY = displayedY;
    }

    public void setDisplayedX(float displayedX) {
        this.displayedX = displayedX;
    }

    public void setDisplayedScale(float displayedScale) {
        this.displayedScale = displayedScale;
    }

    private float getDisplayedX() {
        return displayedX == null ? (VIEWPORT_WIDTH - actor.getWidth()) / 2 : displayedX;
    }
    public void setAfterDisplayAction(Action0 afterDisplayAction) {
        this.afterDisplayAction = afterDisplayAction;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public Completable displayOrHide() {
        if (displayed) {
            return hide();
        }
        actor.setTouchable(Touchable.disabled);
        this.displayed = true;

        return Completable.create(onSub -> {
            beforeDisplayAction.call();
            InfoStage.addBigActor(actorKey, actor, () -> hide().subscribe(), null);
            if (bigActorKey == BigActorsManager.BigActorKey.ANCIENT_ONE) {
                backdrop = new Image(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND));
                backdrop.setColor(0f, 0f, 0f, 0.45f);
                backdrop.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
                actor.getParent().addActorBefore(actor, backdrop);
                ButtonUtils.addClickListener(backdrop, () -> {
                    if (displayed && actor.getTouchable() == Touchable.enabled) {
                        BigActorsManager.displayOrHideAncientOne();
                    }
                });
            }
            actor.setPosition(getDisplayedX(), -actor.getHeight());
            actor.setOrigin(actor.getWidth() / 2, actor.getHeight() / 2);
            actor.setScale(0f);
            actor.addAction(Actions.sequence(
                    new FastForwardAction(Actions.parallel(
                            Actions.moveTo(getDisplayedX(), displayedY, 0.5f, Interpolation.linear),
                            Actions.scaleTo(0.33f, 0.33f, 0.5f, Interpolation.linear)
                    )),
                    new FastForwardAction(Actions.scaleTo(displayedScale, displayedScale, 0.5f, Interpolation.swingOut)), // zoom
                    Actions.run(() -> {
                        BigActorsManager.unlock(bigActorKey);
                        actor.setTouchable(Touchable.enabled);
                        afterDisplayAction.call();
                        onSub.onCompleted();
                    }))
            );
        });
    }

    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    public void setBeforeHideAction(Action0 beforeHideAction) {
        this.beforeHideAction = beforeHideAction;
    }

    public void setAfterHideAction(Action0 afterHideAction) {
        this.afterHideAction = afterHideAction;
    }

    private Completable hide() {
        displayed = false;
        beforeHideAction.call();
        return Completable.create(onSub -> {
            actor.setTouchable(Touchable.disabled);
            actor.setOrigin(actor.getWidth() / 2, actor.getHeight() / 2);
            actor.addAction(Actions.sequence(
                    new FastForwardAction(Actions.scaleTo(0.33f, 0.33f, 0.5f, Interpolation.swingIn)), // zoom
                    new FastForwardAction(Actions.parallel(
                            Actions.moveTo(getDisplayedX(), -actor.getHeight(), 0.5f, Interpolation.linear),
                            Actions.scaleTo(0f, 0f, 0.5f, Interpolation.linear)
                    )),
                    Actions.run(() -> {
                        BigActorsManager.unlock(bigActorKey);
                        actor.remove();
                        if (backdrop != null) {
                            backdrop.remove();
                            backdrop = null;
                        }
                        afterHideAction.call();
                        onSub.onCompleted();
                    }))
            );
        });
    }
}
