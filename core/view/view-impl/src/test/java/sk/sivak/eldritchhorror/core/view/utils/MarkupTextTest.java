package sk.sivak.eldritchhorror.core.view.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher.getDiceGlyph;
import static sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher.getGlyph;

public class MarkupTextTest {

    @Test public void ticketsWorkInRulesCardsAndAbilitiesWithWordBoundaries() {
        String train = "[#FFFFFFFF]" + getGlyph("Train Ticket") + "[]";
        String ship = "[#FFFFFFFF]" + getGlyph("Ship Ticket") + "[]";
        String rule = "Spend 2 Train Tickets or 1 ship ticket.";
        assertEquals("Spend 2 " + train + " or 1 " + ship + ".", MarkupText.replaceColoredGlyphKeywords(rule));
        assertEquals("[#30251F]Spend 2 " + train + " or 1 " + ship + ".", MarkupText.markupWithKeywords(rule, "30251F"));
        assertEquals("Spend 2 " + train + " or 1 " + ship + ".", MarkupText.replaceColoredTicketKeywords(rule));
        assertEquals("1 " + getGlyph("Train Ticket") + " / 2 " + getGlyph("Ship Ticket"),
                MarkupText.replaceGlyphKeywords("1 Train Ticket / 2 Ship Tickets"));
        String prose = "Steamship tickets and train ticketing; ship and train routes; travel tickets.";
        assertEquals(prose, MarkupText.replaceGlyphKeywords(prose));
    }

    @Test public void localizedAndWrappedTicketPhrasesKeepTheirQuantities() {
        assertEquals("1 " + getGlyph("Train Ticket") + ", 2 " + getGlyph("Ship Ticket")
                        + ", 5 " + getGlyph("Train Ticket"),
                MarkupText.replaceGlyphKeywords("1 Vlakový lístok, 2 lodné lístky, 5 vlakových lístkov"));
        assertEquals("Gain one " + getGlyph("Ship Ticket"), MarkupText.replaceGlyphKeywords("Gain one Ship\nTicket"));
    }

    @Test
    public void replacesDiceResultsWithDiceGlyphs() {
        assertEquals("ACTION: Roll 1 die.\n" + getDiceGlyph(5) + " or " + getDiceGlyph(6) + " → gain 1 Item\n"
                        + getDiceGlyph(1) + " → discard this card.",
                MarkupText.replaceDiceResults("ACTION: Roll 1 die.\n5 or 6 → gain 1 Item\n1 → discard this card."));
        assertEquals("Roll 1 die: " + getDiceGlyph(4) + ", " + getDiceGlyph(5) + " or " + getDiceGlyph(6) + " → discard",
                MarkupText.replaceDiceResults("Roll 1 die: 4, 5 or 6 → discard"));
        assertEquals("RECKONING: Roll 1 die. " + getDiceGlyph(1) + " → Doom advances.",
                MarkupText.replaceDiceResults("RECKONING: Roll 1 die. 1 → Doom advances."));
        assertEquals("On a " + getDiceGlyph(1) + " or " + getDiceGlyph(2) + ":",
                MarkupText.replaceDiceResults("On a 1 or 2:"));
        assertEquals("each " + getDiceGlyph(6) + " counts as 2 successes.",
                MarkupText.replaceDiceResults("each 6 counts as 2 successes."));
        assertEquals("each " + getDiceGlyph(6) + " counts as\n2 successes and each " + getDiceGlyph(1) + "\nnegates",
                MarkupText.replaceDiceResults("each 6 counts as\n2 successes and each 1\nnegates"));
        assertEquals("Roll 1 die. On a " + getDiceGlyph(4) + ", " + getDiceGlyph(5) + " or " + getDiceGlyph(6) + ", gain 1 Clue.",
                MarkupText.replaceDiceResults("Roll 1 die. On a 4, 5 or 6, gain 1 Clue."));
        assertEquals("Your tests succeed only on " + getDiceGlyph(6) + ".",
                MarkupText.replaceDiceResults("Your tests succeed only on 6."));
        assertEquals("Your tests succeed on " + getDiceGlyph(4) + ", " + getDiceGlyph(5) + " or " + getDiceGlyph(6) + ".",
                MarkupText.replaceDiceResults("Your tests succeed on 4, 5 or 6."));
    }

    @Test
    public void keepsOtherNumbers() {
        String text = "Spend 2 Clues: gain 1 Health. +1 → test. 10 → nothing. Lose 1 Health for each 1 Clue. On a 2nd try";
        assertEquals(text, MarkupText.replaceDiceResults(text));
    }
}
