package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard13 extends YellowMythosCard {

    public YellowMythosCard13() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("Everyone Has a Price");
        setFlavor("He writes the price on a piece of paper and slides it across the table. It's more than you " +
                "can afford, but you don't have any other options, and this greedy lout knows it.");
        addInfo("Each investigator may:\n" +
                "-[#BAD]Gain a Debt Condition[] to [#GOOD]discard 1 Condition[]");
    }
}
