package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;

import java.util.List;
import java.util.Map;

public interface MonsterCupSaveDataRead {
    List<? extends MonsterSaveDataRead> getMonsters();

    interface MonsterSaveDataRead{

        MonsterId getMonsterId();

        LocationId getCurrentLocationId();

        int getCurrentHealth();
    }
}
