package sk.sivak.eldritchhorror.core.constants.monster;

public class DholeMonster extends AbstractMonsterInfo {

    public DholeMonster() {
        super("Dhole",
                -1, 3, -2, 3, 5);
        setReckoning(true);
        setReckoningText("Move this Monster 1 space toward the nearest investigator. Then each investigator on this space or an adjacent space loses 1 Health and 1 Sanity.");
    }
}
