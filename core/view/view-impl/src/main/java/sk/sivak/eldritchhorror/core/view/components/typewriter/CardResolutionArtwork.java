package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

/** Artwork belongs to its paper, so nested resolutions restore the scene underneath. */
public final class CardResolutionArtwork {
    private static final float FADE_DURATION = 1f;
    private static final float ZOOM_DURATION = 5f;

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
        image.setOrigin(Align.center);
        image.setName("card-resolution-background");
        image.getColor().a = 0f;
        // Same slow push-in as encounter backgrounds.
        if (!Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled")) {
            image.setScale(1.5f);
            image.addAction(Actions.scaleTo(1f, 1f, ZOOM_DURATION, Interpolation.sineOut));
        }
        // The scene is a backdrop; clicks must not reach locations on the hidden map.
        image.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        return image;
    }

    /** Fades the artwork in on top of the layer, matching encounter background transitions. */
    static void show(Group layer, Image image) {
        image.removeAction(fadeOf(image));
        layer.addActor(image);
        image.addAction(new FadeMarker(Actions.alpha(1f, FADE_DURATION)));
    }

    /** Fades the artwork out and detaches it once invisible. */
    static void hide(Image image) {
        if (image.getParent() == null) return;
        image.removeAction(fadeOf(image));
        image.addAction(new FadeMarker(Actions.sequence(Actions.alpha(0f, FADE_DURATION), Actions.removeActor())));
    }

    private static Group suspendedLayer;
    private static Image suspended;
    private static boolean diceCheckActive;

    /** The paper was hidden: drop its artwork unless a dice check needs it as backdrop. */
    static void suspend(Group layer, Image image) {
        suspendedLayer = image == null ? null : layer;
        suspended = image;
        if (image != null && !diceCheckActive) hide(image);
    }

    /** A paper is visible again; any previously hidden artwork no longer applies. */
    static void clearSuspended() {
        suspendedLayer = null;
        suspended = null;
    }

    /** Dice checks hide the paper, but the card scene must stay behind the dice. */
    public static void retainDuringDiceCheck(Group layer) {
        diceCheckActive = true;
        if (suspended != null && suspendedLayer == layer) show(layer, suspended);
    }

    public static void releaseAfterDiceCheck() {
        if (!diceCheckActive) return;
        diceCheckActive = false;
        // The owning paper resumes with the result (or finishes). Keep its scene
        // until then instead of fading out and immediately fading back in.
    }

    private static Action fadeOf(Image image) {
        for (Action action : image.getActions()) {
            if (action instanceof FadeMarker) return action;
        }
        return null;
    }

    /** Tags the fade so it can be replaced without cancelling the zoom. */
    private static final class FadeMarker extends DelegateAction {
        FadeMarker(Action action) { setAction(action); }

        @Override protected boolean delegate(float delta) {
            return action.act(delta);
        }
    }
}
