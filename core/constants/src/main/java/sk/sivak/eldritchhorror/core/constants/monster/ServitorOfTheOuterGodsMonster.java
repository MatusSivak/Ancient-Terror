package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class ServitorOfTheOuterGodsMonster extends AbstractMonsterInfo {

    public ServitorOfTheOuterGodsMonster() {
        super("Servitor of the Outer Gods",
                -1, 3, 0, null, 3);
        setSpecialText("Physical Resistance.\nDamage equals the Gates matching the current Omen.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Servitor of\nthe Outer Gods";
    }
}
