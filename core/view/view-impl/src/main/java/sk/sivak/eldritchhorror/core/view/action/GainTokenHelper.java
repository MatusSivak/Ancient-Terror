package sk.sivak.eldritchhorror.core.view.action;

import com.badlogic.gdx.math.Vector2;
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
        Completable theBigOne = Completable.complete();
        for (int i = 0; i < amount; i++) {
            Vector2 emptyContainerPosition = containerBar.getEmptyContainerPosition(i);
            Completable gainToken = gainToken(tokenId, emptyContainerPosition, infoText);
            Completable updateHud = gainToken.andThen(Completable.create(onSub -> {
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
            InfoStage.addSmallActorToInfoStage(itemActor);
            itemActor.setHolderLocation(holderLocation);
            itemActor.gain().subscribe(onSub::onCompleted);
        });
    }
}
