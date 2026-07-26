package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard10 extends GreenMythosCard {

    public GreenMythosCard10() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("That Which Consumes");
        setFlavor("In one night, the whole monstrous undertaking has disappeared. " +
                "Every occult sigil has been sanded from the wall and painted over. " +
                "Every name on your list has moved to a new city without notice. " +
                "You would like to believe that you managed to drive them out, " +
                "but you fear that it signifies something worse. " +
                "The dark goal they'd been pursuing is now accomplished.");
        addInfo("[#GOOD]Choose one Gate and discard it[]\n" +
                "If the Gate does not represent current Omen:\n" +
                "-[#BAD]Doom advances[]");
    }
}
