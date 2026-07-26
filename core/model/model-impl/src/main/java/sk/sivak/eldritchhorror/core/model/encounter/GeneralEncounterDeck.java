package sk.sivak.eldritchhorror.core.model.encounter;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.GeneralEncounterDeckWrite;
import sk.sivak.eldritchhorror.core.model.save.GeneralEncounterDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GeneralEncounterDeck implements GeneralEncounterDeckWrite {
    private List<Integer> cityEncounters;
    private List<Integer> wildernessEncounters;
    private List<Integer> seaEncounters;

    private Integer topCityEncounter;
    private Integer topWildernessEncounter;
    private Integer topSeaEncounter;

    private final static int TOTAL_CARDS = 16;

    @Override
    public void init() {
        cityEncounters = new LinkedList<>();
        wildernessEncounters = new LinkedList<>();
        seaEncounters = new LinkedList<>();
        for (int i = 1; i <= TOTAL_CARDS; i++) {
            cityEncounters.add(i);
            wildernessEncounters.add(i);
            seaEncounters.add(i);
        }
        Collections.shuffle(cityEncounters);
        Collections.shuffle(wildernessEncounters);
        Collections.shuffle(seaEncounters);

        if (!GoogleServicesHolder.isTutorialPassed()) {
            cityEncounters.add(0,4);
        }
    }

    @Override
    public Integer drawCard(LocationType locationType) {
        switch (locationType) {
            case CITY:
                return drawCityEncounter();
            case SEA:
                return drawSeaEncounter();
            case WILDERNESS:
                return drawWildernessEncounter();
        }
        throw new IllegalArgumentException();
    }

    public Integer drawCityEncounter() {
        shuffleDeckIfEmpty(cityEncounters);
        Integer encounterId = cityEncounters.remove(0);
        topCityEncounter = encounterId;
        return encounterId;
    }

    private void shuffleDeckIfEmpty(List<Integer> deck) {
        if (deck.size() == 0) {
            for (int i = 1; i <= TOTAL_CARDS; i++) {
                deck.add(i);
            }
            Collections.shuffle(deck);
        }
    }

    public Integer drawWildernessEncounter() {
        shuffleDeckIfEmpty(wildernessEncounters);
        Integer encounterId = wildernessEncounters.remove(0);
        topWildernessEncounter = encounterId;
        return encounterId;
    }

    public Integer drawSeaEncounter() {
        shuffleDeckIfEmpty(seaEncounters);
        Integer encounterId = seaEncounters.remove(0);
        topSeaEncounter = encounterId;
        return encounterId;
    }


    @Override
    public SaveDataWrite.GeneralEncounterDeckSaveDataWrite save() {
        GeneralEncounterDeckSaveData save = new GeneralEncounterDeckSaveData();
        save.setTopCityEncounter(topCityEncounter);
        save.setTopSeaEncounter(topSeaEncounter);
        save.setTopWildernessEncounter(topWildernessEncounter);
        return save;
    }
}
