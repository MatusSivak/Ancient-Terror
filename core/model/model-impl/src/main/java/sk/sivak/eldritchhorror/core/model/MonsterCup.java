package sk.sivak.eldritchhorror.core.model;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import java8.features.util.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.CultistMonster;
import sk.sivak.eldritchhorror.core.constants.monster.ManiacMonster;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.MonsterCupSaveData;
import sk.sivak.eldritchhorror.core.model.save.MonsterCupSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.MonsterCupHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java8.features.util.MapUtils.computeIfAbsent;

/**
 * @author msivak
 */
public class MonsterCup implements MonsterCupWrite {

    private static final Logger logger = LogManager.getLogger(MonsterCup.class);

    private List<AbstractMonsterInfo> monsterCup;
    private Map<MonsterId, Integer> removedFromCupMap;
    private List<AbstractMonsterInfo> epicMonsterCup;
    private Map<LocationId, List<MonsterInfo>> locationMonstersMap;

    public void createMonsterCup() {
        monsterCup = new MonsterCupHelper().createMonsterCup();
        epicMonsterCup = new MonsterCupHelper().createEpicMonsterCup();
        locationMonstersMap = new HashMap<>();
        removedFromCupMap = new HashMap<>();

        if (!GoogleServicesHolder.isTutorialPassed()) {
            AbstractMonsterInfo m1 = new ManiacMonster();
            m1.setMonsterId(NonEpicMonsterId.MANIAC);
            monsterCup.add(0, m1);
        }
    }

    @Override
    public MonsterInfo spawnMonster(LocationId locationId, boolean overrideSpawnLocation) {

        AbstractMonsterInfo monster = monsterCup.remove(0);

        monster.setAlive(true);
        computeIfAbsent(locationMonstersMap, locationId, k -> new LinkedList<>());
        locationMonstersMap.get(locationId).add(monster);
        monster.setCurrentLocation(locationId);
        monster.setCurrentHealth(monster.getToughness());

        logger.info("Monster '" + monster.getName() + "' spawned at '" + locationId + "'");
        return monster;
    }

    @Override
    public MonsterInfo spawnMonster(MonsterId monsterId, LocationId locationId, boolean overrideSpawnLocation) {
        AbstractMonsterInfo foundMonster;
        if (Stream.anyMatch(monsterCup, monsterInfo -> monsterInfo.getMonsterId() == monsterId)) {
            foundMonster = Stream.findFirstOrException(monsterCup, monsterInfo -> monsterInfo.getMonsterId() == monsterId);
            monsterCup.remove(foundMonster);
        } else if (Stream.anyMatch(epicMonsterCup, monsterInfo -> monsterInfo.getMonsterId() == monsterId)) {
            foundMonster = Stream.findFirstOrException(epicMonsterCup, monsterInfo -> monsterInfo.getMonsterId() == monsterId);
            epicMonsterCup.remove(foundMonster);
        } else if (MapUtils.getOrDefault(removedFromCupMap, monsterId, 0) > 0){
            try {
                foundMonster = (AbstractMonsterInfo) Class.forName(((NonEpicMonsterId)monsterId).getMonsterClassName()).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException(monsterId.toString());
            }
            removedFromCupMap.put(monsterId, removedFromCupMap.get(monsterId) - 1);
            foundMonster.setMonsterId(monsterId);
        } else if (MapUtils.getOrDefault(removedFromCupMap, monsterId, 0) == 0) {
            return null;
        } else {
            throw new IllegalArgumentException("Monster '" + monsterId + "' not found in any cup.");
        }

        foundMonster.setAlive(true);
        computeIfAbsent(locationMonstersMap, locationId, k -> new LinkedList<>());
        locationMonstersMap.get(locationId).add(foundMonster);
        foundMonster.setCurrentLocation(locationId);
        foundMonster.setCurrentHealth(foundMonster.getToughness());

        logger.info("Monster '" + foundMonster.getName() + "' spawned at '" + locationId + "'");
        return foundMonster;
    }

    @Override
    public boolean canSpawn(NonEpicMonsterId monsterId) {
        if (Stream.anyMatch(monsterCup, monsterInfo -> monsterInfo.getMonsterId() == monsterId)) {
            return true;
        } else return MapUtils.getOrDefault(removedFromCupMap, monsterId, 0) > 0;
    }

    @Override
    public boolean containsMonster(LocationId locationId) {
        return (locationMonstersMap.get(locationId) != null && !locationMonstersMap.get(locationId).isEmpty());
    }

    @Override
    public boolean containsMonster(LocationId locationId, MonsterId monsterId) {
        if (locationMonstersMap.get(locationId) == null) {
            return false;
        }
        if (locationMonstersMap.get(locationId).isEmpty()) {
            return false;
        }
        return Stream.anyMatch(locationMonstersMap.get(locationId),
                monsterInfo -> monsterInfo.getMonsterId() == monsterId);
    }

    @Override
    public void loseHealth(MonsterInfo monsterInfo, int amount) {
        ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(monsterInfo.getCurrentHealth() - amount);
    }

    @Override
    public void discardMonster(MonsterInfo monsterInfo) {
        defeatMonster(monsterInfo);
    }

    @Override
    public void defeatMonster(MonsterInfo monsterInfo) {
        AbstractMonsterInfo abstractMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        abstractMonsterInfo.setCurrentHealth(abstractMonsterInfo.getToughness());
        abstractMonsterInfo.generateUuid();
        abstractMonsterInfo.setAlive(false);
        MapUtils.getOrDefault(locationMonstersMap, monsterInfo.getCurrentLocation(), new LinkedList<>()).remove(monsterInfo);
        abstractMonsterInfo.setCurrentLocation(null);
        if (monsterInfo.isEpic()) {
            epicMonsterCup.add(abstractMonsterInfo);
        } else {
            monsterCup.add(abstractMonsterInfo);
            Collections.shuffle(monsterCup);
        }

        if (removedFromCupMap.containsKey(monsterInfo.getMonsterId())) {
            IterableUtils.removeIf(monsterCup, mi -> mi == monsterInfo);
            removedFromCupMap.put(monsterInfo.getMonsterId(), removedFromCupMap.get(monsterInfo.getMonsterId()) + 1);
        }
    }

    @Override
    public List<MonsterInfo> getMonstersAtLocation(LocationId locationId) {
        return new LinkedList<>(MapUtils.getOrDefault(locationMonstersMap,locationId,new LinkedList<>()));
    }

    @Override
    public List<MonsterInfo> getMonsters() {
        List<MonsterInfo> result = new LinkedList<>();
        Collection<List<MonsterInfo>> collection = locationMonstersMap.values();
        for (List<MonsterInfo> list : collection) {
            result.addAll(list);
        }
        return result;
    }

    @Override
    public MonsterInfo initAmbushingMonster(MonsterId monsterId, LocationId locationId) {
        AbstractMonsterInfo monsterInfo;
        MonsterId monsterIdToUse;
        if (monsterId != null) {
            monsterIdToUse = monsterId;
        } else {
            monsterIdToUse = monsterCup.get(0).getMonsterId();
        }

        try {
            monsterInfo = (AbstractMonsterInfo) Class.forName(((NonEpicMonsterId) monsterIdToUse).getMonsterClassName()).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(monsterIdToUse.toString());
        }
        monsterInfo.setMonsterId(monsterIdToUse);
        monsterInfo.setAlive(true);
        monsterInfo.setCurrentLocation(locationId);
        return monsterInfo;
    }

    @Override
    public void returnAmbushingMonster(MonsterInfo monsterInfo) {
        IterableUtils.removeIf(monsterCup, mi -> mi == monsterInfo);
    }

    @Override
    public void updateMonsterLocation(MonsterInfo monsterInfo, LocationId targetLocationId) {
        locationMonstersMap.get(monsterInfo.getCurrentLocation()).remove(monsterInfo);
        ((AbstractMonsterInfo) monsterInfo).setCurrentLocation(targetLocationId);
        List<MonsterInfo> monstersOnThisLocation = MapUtils.getOrDefault(locationMonstersMap, monsterInfo.getCurrentLocation(), new LinkedList<>());
        monstersOnThisLocation.add(monsterInfo);
        locationMonstersMap.put(targetLocationId, monstersOnThisLocation);

    }

    @Override
    public boolean isEpicMonsterPresent(EpicMonsterId epicMonsterId) {
        return Stream.anyMatch(epicMonsterCup, monsterInfo -> monsterInfo.getMonsterId() == epicMonsterId);
    }

    @Override
    public List<AbstractMonsterInfo> getMonstersInCup() {
        return monsterCup;
    }

    @Override
    public void removeFromCup(List<MonsterId> removedMonsters) {
        IterableUtils.removeIf(monsterCup, monsterInfo -> removedMonsters.contains(monsterInfo.getMonsterId()));
        for (MonsterId removedMonster : removedMonsters) {
            removedFromCupMap.put(removedMonster, MapUtils.getOrDefault(removedFromCupMap, removedMonster, 0) + 1);
        }
    }

    @Override
    public MonsterCupSaveData save() {
        MonsterCupSaveData monsterCupSaveData = new MonsterCupSaveData();
        LinkedList<MonsterCupSaveDataRead.MonsterSaveDataRead> monsters = new LinkedList<>();

        for (List<MonsterInfo> monsterInfos : locationMonstersMap.values()) {
            for (MonsterInfo monsterInfo : monsterInfos) {
                MonsterCupSaveData.MonsterSaveData monsterSaveData = new MonsterCupSaveData.MonsterSaveData();
                monsterSaveData.setCurrentHealth(monsterInfo.getCurrentHealth());
                monsterSaveData.setCurrentLocationId(monsterInfo.getCurrentLocation());
                monsterSaveData.setMonsterId(monsterInfo.getMonsterId());
                monsters.add(monsterSaveData);
            }
        }
        monsterCupSaveData.setMonsters(monsters);
        return monsterCupSaveData;
    }

    @Override
    public void load(MonsterCupSaveDataRead saveData) {
        for (MonsterCupSaveDataRead.MonsterSaveDataRead monsterSaveDataRead : saveData.getMonsters()) {
            MonsterInfo monsterInfo = spawnMonster(monsterSaveDataRead.getMonsterId(), monsterSaveDataRead.getCurrentLocationId(), true);
            ((AbstractMonsterInfo)monsterInfo).setCurrentHealth(monsterSaveDataRead.getCurrentHealth());
        }
    }
}
