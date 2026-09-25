package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardZoomPlacementTest {
    @Test
    public void copyMatchesAllCornersInsideScaledAndScrolledParents() {
        Group passport = new Group();
        passport.setPosition(180, 90);
        passport.setOrigin(380, 225);
        passport.setScale(0.9f);
        Group fittedSheet = new Group();
        fittedSheet.setPosition(35, 50);
        fittedSheet.setScale(0.78f);
        passport.addActor(fittedSheet);
        Group scrolledRow = new Group();
        scrolledRow.setPosition(-137, 24);
        fittedSheet.addActor(scrolledRow);
        Actor card = new Actor();
        card.setPosition(205, 10);
        card.setScale(0.16f);
        scrolledRow.addActor(card);
        assertMatchingCorners(card);
        // Closing must use the current location, even after scrolling or relayout.
        scrolledRow.setX(-220);
        fittedSheet.setScale(0.86f);
        assertMatchingCorners(card);
    }

    @Test
    public void copyMatchesStandaloneCardWithNonzeroOrigin() {
        Actor card = new Actor();
        card.setPosition(40, 80);
        card.setOrigin(80, 120);
        card.setScale(0.133f, 0.15f);
        assertMatchingCorners(card);
    }

    private void assertMatchingCorners(Actor card) {
        CardZoomPlacement placement = new CardZoomPlacement(card, 1191, 1254, 595, 627);
        Actor copy = new Actor();
        copy.setOrigin(595, 627);
        copy.setPosition(placement.x, placement.y);
        copy.setScale(placement.scaleX, placement.scaleY);
        for (float x : new float[]{0, 1191}) {
            for (float y : new float[]{0, 1254}) {
                Vector2 expected = card.localToStageCoordinates(new Vector2(x, y));
                Vector2 actual = copy.localToStageCoordinates(new Vector2(x, y));
                assertEquals(expected.x, actual.x, 0.001f);
                assertEquals(expected.y, actual.y, 0.001f);
            }
        }
    }
}
