package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard16 extends YellowMythosCard {

    public YellowMythosCard16() {
        setTitle("The Unbidden Recital");
        setFlavor("Every retained spell starts reciting itself inside its owner's thoughts. " +
                "The investigators can relinquish spells before the voices rise together. " +
                "Each one kept strains the mind, and anyone who loses sanity to the recital is left with a lasting disorder.");
        addInfo("Each investigator may discard any number of Spells, then:\n" +
                "-[#BAD]Loses 1 Sanity for each Spell he has[]\n" +
                "If he lost Sanity this way:\n" +
                "-[#BAD]Gains a Madness Condition[]");
    }
}
