package sk.sivak.eldritchhorror.core.constants.monster.epic;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
public class ChildrenOfYigMonster extends AbstractMonsterInfo {
    public ChildrenOfYigMonster() {
        super("Children of Yig", -1, 0, -2, 2, null);
        setHorrorTestType(Stat.OBSERVATION);
        setEpic(true);
        setMonsterId(EpicMonsterId.CHILDREN_OF_YIG);
        setSpecialText("If the Observation test fails, move to a random space and skip the Strength test.");
    }
}
