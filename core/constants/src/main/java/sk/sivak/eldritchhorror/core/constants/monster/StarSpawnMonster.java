package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class StarSpawnMonster extends AbstractMonsterInfo {

    public StarSpawnMonster() {
        super("Star Spawn",
                -1, 3, -3, 3, 5);
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, Doom advances.");
    }
}
