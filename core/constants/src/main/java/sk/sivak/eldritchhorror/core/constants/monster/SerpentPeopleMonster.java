package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class SerpentPeopleMonster extends AbstractMonsterInfo {

    public SerpentPeopleMonster() {
        super("Serpent People",
                0, 2, -1, 3, 2);
        setReckoning(true);
        setSpawnText("Moves to the Amazon");
        setReckoningText("Roll 1 die. On a 1 or 2, the nearest investigator moves 1 space toward this Monster.");
    }
}
