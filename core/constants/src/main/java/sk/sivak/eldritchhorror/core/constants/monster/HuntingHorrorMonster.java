package sk.sivak.eldritchhorror.core.constants.monster;

public class HuntingHorrorMonster extends AbstractMonsterInfo {

    public HuntingHorrorMonster() {
        super("Hunting Horror",
                -1, 3, 0, 2, 3);
        setReckoning(true);
        setReckoningText("Move to the nearest investigator. Then an investigator there immediately encounters it.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Hunting\nHorror";
    }
}
