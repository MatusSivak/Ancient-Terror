package sk.sivak.eldritchhorror.core.constants.monster;

public class SpawnOfSebakMonster extends AbstractMonsterInfo {

    public SpawnOfSebakMonster() {
        super("Spawn of Sebak",
                0, 2, 0, 4, 4);
        setSpecialText("Gain 1 Injury Condition for each Health you lose from the Strength test.");
        setReckoning(true);
        setReckoningText("Each investigator on this space loses 1 Sanity.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Spawn of\nSebak";
    }
}
