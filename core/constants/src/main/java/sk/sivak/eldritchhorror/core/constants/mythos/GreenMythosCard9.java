package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard9 extends GreenMythosCard {

    public GreenMythosCard9() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("Heart of Corruption");
        setFlavor("You compared the picture in the magazine to the drawing on the old, tattered map. " +
                "It was definitely a match, but what could be so valuable that it required so much secrecy, " +
                "and why was that particular point on the map marked with blood?");
        addInfo("The Lead Investigator:\n" +
                "-[#GOOD]Gains one Artifact[]\n" +
                "-Rolls one die. On a 1 or 2:\n" +
                "-[#BAD]Loses two Health[]\n" +
                "-[#BAD]Loses two Sanity[]\n");
    }
}
