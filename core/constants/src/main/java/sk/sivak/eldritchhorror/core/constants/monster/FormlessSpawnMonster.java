package sk.sivak.eldritchhorror.core.constants.monster;

public class FormlessSpawnMonster extends AbstractMonsterInfo {

    public FormlessSpawnMonster() {
        super("Formless Spawn",
                -1, 2, -1, 2, 2);
        setSpecialText("Physical Resistance");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Formless\nSpawn";
    }
}
