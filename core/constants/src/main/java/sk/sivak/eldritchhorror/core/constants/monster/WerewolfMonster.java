package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class WerewolfMonster extends AbstractMonsterInfo {

    public WerewolfMonster() {
        super("Werewolf",
                0, 1, 0, 2, 2);
        setReckoning(true);
        setReckoningText("Move 1 space toward the nearest investigator. Then each investigator here loses 1 Health.");
        setSpecialText("Physical Resistance");
    }
}
