package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard9 extends GreenMythosCard {

    public GreenMythosCard9() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("A Parcel without a Sender");
        setFlavor("A locked case arrives for one investigator, containing a relic wrapped in layers of lead foil. " +
                "The artifact is theirs to keep, but the seals are brittle. " +
                "If the last seal breaks during unpacking, the escaping force will wound both body and mind.");
        addInfo("{investigator}:\n" +
                "-[#GOOD]Gains one Artifact[]\n" +
                "-Rolls one die. On a 1 or 2:\n" +
                "-[#BAD]Loses two Health[]\n" +
                "-[#BAD]Loses two Sanity[]\n");
    }
}
