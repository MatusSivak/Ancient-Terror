package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard7 extends GreenMythosCard {

    public GreenMythosCard7() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("Voices Behind the Switchboard");
        setFlavor("A telephone operator connects one investigator to witnesses who have been too frightened to speak. " +
                "No one will volunteer a name or address without reassurance. " +
                "Every confidence the investigator earns opens another lead; an unconvincing appeal may leave the line silent.");
        addInfo("{investigator} tests Influence.\n" +
                "-[#GOOD]Gains a number of Clues\n" +
                "equal to his test result.[]");
    }
}
