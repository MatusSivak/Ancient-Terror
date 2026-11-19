package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class SkeletonMonster extends AbstractMonsterInfo {

    public SkeletonMonster() {
        super("Skeleton",
                0, 2, -2, 1, 2);
        setSpecialText("Defeat in combat: recover 1 Sanity.");
    }
}
