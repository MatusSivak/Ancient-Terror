package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard18 extends YellowMythosCard {

    public YellowMythosCard18() {
        setTitle("Shattered Magic");
        setFlavor("You believed you could harness the power of these mysterious treasures. You were wrong. " +
                "Now they lie in ashes, consumed by their own strange energies. In the end they were " +
                "nothing more than fuel to hasten the coming catastrophe.");
        addInfo("Each investigator:\n" +
                "-[#BAD]Discards half (rounded up) of his Magical possessions[]\n" +
                "Then:\n" +
                "-[#BAD]Doom advances by 1 for each Artifact discarded this way[]");
    }
}
