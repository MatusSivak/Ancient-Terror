package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;

import java.util.LinkedList;
import java.util.List;

public class MonsterCupSaveData implements SaveDataWrite.MonsterCupSaveDataWrite {
    private List<? extends MonsterSaveDataRead> monsters = new LinkedList<>();


    @Override
    public List<? extends MonsterSaveDataRead> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<? extends MonsterSaveDataRead> monsters) {
        this.monsters = monsters;
    }

    public static class MonsterSaveData implements MonsterSaveDataRead{

        private MonsterId monsterId;
        private LocationId currentLocationId;
        private int currentHealth;

        public MonsterSaveData(MonsterId monsterId, LocationId currentLocationId, int currentHealth) {
            this.monsterId = monsterId;
            this.currentLocationId = currentLocationId;
            this.currentHealth = currentHealth;
        }

        // just for the JSON
        public MonsterSaveData() {
        }

        public void setMonsterId(MonsterId monsterId) {
            this.monsterId = monsterId;
        }

        public void setCurrentLocationId(LocationId currentLocationId) {
            this.currentLocationId = currentLocationId;
        }

        public void setCurrentHealth(int currentHealth) {
            this.currentHealth = currentHealth;
        }

        @Override
        public MonsterId getMonsterId() {
            return monsterId;
        }

        @Override
        public LocationId getCurrentLocationId() {
            return currentLocationId;
        }

        @Override
        public int getCurrentHealth() {
            return currentHealth;
        }
    }
}