package sk.sivak.eldritchhorror.core.view.action.rest;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.action.GainTokenHelper.gainTokenAndUpdateHud;

/**
 * @author msivak
 */
public class RestActionView {

    public Completable gainHealth(int amount) {
        return gainTokenAndUpdateHud(amount, InfoStage.getInvestigatorHud().getHealthBar(), CustomAssetManager.HEALTH_ICON, "Health Restored");
    }

    public Completable gainSanity(int amount) {
        return gainTokenAndUpdateHud(amount, InfoStage.getInvestigatorHud().getSanityBar(), CustomAssetManager.SANITY_ICON, "Sanity Restored");
    }

    public Completable justRest() {
        return Completable.create(onSub -> InfoStage.addActionToInfoStage(Actions.sequence(
                Actions.run(() -> InfoStage.displayText("Resting...")),
                new FastForwardAction<>(Actions.delay(ViewProperties.NORMAL_ACTION_DURATION * 2)),
                Actions.run(onSub::onCompleted)
        )));
    }
}
