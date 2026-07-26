package sk.sivak.eldritchhorror.core.constants.reference;

/**
 * @author msivak
 */
public class Reference implements ReferenceInfo {

    private int player;
    private int spawnGates;
    private int spawnClues;
    private int monsterSurge;

    public Reference(int player, int spawnGates, int spawnClues, int monsterSurge) {
        this.player = player;
        this.spawnGates = spawnGates;
        this.spawnClues = spawnClues;
        this.monsterSurge = monsterSurge;
    }

    @Override
    public int getPlayers() {
        return player;
    }

    @Override
    public int getSpawnGates() {
        return spawnGates;
    }

    @Override
    public int getSpawnClues() {
        return spawnClues;
    }

    @Override
    public int getMonsterSurge() {
        return monsterSurge;
    }
}
