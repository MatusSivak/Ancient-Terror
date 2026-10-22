package sk.sivak.eldritchhorror.core.constants.monster;

public class PoltergeistMonster extends AbstractMonsterInfo {

    public PoltergeistMonster() {
        super("Poltergeist",
                -1, 3, -1, 0, 3);
        setSpawnText("When this Monster is spawned, the Lead Investigator gains 1 Bane Condition.");
        setSpecialText("When this Monster is defeated, an investigator gains 1 Boon Condition.");
    }
}
