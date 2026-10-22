package sk.sivak.eldritchhorror.core.constants.monster;

public class FireVampireMonster extends AbstractMonsterInfo {

    public FireVampireMonster() {
        super("Fire Vampire",
                -1, 2, 0, 3, 2);
        setSpecialText("Physical Resistance");
        setReckoning(true);
        setReckoningText("Move this Monster 2 spaces toward the nearest investigator. Then each investigator on this space loses 1 Health.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Fire\nVampire";
    }
}
