package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class MiGoMonster extends AbstractMonsterInfo {

    public MiGoMonster() {
        super("Mi-go",
                0, 1, -2, 2, 3);
        setReckoning(true);
        setReckoningText("Discard the nearest Clue and move this Monster to that space.");
        setSpecialText("If you defeat this Monster during a Combat Encounter, gain 1 Artifact.");
    }
}
