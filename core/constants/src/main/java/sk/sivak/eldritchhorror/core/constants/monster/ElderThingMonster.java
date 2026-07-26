package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class ElderThingMonster extends AbstractMonsterInfo {

    public ElderThingMonster() {
        super("Elder Thing",
                0, 3, -1, 2, 3);
        setSpawnText("Moves to Antarctica.");
    }
}
