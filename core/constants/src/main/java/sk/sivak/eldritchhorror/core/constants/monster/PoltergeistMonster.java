package sk.sivak.eldritchhorror.core.constants.monster;

public class PoltergeistMonster extends AbstractMonsterInfo {

    public PoltergeistMonster() {
        super("Poltergeist",
                -1, 3, -1, 0, 3);
        setSpawnText("A random investigator gains 1 Bane.");
        setSpecialText("When defeated: an investigator gains 1 Boon.");
    }
}
