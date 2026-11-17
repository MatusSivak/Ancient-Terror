package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard13 extends YellowMythosCard {

    public YellowMythosCard13() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("The Deferred Invoice");
        setFlavor("An envelope lists a problem its recipient has told no one about. " +
                "Inside is an offer to have that burden removed now, with payment demanded later. " +
                "An investigator who accepts can leave one condition behind, but the signature becomes a debt.");
        addInfo("Each investigator may:\n" +
                "-[#BAD]Gain a Debt Condition[] to [#GOOD]discard 1 Condition[]");
    }
}
