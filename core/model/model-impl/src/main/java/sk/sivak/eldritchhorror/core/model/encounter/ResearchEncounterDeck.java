package sk.sivak.eldritchhorror.core.model.encounter;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.ResearchEncounterDeckWrite;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResearchEncounterDeck implements ResearchEncounterDeckWrite {
    private List<Integer> cityEncounters = new LinkedList<>();
    private List<Integer> wildernessEncounters = new LinkedList<>();
    private List<Integer> seaEncounters = new LinkedList<>();

    private final Map<AncientOneId, Integer> cardCountMap = new HashMap<>();
    private AncientOneId ancientOneId;

    public ResearchEncounterDeck() {
        cardCountMap.put(AncientOneId.AZATHOTH, 24);
        cardCountMap.put(AncientOneId.CTHULHU, 18);
        cardCountMap.put(AncientOneId.SHUB_NIGGURATH, 16);
        cardCountMap.put(AncientOneId.YOG_SOTHOTH, 16);
    }


    @Override
    public void init(AncientOneId ancientOneId) {
        this.ancientOneId = ancientOneId;
        cityEncounters = new LinkedList<>();
        wildernessEncounters = new LinkedList<>();
        seaEncounters = new LinkedList<>();
        for (int i = 1; i <= cardCountMap.get(ancientOneId); i++) {
            cityEncounters.add(i);
            wildernessEncounters.add(i);
            seaEncounters.add(i);
        }
        Collections.shuffle(cityEncounters);
        Collections.shuffle(wildernessEncounters);
        Collections.shuffle(seaEncounters);

        if (!GoogleServicesHolder.isTutorialPassed()) {
            wildernessEncounters.add(0,8);
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
        return cityEncounters.remove(0);
    }

    private void shuffleDeckIfEmpty(List<Integer> deck) {
        if (deck.size() == 0) {
            for (int i = 1; i <= cardCountMap.get(ancientOneId); i++) {
                deck.add(i);
            }
            Collections.shuffle(deck);
        }
    }

    public Integer drawWildernessEncounter() {
        shuffleDeckIfEmpty(wildernessEncounters);
        return wildernessEncounters.remove(0);
    }

    public Integer drawSeaEncounter() {
        shuffleDeckIfEmpty(seaEncounters);
        return seaEncounters.remove(0);
    }
}
