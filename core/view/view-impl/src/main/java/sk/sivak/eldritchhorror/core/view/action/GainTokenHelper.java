package sk.sivak.eldritchhorror.core.view.action;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.ItemActor;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

/**
 * @author msivak
 */
public class GainTokenHelper {
    public static Completable gainTokenAndUpdateHud(int amount, ContainerBar containerBar, String tokenId, String infoText) {
        return gainTokenAndUpdateHud(amount, containerBar, tokenId, infoText, null);
    }

    public static Completable gainTokenAndUpdateHud(int amount, ContainerBar containerBar, String tokenId, String infoText,
                                                    TokenSounds.Cue cue) {
        Completable theBigOne = Completable.complete();
        for (int i = 0; i < amount; i++) {
            Vector2 emptyContainerPosition = containerBar.getEmptyContainerPosition(i);
            int tokenIndex = i;
            Completable playCue = cue == null ? Completable.complete() : Completable.fromAction(() -> TokenSounds.playGain(cue, tokenIndex));
            Completable gainToken = playCue.andThen(gainToken(tokenId, emptyContainerPosition, infoText));
            Completable updateHud = gainToken.andThen(Completable.create(onSub -> {
                if (cue != null) {
                    TokenSounds.playLand(cue, tokenIndex);
                }
                containerBar.increaseCurrentValue();
                onSub.onCompleted();
            }));
            theBigOne = theBigOne.andThen(updateHud);

        }
        return theBigOne;
    }

    private static Completable gainToken(String textureId, Vector2 holderLocation, String text) {
        return Completable.create(onSub -> {
            InfoStage.displayText(text);
            ItemActor itemActor = new ItemActor(CustomAssetManager.getTexture(textureId));
            if (textureId.equals(CustomAssetManager.HEALTH_ICON)) itemActor.setMaterializeColor(new Color(1, .28f, .18f, 1));
            else if (textureId.equals(CustomAssetManager.SANITY_ICON)) itemActor.setMaterializeColor(new Color(.3f, .65f, 1, 1));
            else if (textureId.equals(CustomAssetManager.CLUE_TOKEN)) itemActor.setMaterializeColor(new Color(.35f, 1, .65f, 1));
            InfoStage.addSmallActorToInfoStage(itemActor);
            itemActor.setHolderLocation(holderLocation);
            itemActor.gain().subscribe(onSub::onCompleted);
        });
    }
}
