package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class HoundOfTindalosMonster extends AbstractMonsterInfo {

    public HoundOfTindalosMonster() {
        super("Hound of Tindalos",
                0, 2, -1, 3, 3);
        setReckoning(true);
        setReckoningText("Move this Monster to the nearest investigator. " +
                "Then that investigator immediately encounters it.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Hound of\nTindalos";
    }
}
