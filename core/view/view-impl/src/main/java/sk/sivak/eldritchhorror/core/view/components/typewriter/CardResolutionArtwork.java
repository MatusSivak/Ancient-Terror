package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

/** Artwork belongs to its paper, so nested resolutions restore the scene underneath. */
public final class CardResolutionArtwork {
    private CardResolutionArtwork() {}

    public static String textureName(CardInfo card) {
        if (card instanceof SpellInfo) return "card/spell/" + ((SpellInfo) card).getId().name() + ".jpg";
        if (card instanceof ConditionInfo) return "card/condition/" + ((ConditionInfo) card).getId().name() + ".jpg";
        if (card instanceof AssetInfo) return "card/asset/" + ((AssetInfo) card).getId().name() + ".jpg";
        if (card instanceof ArtifactInfo) return "card/artifact/" + ((ArtifactInfo) card).getId().name() + ".jpg";
        throw new IllegalArgumentException("Card has no resolution artwork: " + card.getId());
    }

    static Image create(Texture texture) {
        Image image = new Image(texture);
        image.setScaling(Scaling.fill);
        image.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        image.setName("card-resolution-background");
        // The scene is a backdrop; clicks must not reach locations on the hidden map.
        image.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        return image;
    }
}
