package sk.sivak.eldritchhorror.core.model;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import java8.features.util.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.clue.Clue;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.CluePoolSaveData;
import sk.sivak.eldritchhorror.core.model.save.CluePoolSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.ClueHelper;

import java.util.*;

import static java8.features.util.MapUtils.computeIfAbsent;
import static java8.features.util.MapUtils.getOrDefault;

/**
 * @author msivak
 */
public class CluePool implements CluePoolWrite {

    private static final Logger logger = LogManager.getLogger(CluePool.class);

    private List<Clue> cluePool;
    private Map<InvestigatorId, List<Clue>> investigatorCluesMap;
    private List<Clue> spawnedClues;

    @Override
    public void initCluePool() {
        logger.info("Initializing clue pool");
        cluePool = new LinkedList<>();
        investigatorCluesMap = new HashMap<>();
        spawnedClues = new LinkedList<>();
        new ClueHelper().initCluePool(cluePool);

        if (!GoogleServicesHolder.isTutorialPassed()) {
            cluePool.add(0, new Clue(LocationId.THE_PYRAMIDS));
            cluePool.add(1, new Clue(LocationId.SPACE_7));
        }
    }

    @Override
    public void gainClueFromPool(InvestigatorId investigatorId) {
        computeIfAbsent(investigatorCluesMap, investigatorId, k -> new LinkedList<>());

        Clue clue = cluePool.remove(0);
        investigatorCluesMap.get(investigatorId).add(clue);

        logger.info("Clue '" + clue.getSpawnLocationId() + "' added to investigator '" + investigatorId + "'");
    }

    @Override
    public boolean isCluePoolEmpty() {
        return cluePool.isEmpty();
    }

    @Override
    public LocationId gainClueFromSpace(InvestigatorId investigatorId, LocationId locationId) {
        computeIfAbsent(investigatorCluesMap, investigatorId, k -> new LinkedList<>());

        Clue clueAtLocation = Stream.findFirstOrException(spawnedClues, c -> c.getCurrentLocationId() == locationId);
        spawnedClues.remove(clueAtLocation);
        investigatorCluesMap.get(investigatorId).add(clueAtLocation);

        logger.info("Clue '" + clueAtLocation.getSpawnLocationId() + "' added to investigator '" + investigatorId + "'");
        return clueAtLocation.getSpawnLocationId();
    }

    @Override
    public void loseClue(InvestigatorId investigatorId) {
        Clue removedClue = investigatorCluesMap.get(investigatorId).remove(0);
        cluePool.add(removedClue);
        Collections.shuffle(cluePool);
        logger.info("Investigator '" + investigatorId + "' lost clue '" + removedClue.getSpawnLocationId() + "'.");
    }

    @Override
    public ClueInfo spawnClue() {
        if (cluePool.isEmpty()) {
            return null;
        }
        Clue clue = cluePool.remove(0);
        spawnedClues.add(clue);
        logger.info("Clue spawned at '" + clue.getSpawnLocationId() + "'");
        return clue;
    }

    @Override
    public ClueInfo spawnClue(LocationId locationId) {
        Clue clue = cluePool.remove(0);
        clue.setCurrentLocationId(locationId);
        spawnedClues.add(clue);
        logger.info("Clue '"+clue.getSpawnLocationId()+"' spawned at '" + clue.getCurrentLocationId() + "'");
        return clue;
    }

    @Override
    public LocationId discardClue(LocationId locationId) {
        Clue clueAtLocation = Stream.findFirstOrException(spawnedClues, c -> c.getCurrentLocationId() == locationId);
        spawnedClues.remove(clueAtLocation);
        cluePool.add(clueAtLocation);
        return clueAtLocation.getSpawnLocationId();
    }

    @Override
    public void updateClueLocation(LocationId spawnLocationId, LocationId currentLocationId) {
        Clue foundClue = Stream.findFirstOrException(spawnedClues, clue -> clue.getSpawnLocationId() == spawnLocationId);
        foundClue.setCurrentLocationId(currentLocationId);
    }

    @Override
    public int getClueCount(InvestigatorId investigatorId) {
        return getOrDefault(investigatorCluesMap, investigatorId, new LinkedList<>()).size();
    }

    @Override
    public boolean isClueAt(LocationId locationId) {
        return Stream.anyMatch(spawnedClues, clue -> clue.getCurrentLocationId() == locationId);
    }

    @Override
    public Set<LocationId> getClueLocations() {
        Set<LocationId> locationIds = new HashSet<>();
        for (Clue spawnedClue : spawnedClues) {
            locationIds.add(spawnedClue.getCurrentLocationId());
        }
        return locationIds;
    }

    @Override
    public List<ClueInfo> getSpawnedClues() {
        return new LinkedList<>(spawnedClues);
    }

    @Override
    public LocationId getClueCurrentLocationId(LocationId spawnLocationId) {
        return Stream.findFirstOrException(spawnedClues, clue -> clue.getSpawnLocationId() == spawnLocationId).getCurrentLocationId();
    }

    @Override
    public void addOnTop(LocationId locationId) {
        if (Stream.anyMatch(cluePool, clue -> clue.getSpawnLocationId().equals(locationId))) {
            Clue foundClue = Stream.findFirstOrException(cluePool, clue -> clue.getSpawnLocationId().equals(locationId));
            cluePool.remove(foundClue);
            cluePool.add(0, foundClue);
        } else {
            cluePool.add(0, new Clue(locationId));
        }
    }

    @Override
    public void tradeClues(InvestigatorId investigatorA, InvestigatorId investigatorB, int difference) {
        if (difference == 0) {
            return;
        }
        InvestigatorId giver = difference > 0 ? investigatorB : investigatorA;
        InvestigatorId taker = difference > 0 ? investigatorA : investigatorB;

        for (int i = 0; i < Math.abs(difference); i++) {
            Clue clue = investigatorCluesMap.get(giver).remove(0);
            MapUtils.computeIfAbsent(investigatorCluesMap, taker, investigatorId -> new LinkedList<>());
            investigatorCluesMap.get(taker).add(clue);
        }
    }

    @Override
    public CluePoolSaveData save() {
        CluePoolSaveData cluePoolSaveData = new CluePoolSaveData();
        cluePoolSaveData.setSpawnedClues(spawnedClues);

        HashMap<String, List<Clue>> saveDataMap = new HashMap<>();
        for (Map.Entry<InvestigatorId, List<Clue>> entry : investigatorCluesMap.entrySet()) {
            saveDataMap.put(entry.getKey().name(), entry.getValue());
        }

        cluePoolSaveData.setInvestigatorCluesMap(saveDataMap);
        return cluePoolSaveData;
    }

    @Override
    public void load(CluePoolSaveDataRead saveData) {
        initCluePool();
        spawnedClues = saveData.getSpawnedClues();
        for (Clue spawnedClue : spawnedClues) {
            IterableUtils.removeIf(cluePool, spawnedClue::equals);
        }
        for (Map.Entry<String, List<Clue>> entry : saveData.getInvestigatorCluesMap().entrySet()) {
            investigatorCluesMap.put(InvestigatorId.valueOf(entry.getKey()), entry.getValue());
            for (Clue clue : entry.getValue()) {
                IterableUtils.removeIf(cluePool, clue::equals);
            }

        }
    }
}
