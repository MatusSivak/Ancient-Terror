package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard7 extends YellowMythosCard {

    public YellowMythosCard7() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("Silver Twilight Aid");
        setFlavor("Once inside the lodge, your host invites you to follow him upstairs. " +
                "Several members stare in astonishment. Outsiders are rarely granted such access. " +
                "At the top of the stairs you are ushered into a library, filled with rare and exotic tomes. " +
                "\"Now, \" he says, \"how can we help you?\"");
        addInfo("Each investigator selects one:\n" +
                "-[#GOOD]Gain one Clue[]\n" +
                "-[#GOOD]Gain one Asset[]\n" +
                "-[#GOOD]Gain one Spell[]");
    }
}
