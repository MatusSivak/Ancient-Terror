package sk.sivak.eldritchhorror.core.constants.monster;

public class ChildOfTheGoatMonster extends AbstractMonsterInfo {

    public ChildOfTheGoatMonster() {
        super("Child of the Goat",
                0, null, 0, 2, 1);
        setSpecialText("Magical Resistance");
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, advance Doom by 1.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Child of\nthe Goat";
    }
}
