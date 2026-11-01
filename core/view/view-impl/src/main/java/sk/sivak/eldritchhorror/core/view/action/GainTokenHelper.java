package sk.sivak.eldritchhorror.core.view.action;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.ItemActor;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.components.hud.InvestigatorHud;
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
        return gainTokenAndUpdateHud(amount, containerBar, tokenId, infoText, cue, false);
    }

    /**
     * @param completeOnSpawn complete once the last token has materialized; the HUD is still updated when each token lands
     */
    public static Completable gainTokenAndUpdateHud(int amount, ContainerBar containerBar, String tokenId, String infoText,
                                                    TokenSounds.Cue cue, boolean completeOnSpawn) {
        Completable theBigOne = Completable.complete();
        for (int i = 0; i < amount; i++) {
            Vector2 emptyContainerPosition = containerBar.getEmptyContainerPosition(i);
            int tokenIndex = i;
            Completable playCue = cue == null ? Completable.complete() : Completable.fromAction(() -> TokenSounds.playGain(cue, tokenIndex));
            Action0 onLanded = () -> {
                if (cue != null) {
                    TokenSounds.playLand(cue, tokenIndex);
                }
                containerBar.increaseCurrentValue();
            };
            theBigOne = theBigOne.andThen(playCue.andThen(gainToken(tokenId, emptyContainerPosition, infoText, completeOnSpawn, onLanded)));
        }
        return theBigOne;
    }

    /**
     * Call when the token spawns. Registers the token as flying towards the HUD, so the HUD is not switched to another
     * investigator before it lands. The returned action still skips the landing when the HUD values were reset from
     * the model in the meantime, because they already include the token.
     */
    public static Action0 skipIfHudReset(Action0 onLanded) {
        InvestigatorHud hud = InfoStage.getInvestigatorHud();
        int version = hud.getValuesVersion();
        hud.tokenLaunched();
        return () -> {
            if (InfoStage.getInvestigatorHud() == hud && hud.getValuesVersion() == version) {
                onLanded.call();
            }
            hud.tokenLanded();
        };
    }

    private static Completable gainToken(String textureId, Vector2 holderLocation, String text, boolean completeOnSpawn,
                                         Action0 onLanded) {
        return Completable.defer(() -> {
            InfoStage.displayText(text);
            ItemActor itemActor = new ItemActor(CustomAssetManager.getTexture(textureId));
            if (textureId.equals(CustomAssetManager.HEALTH_ICON)) itemActor.setMaterializeColor(new Color(1, .28f, .18f, 1));
            else if (textureId.equals(CustomAssetManager.SANITY_ICON)) itemActor.setMaterializeColor(new Color(.3f, .65f, 1, 1));
            else if (textureId.equals(CustomAssetManager.CLUE_TOKEN)) itemActor.setMaterializeColor(new Color(.35f, 1, .65f, 1));
            InfoStage.addSmallActorToInfoStage(itemActor);
            itemActor.setHolderLocation(holderLocation);
            return itemActor.gain(completeOnSpawn, skipIfHudReset(onLanded));
        });
    }
}
