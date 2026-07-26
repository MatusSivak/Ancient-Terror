package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class CthonianMonster extends AbstractMonsterInfo {

    public CthonianMonster() {
        super("Chthonian",
                -1, 2, -2, 3, 4);
        setSpawnText("Moves to the Heart of Africa");
    }
}
