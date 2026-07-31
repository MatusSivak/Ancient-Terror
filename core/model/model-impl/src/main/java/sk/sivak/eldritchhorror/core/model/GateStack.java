package sk.sivak.eldritchhorror.core.model;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.gate.Gate;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.GateStackSaveData;
import sk.sivak.eldritchhorror.core.model.save.GateStackSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.GateHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class GateStack implements GateStackWrite {

    private static final Logger logger = LogManager.getLogger(GateStack.class);

    private List<Gate> gateList;
    private List<Gate> discardedGates;
    private List<Gate> spawnedGates;
    private List<Integer> gateCards;


    private final static int TOTAL_CARDS = 30;

    public void init() {
        gateCards = new LinkedList<>();
        for (int i = 1; i <= TOTAL_CARDS; i++) {
            gateCards.add(i);
        }
        Collections.shuffle(gateCards);

        if (!GoogleServicesHolder.isTutorialPassed()) {
            gateCards.add(0, 1);
        }
    }

    @Override
    public Integer drawGateEncounter() {
        shuffleDeckIfEmpty();
        return gateCards.remove(0);
    }

    private void shuffleDeckIfEmpty() {
        if (gateCards.size() == 0) {
            init();
        }
    }

    @Override
    public void initGateStack() {
        init();
        gateList = new LinkedList<>();
        discardedGates = new LinkedList<>();
        spawnedGates = new LinkedList<>();
        logger.info("Initializing gate stack");
        new GateHelper().initGateStack(gateList);

        if (!GoogleServicesHolder.isTutorialPassed()) {
            moveTutorialGateToTop(LocationId.ISTANBUL, GateColor.BLUE);
            moveTutorialGateToTop(LocationId.SYDNEY, GateColor.RED);
        }
    }

    private void moveTutorialGateToTop(LocationId locationId, GateColor gateColor) {
        Gate gate = Stream.findFirstOrException(gateList, x -> x.getLocationId() == locationId);
        gateList.remove(gate);
        gateList.add(0, new Gate(gateColor, locationId));
    }

    @Override
    public GateInfo spawnGate() {
        if (gateList.isEmpty()) {
            addDiscardedGatesToGateList();
        }
        if (gateList.isEmpty()) {
            return null;
        }
        Gate gate = gateList.remove(0);
        spawnedGates.add(gate);
        logger.info("Gate spawned at '" + gate.getLocationId() + "'");
        return gate;
    }

    @Override
    public GateColor closeGate(LocationId location) {
        Gate foundGate = Stream.findFirstOrException(spawnedGates, gate -> gate.getLocationId().equals(location));
        spawnedGates.remove(foundGate);
        discardedGates.add(foundGate);
        return foundGate.getGateColor();
    }

    @Override
    public void putGateToTop(GateInfo selectedGate) {
        Gate foundGate = Stream.findFirstOrException(gateList, gate -> gate.getLocationId() == selectedGate.getLocationId());
        gateList.remove(foundGate);
        gateList.add(0, foundGate);
    }

    @Override
    public void putGateToBottom(GateInfo selectedGate) {
        Gate foundGate = Stream.findFirstOrException(gateList, gate -> gate.getLocationId() == selectedGate.getLocationId());
        gateList.remove(foundGate);
        gateList.add(foundGate);
    }

    @Override
    public boolean isGateAtLocation(LocationId locationId) {
        return Stream.anyMatch(spawnedGates, gate -> gate.getLocationId() == locationId);
    }

    @Override
    public GateColor getGateColor(LocationId locationId) {
        return Stream.findFirstOrException(spawnedGates, gate -> gate.getLocationId() == locationId).getGateColor();
    }


    @Override
    public List<LocationId> getSpawnedGatesLocations() {
        List<LocationId> locationIds = new LinkedList<>();
        for (Gate spawnedGate : spawnedGates) {
            locationIds.add(spawnedGate.getLocationId());
        }
        return locationIds;
    }

    @Override
    public List<GateInfo> getSpawnedGates() {
        return new LinkedList<>(spawnedGates);
    }

    @Override
    public List<GateInfo> getTopTwoGates() {
        if (gateList.size() < 2) {
            addDiscardedGatesToGateList();
        }
        return Arrays.asList(gateList.get(0), gateList.get(1));
    }

    @Override
    public GateInfo getTopGate() {
        if (gateList.size() < 1) {
            addDiscardedGatesToGateList();
        }
        return gateList.get(0);
    }

    @Override
    public List<LocationId> getSpawnedGates(GateColor gateColor) {
        LinkedList<Gate> gatesOfGivenColor = new LinkedList<>(spawnedGates);
        IterableUtils.removeIf(gatesOfGivenColor, gate -> gate.getGateColor() != gateColor);

        List<LocationId> locationIds = new LinkedList<>();
        for (Gate gateOfGivenColor : gatesOfGivenColor) {
            locationIds.add(gateOfGivenColor.getLocationId());
        }
        return locationIds;
    }

    private void addDiscardedGatesToGateList() {
        gateList.addAll(discardedGates);
        discardedGates.clear();
        Collections.shuffle(gateList);
    }

    @Override
    public GateStackSaveData save() {
        GateStackSaveData gateStackSaveData = new GateStackSaveData();
        gateStackSaveData.setDiscardedGates(discardedGates);
        gateStackSaveData.setGateList(gateList);
        gateStackSaveData.setSpawnedGates(spawnedGates);
        return gateStackSaveData;
    }

    @Override
    public void load(GateStackSaveDataRead saveData) {
        init();
        discardedGates = saveData.getDiscardedGates();
        gateList = saveData.getGateList();
        spawnedGates = saveData.getSpawnedGates();
    }
}
