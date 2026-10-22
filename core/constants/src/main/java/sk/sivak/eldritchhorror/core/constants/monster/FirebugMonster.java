package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class FirebugMonster extends AbstractMonsterInfo {

    public FirebugMonster() {
        super("Firebug",
                0, 1, 0, 2, 1);
        setHorrorTestType(Stat.INFLUENCE);
        setSpecialText("If you pass the Influence test, defeat this Monster.");
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, advance Doom by 1.");
    }
}
