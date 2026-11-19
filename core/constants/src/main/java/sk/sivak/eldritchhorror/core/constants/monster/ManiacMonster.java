package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class ManiacMonster extends AbstractMonsterInfo {

    public ManiacMonster() {
        super("Maniac",
                1, 1, -1, 2, 1);
        setSpecialText("Fail Strength: you may discard 1 Ally Asset instead of losing Health.\nDefeat in combat: gain 1 Axe Asset.");
    }
}
