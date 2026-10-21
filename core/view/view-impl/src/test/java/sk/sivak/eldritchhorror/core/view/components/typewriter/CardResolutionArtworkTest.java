package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardResolutionArtworkTest {

    @Test
    public void showFadesInInsteadOfPoppingUp() {
        Group layer = new Group();
        Image image = transparentImage();

        CardResolutionArtwork.show(layer, image);
        assertSame(layer, image.getParent());
        assertEquals(0f, image.getColor().a, 0.001f);

        layer.act(0.5f);
        assertTrue(image.getColor().a > 0f && image.getColor().a < 1f);

        layer.act(0.6f);
        assertEquals(1f, image.getColor().a, 0.001f);
    }

    @Test
    public void hideFadesOutAndThenDetaches() {
        Group layer = new Group();
        Image image = transparentImage();
        CardResolutionArtwork.show(layer, image);
        layer.act(1.1f);

        CardResolutionArtwork.hide(image);
        layer.act(0.5f);
        assertSame(layer, image.getParent());
        assertTrue(image.getColor().a < 1f);

        layer.act(0.6f);
        layer.act(0.01f);
        assertNull(image.getParent());
    }

    @Test
    public void reshowDuringFadeOutCancelsRemovalButKeepsZoom() {
        Group layer = new Group();
        Image image = transparentImage();
        image.addAction(Actions.scaleTo(1f, 1f, 5f));
        CardResolutionArtwork.show(layer, image);
        layer.act(1.1f);

        CardResolutionArtwork.hide(image);
        layer.act(0.5f);
        CardResolutionArtwork.show(layer, image);
        layer.act(2f);

        assertSame(layer, image.getParent());
        assertEquals(1f, image.getColor().a, 0.001f);
        assertEquals(1, image.getActions().size); // only the zoom remains
    }

    private static Image transparentImage() {
        Image image = new Image();
        image.getColor().a = 0f;
        return image;
    }
}
