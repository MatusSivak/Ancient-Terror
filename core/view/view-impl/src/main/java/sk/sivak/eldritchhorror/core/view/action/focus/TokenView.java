package sk.sivak.eldritchhorror.core.view.action.focus;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.TokenActor;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.action.GainTokenHelper.gainTokenAndUpdateHud;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class TokenView {

    public Completable gainClueFromPool() {
        return gainTokenAndUpdateHud(1, InfoStage.getInvestigatorHud().getClueBar(), CustomAssetManager.CLUE_TOKEN, get("focus.gainingClue"));
    }

    public Completable gainClueFromSpace(LocationId locationId) {
        return GainClueFromSpaceHelper.gainClueFromSpace(locationId);
    }

    public Completable gainFocus(int amount) {
        return gainTokenAndUpdateHud(amount, InfoStage.getInvestigatorHud().getFocusBar(), CustomAssetManager.FOCUS_TOKEN, get("focus.gainingFocus"));
    }

    public Completable justFocus() {
        return Completable.create(onSub -> InfoStage.addActionToInfoStage(Actions.sequence(
                Actions.run(() -> InfoStage.displayText(get("focus.focusing"))),
                new FastForwardAction<>(Actions.delay(ViewProperties.NORMAL_ACTION_DURATION * 2)),
                Actions.run(onSub::onCompleted)
        )));
    }

    public Completable loseFocus() {
        return loseToken(FOCUS_TOKEN_LEFT, FOCUS_TOKEN_RIGHT, InfoStage.getInvestigatorHud().getFocusBar(), new Vector2(27,27),0);
    }

    public Completable loseClue() {
        return loseToken(CLUE_TOKEN_LEFT, CLUE_TOKEN_RIGHT, InfoStage.getInvestigatorHud().getClueBar(), new Vector2(27,27),0);
    }

    public Completable loseToken(String leftTexture, String rightTexture, ContainerBar containerBar, Vector2 tokenSize, float offsetX) {
        return Completable.create(onMainSub -> {
            TokenActor tokenActor = new TokenActor(getTexture(leftTexture), getTexture(rightTexture));
            tokenActor.setHolderLocation(containerBar.getFullContainerPosition());
            tokenActor.setPosition(-tokenActor.getWidth(), -tokenActor.getHeight());
            tokenActor.setSize(tokenSize.x,tokenSize.y);
            InfoStage.addSmallActorToInfoStage(tokenActor);
            containerBar.decreaseCurrentValue();
            tokenActor.discard(offsetX).subscribe(onMainSub::onCompleted);
        });
    }

    public Completable loseHealth(float offsetX) {
        return loseToken(HEALTH_ICON_LEFT, HEALTH_ICON_RIGHT, InfoStage.getInvestigatorHud().getHealthBar(), new Vector2(22,27), offsetX);
    }

    public Completable loseSanity(float offsetX) {
        return loseToken(SANITY_ICON_LEFT, SANITY_ICON_RIGHT, InfoStage.getInvestigatorHud().getSanityBar(), new Vector2(22,27), offsetX);
    }
}
