package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class WraithMonster extends AbstractMonsterInfo {

    public WraithMonster() {
        super("Wraith",
                -1, 2, -1, 2, 3);
        setSpawnText("A random investigator gains Cursed.");
        setSpecialText("When defeated: an investigator may discard Cursed.");
    }
}
