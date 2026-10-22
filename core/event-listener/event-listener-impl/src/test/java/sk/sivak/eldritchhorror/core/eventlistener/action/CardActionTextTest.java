package sk.sivak.eldritchhorror.core.eventlistener.action;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.artifact.LightningGunArtifact;
import sk.sivak.eldritchhorror.core.constants.condition.detained.DetainedCondition;
import sk.sivak.eldritchhorror.core.constants.spell.shriveling.ShrivelingSpell;

import static org.junit.Assert.assertEquals;
import static sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener.cardActionText;

public class CardActionTextTest {

    @Test
    public void extractsActionAbilityAndKeepsSentenceBreaks() {
        assertEquals("Test Lore.\nPass → 1 Monster on your space loses 2 Health. Then flip this card.",
                cardActionText(new ShrivelingSpell().getDescription()));
    }

    @Test
    public void ignoresOtherAbilitiesOnTheCard() {
        assertEquals("You and each Monster on your space lose 1 Health.",
                cardActionText(new LightningGunArtifact().getDescription()));
    }

    @Test
    public void extractsLocalAction() {
        assertEquals("Test Influence.\nPass → discard this card.",
                cardActionText(new DetainedCondition().getDescription()));
    }

    @Test
    public void usesFirstParagraphWhenCardHasNoActionMarker() {
        assertEquals("Test Lore+2.\nPass → discard 1 Monster on the nearest Gate with toughness up to your result. Then flip this card.",
                cardActionText("Test Lore+2.\nPass → discard 1 Monster on the\nnearest Gate with toughness up to\nyour result. Then flip this card."));
    }
}
