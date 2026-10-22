package sk.sivak.eldritchhorror.core.constants.monster;

public class GhastMonster extends AbstractMonsterInfo {

    public GhastMonster() {
        super("Ghast",
                -1, 1, -1, 2, 2);
        setSpawnText("When this Monster is spawned, an investigator on this space immediately encounters it.");
    }
}
