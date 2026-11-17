package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard18 extends YellowMythosCard {

    public YellowMythosCard18() {
        setTitle("The Levy on Wonders");
        setFlavor("A lattice of pale cracks spreads between enchanted possessions. " +
                "To stop it consuming the collection, each investigator must surrender half their magical belongings, with any odd piece claimed as well. " +
                "Every artifact lost in this levy sends a greater surge outward, bringing the catastrophe closer.");
        addInfo("Each investigator:\n" +
                "-[#BAD]Discards half (rounded up) of his Magical possessions[]\n" +
                "Then:\n" +
                "-[#BAD]Doom advances by 1 for each Artifact discarded this way[]");
    }
}
