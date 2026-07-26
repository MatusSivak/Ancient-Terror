package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class ColourOutOfSpaceMonster extends AbstractMonsterInfo {

    public ColourOutOfSpaceMonster() {
        super("Colour out of Space",
                0, 2, 0, null, 2);
        setSpawnText("Moves to Tunguska");
        setSpecialText("After resolving the Will test roll 1 die. On a 5 or 6, defeat this Monster.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Colour out\nof Space";
    }
}
