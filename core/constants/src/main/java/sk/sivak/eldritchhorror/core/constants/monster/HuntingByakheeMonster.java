package sk.sivak.eldritchhorror.core.constants.monster;

public class HuntingByakheeMonster extends AbstractMonsterInfo {

    public HuntingByakheeMonster() {
        super("Hunting Byakhee",
                -1, 2, -2, 1, 2);
        setSpawnText("When this Monster is spawned, move it to the nearest investigator. Then an investigator on this space becomes Delayed.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Hunting\nByakhee";
    }
}
