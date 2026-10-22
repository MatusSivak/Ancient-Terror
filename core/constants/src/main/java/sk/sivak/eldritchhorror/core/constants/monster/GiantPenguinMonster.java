package sk.sivak.eldritchhorror.core.constants.monster;

public class GiantPenguinMonster extends AbstractMonsterInfo {

    public GiantPenguinMonster() {
        super("Giant Penguin",
                1, 1, -1, 2, 2);
        setSpawnText("When this Monster is spawned, move it to Antarctica.");
        setSpecialText("If you lose Health from the Strength test, gain 1 Injury Condition.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Giant\nPenguin";
    }
}
