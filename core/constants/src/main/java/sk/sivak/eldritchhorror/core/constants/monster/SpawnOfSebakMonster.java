package sk.sivak.eldritchhorror.core.constants.monster;

public class SpawnOfSebakMonster extends AbstractMonsterInfo {

    public SpawnOfSebakMonster() {
        super("Spawn of Sebak",
                0, 2, 0, 4, 4);
        setSpecialText("Lose Health from Strength: gain 1 Injury per Health lost.");
        setReckoning(true);
        setReckoningText("Each investigator here loses 1 Sanity.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Spawn of\nSebak";
    }
}
