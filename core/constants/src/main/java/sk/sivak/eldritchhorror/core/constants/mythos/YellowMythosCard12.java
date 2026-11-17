package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard12 extends YellowMythosCard {

    public YellowMythosCard12() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("Rooms with Lamps Still Lit");
        setFlavor("Messages arrive from a scattered network of safe houses. " +
                "Each investigator can call on one kind of help: treatment for injuries, a quiet refuge for a shaken mind, or a team to drive off a creature nearby. " +
                "The volunteers await a choice before committing their scarce resources.");
        addInfo("Each investigator may select one:\n" +
                "-[#GOOD]Recover 2 Health[]\n" +
                "-[#GOOD]Recover 2 Sanity[]\n" +
                "-[#GOOD]Discard 1 Monster from his space[]");
    }
}
