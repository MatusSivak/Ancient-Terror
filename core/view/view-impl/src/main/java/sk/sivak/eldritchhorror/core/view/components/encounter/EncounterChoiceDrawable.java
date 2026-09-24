package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Thin edges stay crisp at any button size, without stretching decorative artwork. */
final class EncounterChoiceDrawable extends BaseDrawable {
    private static final int STRIPE_WIDTH = 4;

    static final EncounterChoiceDrawable NORMAL = new EncounterChoiceDrawable(0x172421ff, 0x736344ff);
    static final EncounterChoiceDrawable HOVER = new EncounterChoiceDrawable(0x253831ff, 0xbda16aff);
    static final EncounterChoiceDrawable PRESSED = new EncounterChoiceDrawable(0x101b18ff, 0xd2b77aff);
    static final EncounterChoiceDrawable DISABLED = new EncounterChoiceDrawable(0x222321ff, 0x514b3fff);

    private final Color fill;
    private final Color edge;
    private final Color stripe;

    private EncounterChoiceDrawable(int fill, int edge) {
        this(new Color(fill), new Color(edge), null);
    }

    private EncounterChoiceDrawable(Color fill, Color edge, Color stripe) {
        this.fill = fill;
        this.edge = edge;
        this.stripe = stripe;
    }

    /** Normal, hover and pressed variants sharing one fill; only the edge follows the accent color. */
    static EncounterChoiceDrawable[] forAccent(Color accent) {
        Color dimEdge = accent.cpy().mul(0.5f, 0.5f, 0.5f, 1f);
        Color brightEdge = accent.cpy().lerp(Color.WHITE, 0.2f);
        return new EncounterChoiceDrawable[]{
                new EncounterChoiceDrawable(NORMAL.fill, dimEdge, accent),
                new EncounterChoiceDrawable(HOVER.fill, accent, brightEdge),
                new EncounterChoiceDrawable(PRESSED.fill, brightEdge, brightEdge)
        };
    }

    @Override public void draw(Batch batch, float x, float y, float width, float height) {
        Color color = batch.getColor();
        float r = color.r, g = color.g, b = color.b, a = color.a;
        Texture white = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
        batch.setColor(r * edge.r, g * edge.g, b * edge.b, a);
        batch.draw(white, x, y, width, height);
        batch.setColor(r * fill.r, g * fill.g, b * fill.b, a);
        batch.draw(white, x + 1, y + 1, width - 2, height - 2);
        if (stripe != null) {
            batch.setColor(r * stripe.r, g * stripe.g, b * stripe.b, a);
            batch.draw(white, x + 1, y + 1, STRIPE_WIDTH, height - 2);
        }
        batch.setColor(r, g, b, a);
    }
}
