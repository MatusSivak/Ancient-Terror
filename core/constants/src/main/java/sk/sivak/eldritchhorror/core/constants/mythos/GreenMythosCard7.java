package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard7 extends GreenMythosCard {

    public GreenMythosCard7() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("Buying Information");
        setFlavor("\"When you get there, stop at this address.\" " +
                "The nervous little man hands you a business card for a restaurant. " +
                "\"Ask for the catch of the day and then tuck a sawbuck into your napkin. " +
                "These guys can get you the answers you need.\"\n" +
                "When you get the bill for your meal, it includes directions to a payphone. " +
                "It's already ringing by the time you find it. " +
                "You answer and explain your situation to the gruff voice on the other end of the line.");
        addInfo("The Lead Investigator tests Influence.\n" +
                "-[#GOOD]Gains a number of Clues\n" +
                "equal to his test result.[]");
    }
}
