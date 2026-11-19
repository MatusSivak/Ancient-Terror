package sk.sivak.eldritchhorror.core.constants.monster;

public class HuntingByakheeMonster extends AbstractMonsterInfo {

    public HuntingByakheeMonster() {
        super("Hunting Byakhee",
                -1, 2, -2, 1, 2);
        setSpawnText("Move to the nearest investigator. Then an investigator here becomes Delayed.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Hunting\nByakhee";
    }
}
