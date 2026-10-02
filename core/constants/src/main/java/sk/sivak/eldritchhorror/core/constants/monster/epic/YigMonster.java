package sk.sivak.eldritchhorror.core.constants.monster.epic;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
public class YigMonster extends AbstractMonsterInfo {
    public YigMonster() {
        super("Yig", -1, 3, -2, 4, null);
        setHorrorTestType(Stat.WILL);
        setEpic(true);
        setMonsterId(EpicMonsterId.YIG);
        setSpecialText("If the Will test causes you to lose Sanity, gain Cursed.");
    }
}
