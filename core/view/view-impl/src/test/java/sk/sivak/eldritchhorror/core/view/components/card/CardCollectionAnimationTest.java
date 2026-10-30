package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import static org.junit.Assert.*;

public class CardCollectionAnimationTest {
    @Test public void cardRotatesShrinksAndArrivesAtRecipientWithoutFadingEarly() {
        float oldDuration = ViewProperties.NORMAL_ACTION_DURATION;
        ViewProperties.NORMAL_ACTION_DURATION = 1f;
        try {
            Actor card = new Actor();
            card.setSize(200, 300); card.setOrigin(100, 150);
            card.setPosition(300, 200); card.setScale(.8f);
            TemporalAction flight = CardCollectionAnimation.flight(card, new Vector2(80, 90));
            flight.setActor(card);
            assertFalse(flight.act(.425f));
            assertEquals(180f, card.getRotation(), .01f);
            assertTrue(card.getScaleX() < .8f && card.getScaleX() > 0f);
            assertEquals(1f, card.getColor().a, .001f);
            assertTrue(flight.act(1f));
            assertEquals(80f, card.getX() + card.getOriginX(), .001f);
            assertEquals(90f, card.getY() + card.getOriginY(), .001f);
            assertEquals(360f, card.getRotation(), .001f);
            assertEquals(0f, card.getColor().a, .001f);
        } finally { ViewProperties.NORMAL_ACTION_DURATION = oldDuration; }
    }
}
