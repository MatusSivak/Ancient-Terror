package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class WraithMonster extends AbstractMonsterInfo {

    public WraithMonster() {
        super("Wraith",
                -1, 2, -1, 2, 3);
        setSpawnText("The Lead Investigator gains a Cursed Condition.");
        setSpecialText("When this Monster is defeated, an investigator may discard a Cursed Condition.");
    }
}
