package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class GoatSpawnMonster extends AbstractMonsterInfo {

    public GoatSpawnMonster() {
        super("Goat Spawn",
                0, 1, -2, 2, 2);
        setSpecialText("Defeat in combat: you may gain Dark Pact to discard 1 Monster from any space.");
    }
}
