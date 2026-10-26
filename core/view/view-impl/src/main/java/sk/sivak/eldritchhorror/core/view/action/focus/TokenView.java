package sk.sivak.eldritchhorror.core.view.action.focus;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.TokenActor;
import sk.sivak.eldritchhorror.core.view.components.ResourceSpendEffect;
import sk.sivak.eldritchhorror.core.view.components.VitalityLossEffect;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;
import sk.sivak.eldritchhorror.core.view.action.TokenSounds;

import static sk.sivak.eldritchhorror.core.view.action.GainTokenHelper.gainTokenAndUpdateHud;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class TokenView {

    public Completable gainClueFromPool() {
        return gainTokenAndUpdateHud(1, InfoStage.getInvestigatorHud().getClueBar(), CustomAssetManager.CLUE_TOKEN,
                get("focus.gainingClue"), TokenSounds.Cue.CLUE_GAIN);
    }

    public Completable gainClueFromSpace(LocationId locationId) {
        return GainClueFromSpaceHelper.gainClueFromSpace(locationId);
    }

    public Completable gainFocus(int amount) {
        return gainTokenAndUpdateHud(amount, InfoStage.getInvestigatorHud().getFocusBar(), CustomAssetManager.FOCUS_TOKEN,
                get("focus.gainingFocus"), TokenSounds.Cue.FOCUS_GAIN);
    }

    public Completable justFocus() {
        return Completable.create(onSub -> InfoStage.addActionToInfoStage(Actions.sequence(
                Actions.run(() -> InfoStage.displayText(get("focus.focusing"))),
                new FastForwardAction<>(Actions.delay(ViewProperties.NORMAL_ACTION_DURATION * 2)),
                Actions.run(onSub::onCompleted)
        )));
    }

    public Completable loseFocus() {
        return loseResource(InfoStage.getInvestigatorHud().getFocusBar(), ResourceSpendEffect.Kind.FOCUS);
    }

    public Completable loseClue() {
        return loseResource(InfoStage.getInvestigatorHud().getClueBar(), ResourceSpendEffect.Kind.CLUE);
    }

    private Completable loseResource(ContainerBar bar, ResourceSpendEffect.Kind kind) {
        return Completable.defer(() -> {
            if (bar.getCurrentValue() <= 0) return Completable.complete();
            return ResourceSpendEffect.play(InfoStage.getStageSafe(), bar.getFullContainerPosition(), 27, 27, kind,
                    bar::decreaseCurrentValue, () -> GameplaySounds.play(GameplaySounds.Cue.RESOURCE_SPEND));
        });
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
        return loseHealth(offsetX, () -> TokenSounds.playLoss(TokenSounds.Cue.LOSS, 1));
    }

    public Completable loseSanity(float offsetX) {
        return loseSanity(offsetX, () -> TokenSounds.playLoss(TokenSounds.Cue.LOSS, 1));
    }

    public Completable loseHealth(float offsetX, Action0 onTear) {
        return loseVitality(InfoStage.getInvestigatorHud().getHealthBar(), offsetX, VitalityLossEffect.Kind.HEALTH, onTear);
    }

    public Completable loseSanity(float offsetX, Action0 onTear) {
        return loseVitality(InfoStage.getInvestigatorHud().getSanityBar(), offsetX, VitalityLossEffect.Kind.SANITY, onTear);
    }

    private Completable loseVitality(ContainerBar bar, float offsetX, VitalityLossEffect.Kind kind, Action0 onTear) {
        return Completable.defer(() -> {
            if (bar.getCurrentValue() <= 0) return Completable.complete();
            return VitalityLossEffect.play(InfoStage.getStageSafe(), bar.getFullContainerPosition(), 22, 27,
                    offsetX, kind, bar::decreaseCurrentValue, onTear);
        });
    }
}
