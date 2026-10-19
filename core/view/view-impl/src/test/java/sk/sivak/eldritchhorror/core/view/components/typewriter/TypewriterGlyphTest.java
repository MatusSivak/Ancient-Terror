package sk.sivak.eldritchhorror.core.view.components.typewriter;

import org.junit.Test;
import rx.CompletableSubscriber;

import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher.*;

public class TypewriterGlyphTest {
    private final CapturingTypewriter view = new CapturingTypewriter();

    @Test public void ruleTextUsesIconsAndPreservesModifiersAndOutcomeColors() {
        view.typeInfo("Test Observation-1 or Will+2. [#BAD]Lose 1 Health[]; [#GOOD]gain 2 Clues[].")
                .subscribe();
        assertEquals("Test " + icon("Observation") + "-1 or " + icon("Will")
                + "+2. [#" + TypewriterConstants.HEX_BAD + "]Lose 1 " + icon("Health")
                + "[]; [#" + TypewriterConstants.HEX_GOOD + "]gain 2 " + icon("Clues") + "[].", view.text);
    }

    @Test public void ruleTextUsesAllSkillsAndDiceWithoutChangingQuantitiesOrNames() {
        view.typeInfo("William is Focused. Test Lore, Influence or Strength. Roll 1 die. On a 1 or 2, lose 2 Sanity.")
                .subscribe();
        assertEquals("William is Focused. Test " + icon("Lore") + ", " + icon("Influence")
                + " or " + icon("Strength") + ". Roll 1 die. On a [#FFFFFFFF]" + getDiceGlyph(1)
                + "[] or [#FFFFFFFF]" + getDiceGlyph(2) + "[], lose 2 " + icon("Sanity") + ".", view.text);
    }

    @Test public void improvementRulesRetainIconsAndFlavorRemainsProse() {
        view.typeInfo("Improve Observation.").subscribe();
        assertEquals("Improve " + icon("Observation") + ".", view.text);
        view.typeFlavor("Will you follow the Clues?").subscribe();
        assertEquals("Will you follow the Clues?\n ", view.text);
    }

    private static String icon(String keyword) { return "[#FFFFFFFF]" + getGlyph(keyword) + "[]"; }

    @Test public void ticketRulesUseIconsButFlavorKeepsItsWords() {
        view.typeInfo("[#GOOD]Gain one Ship Ticket[] and 2 train tickets; spend 1 TRAIN TICKET.").subscribe();
        assertEquals("[#" + TypewriterConstants.HEX_GOOD + "]Gain one " + icon("Ship Ticket")
                + "[] and 2 " + icon("Train Ticket") + "; spend 1 " + icon("Train Ticket") + ".", view.text);
        view.typeFlavor("The conductor takes your train ticket.").subscribe();
        assertEquals("The conductor takes your train ticket.\n ", view.text);
    }

    @Test public void reckoningUsesGlyphInRulesRegardlessOfCaseButNotFlavorOrLongerWords() {
        view.typeInfo("[#BAD]Reckoning![] Resolve reckoning and RECKONING; reckonings remain text.").subscribe();
        assertEquals("[#" + TypewriterConstants.HEX_BAD + "]" + icon("RECKONING")
                + "![] Resolve " + icon("RECKONING") + " and " + icon("RECKONING")
                + "; reckonings remain text.", view.text);
        view.typeFlavor("The day of Reckoning has come.").subscribe();
        assertEquals("The day of Reckoning has come.\n ", view.text);
    }

    private static class CapturingTypewriter extends TypewriterViewImpl {
        String text;
        @Override void prepareAndRepeatRuns(String text, CompletableSubscriber subscriber) {
            this.text = replacePlaceholders(text);
            subscriber.onCompleted();
        }
    }
}
