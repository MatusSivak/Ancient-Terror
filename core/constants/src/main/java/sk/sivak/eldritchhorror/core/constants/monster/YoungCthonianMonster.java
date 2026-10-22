package sk.sivak.eldritchhorror.core.constants.monster;

public class YoungCthonianMonster extends AbstractMonsterInfo {

    public YoungCthonianMonster() {
        super("Young Cthonian",
                -1, 2, -1, 2, 3);
        setSpawnText("When this Monster is spawned, each investigator on this space or an adjacent space loses 1 Health and 1 Sanity.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Young\nCthonian";
    }
}
