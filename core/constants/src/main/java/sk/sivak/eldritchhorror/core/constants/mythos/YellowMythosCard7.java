package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard7 extends YellowMythosCard {

    public YellowMythosCard7() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("The Night Dispatch");
        setFlavor("A courier network has been collecting what the authorities refuse to catalogue. " +
                "Each investigator is offered one dispatch: a report worth following, practical assistance, or a recovered spell. " +
                "The couriers can spare only one of these gifts for each recipient before moving on.");
        addInfo("Each investigator selects one:\n" +
                "-[#GOOD]Gain one Clue[]\n" +
                "-[#GOOD]Gain one Asset[]\n" +
                "-[#GOOD]Gain one Spell[]");
    }
}
