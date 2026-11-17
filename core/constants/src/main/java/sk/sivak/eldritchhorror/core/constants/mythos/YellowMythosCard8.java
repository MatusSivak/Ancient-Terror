package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard8 extends YellowMythosCard {

    public YellowMythosCard8() {
        setTitle("The Weapons Register");
        setFlavor("City officials begin checking every armed stranger against a hastily compiled register. " +
                "An investigator carrying a weapon must persuade the officers that their work is legitimate. " +
                "If the explanation fails, a weapon is confiscated and the investigator is detained.");
        addInfo("Each investigator on a City space with a Weapon\n" +
                "tests Influence. If he fails:\n" +
                "-[#BAD]He discards one Weapon[]\n" +
                "-[#BAD]He gains a Detained Condition[]\n");
    }
}
