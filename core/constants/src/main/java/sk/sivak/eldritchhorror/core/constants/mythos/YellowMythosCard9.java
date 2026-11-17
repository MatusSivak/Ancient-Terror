package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard9 extends YellowMythosCard {

    public YellowMythosCard9() {
        setTitle("A Seat Kept Empty");
        setFlavor("One investigator receives a travel itinerary with a second seat already reserved. " +
                "In that seat waits someone who has seen enough to believe the warnings and refuses to stand aside. " +
                "The stranger offers their help and joins the investigation.");
        addInfo("{investigator}:\n" +
                "-[#GOOD]Gains one random Ally from the deck[]");
    }
}
