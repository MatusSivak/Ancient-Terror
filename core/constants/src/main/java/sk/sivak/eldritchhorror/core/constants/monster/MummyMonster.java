package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class MummyMonster extends AbstractMonsterInfo {

    public MummyMonster() {
        super("Mummy",
                0, 2, -2, 2, 3);
        setSpawnText("Move to the Pyramids.");
    }
}
