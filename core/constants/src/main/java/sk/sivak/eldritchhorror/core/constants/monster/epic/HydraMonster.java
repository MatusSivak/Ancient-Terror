package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class HydraMonster extends AbstractMonsterInfo{

    public HydraMonster() {
        super("Hydra", -1, 3, -2, 3, null);
        setReckoning(false);
        setEpic(true);
        setSpecialText("This Epic Monster cannot lose Health unless the active investigator has the Sword of Y'ha-Talla Artifact.");
        setMonsterId(EpicMonsterId.HYDRA);
    }
}
