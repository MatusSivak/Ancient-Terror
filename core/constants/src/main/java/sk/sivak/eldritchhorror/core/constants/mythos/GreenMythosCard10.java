package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard10 extends GreenMythosCard {

    public GreenMythosCard10() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("A Stitch in the Breach");
        setFlavor("For a moment, the edges of the open gates become visible as loose threads. " +
                "One can be pulled shut. " +
                "Choose a breach attuned to the current omen and the stitch holds cleanly; choose another, and the tension draws the catastrophe closer.");
        addInfo("[#GOOD]Choose one Gate and discard it[]\n" +
                "If the Gate does not represent current Omen:\n" +
                "-[#BAD]Doom advances[]");
    }
}
