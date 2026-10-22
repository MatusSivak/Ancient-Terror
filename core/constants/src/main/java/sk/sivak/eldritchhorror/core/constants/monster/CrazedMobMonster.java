package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class CrazedMobMonster extends AbstractMonsterInfo {

    public CrazedMobMonster() {
        super("Crazed Mob",
                -1, 2, -3, 3, 3);
        setHorrorTestType(Stat.INFLUENCE);
        setSpecialText("If you pass the Influence test, defeat this Monster.");
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, advance Doom by 1.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Crazed\nMob";
    }
}
