package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

import java.util.List;

/**
 * @author msivak
 */
public class GateSpawnData {

    private GateInfo gate;
    private List<MonsterInfo> monsters;

    public GateInfo getGate() {
        return gate;
    }

    public void setGate(GateInfo gate) {
        this.gate = gate;
    }

    public List<MonsterInfo> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<MonsterInfo> monsters) {
        this.monsters = monsters;
    }
}
