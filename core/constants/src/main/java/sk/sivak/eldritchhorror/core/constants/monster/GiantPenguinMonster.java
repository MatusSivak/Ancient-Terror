package sk.sivak.eldritchhorror.core.constants.monster;

public class GiantPenguinMonster extends AbstractMonsterInfo {

    public GiantPenguinMonster() {
        super("Giant Penguin",
                1, 1, -1, 2, 2);
        setSpawnText("Move to Antarctica.");
        setSpecialText("Lose Health from Strength: gain 1 Injury.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Giant\nPenguin";
    }
}
