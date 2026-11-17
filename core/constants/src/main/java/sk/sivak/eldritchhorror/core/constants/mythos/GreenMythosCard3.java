package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard3 extends GreenMythosCard {

    public GreenMythosCard3() {
        setTitle("The Wound Between");
        setFlavor("A red thread appears on an investigator's wrist, its other end vanishing toward a creature somewhere in the world. " +
                "Pulling it can unravel the beast without a fight. " +
                "But the thread passes through living flesh, and the tougher the creature, the deeper it will cut.");
        addInfo("{investigator}:\n" +
                "-[#GOOD]Discards one Monster of his choice[]\n" +
                "-[#BAD]Loses Health equal to its toughness[]");
    }
}
