package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class ServitorOfTheOuterGodsMonster extends AbstractMonsterInfo {

    public ServitorOfTheOuterGodsMonster() {
        super("Servitor of the Outer Gods",
                -1, 3, 0, null, 3);
        setSpecialText("- Physical Resistance.\n" +
                "- Damage is equal to the number of Gates representing current Omen.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Servitor of\nthe Outer Gods";
    }
}
