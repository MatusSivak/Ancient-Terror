package sk.sivak.eldritchhorror.core.constants.monster;

public class ProtoShoggothMonster extends AbstractMonsterInfo {

    public ProtoShoggothMonster() {
        super("Proto-Shoggoth",
                0, 2, -1, 3, 3);
        setReckoning(true);
        setReckoningText("Move this Monster 1 space toward the nearest investigator. Then an investigator on this space immediately encounters this Monster.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Proto-\nShoggoth";
    }
}
