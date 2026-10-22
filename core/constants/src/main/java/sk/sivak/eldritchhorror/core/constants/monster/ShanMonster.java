package sk.sivak.eldritchhorror.core.constants.monster;

public class ShanMonster extends AbstractMonsterInfo {

    public ShanMonster() {
        super("Shan",
                -1, 2, 0, 1, 2);
        setSpecialText("Physical Resistance");
        setReckoning(true);
        setReckoningText("Discard the nearest Clue.");
    }
}
