package sk.sivak.eldritchhorror.core.constants.monster;

public class HonoredDeadMonster extends AbstractMonsterInfo {

    public HonoredDeadMonster() {
        super("Honored Dead",
                0, 2, -1, 3, 3);
        setSpawnText("When this Monster is spawned, move it to The Pyramids.");
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, the nearest investigator gains a Cursed Condition.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Honored\nDead";
    }
}
