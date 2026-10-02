package sk.sivak.eldritchhorror.core.constants.monster.epic;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
public class WingedSerpentMonster extends AbstractMonsterInfo {
    public WingedSerpentMonster() {
        super("Winged Serpent", -1, 2, -2, 4, null);
        setHorrorTestType(Stat.WILL);
        setEpic(true);
        setMonsterId(EpicMonsterId.WINGED_SERPENT);
        setSpecialText("If the Strength test causes you to lose Health, gain Poisoned.");
    }
}
