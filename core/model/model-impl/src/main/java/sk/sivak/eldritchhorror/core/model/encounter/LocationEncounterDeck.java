package sk.sivak.eldritchhorror.core.model.encounter;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.GeneralEncounterDeckWrite;
import sk.sivak.eldritchhorror.core.model.LocationEncounterDeckWrite;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LocationEncounterDeck implements LocationEncounterDeckWrite {
    private List<Integer> sanFranciscoEncounters;
    private List<Integer> buenosAiresEncounters;
    private List<Integer> arkhamEncounters;
    private List<Integer> londonEncounters;
    private List<Integer> romeEncounters;
    private List<Integer> istanbulEncounters;
    private List<Integer> shanghaiEncounters;
    private List<Integer> tokyoEncounters;
    private List<Integer> sydneyEncounters;

    private final static int TOTAL_CARDS = 16;

    @Override
    public void init() {
        sanFranciscoEncounters = new LinkedList<>();
        buenosAiresEncounters = new LinkedList<>();
        arkhamEncounters = new LinkedList<>();
        londonEncounters = new LinkedList<>();
        romeEncounters = new LinkedList<>();
        istanbulEncounters = new LinkedList<>();
        shanghaiEncounters = new LinkedList<>();
        tokyoEncounters = new LinkedList<>();
        sydneyEncounters = new LinkedList<>();
        List<List<Integer>> locationEncountersList = Arrays.asList(sanFranciscoEncounters, buenosAiresEncounters, arkhamEncounters,
                londonEncounters, romeEncounters, istanbulEncounters, shanghaiEncounters, tokyoEncounters, sydneyEncounters);
        for (int i = 1; i <= TOTAL_CARDS; i++) {
            for (List<Integer> locationEncounters : locationEncountersList) {
                locationEncounters.add(i);
            }
        }
        for (List<Integer> locationEncounters : locationEncountersList) {
            Collections.shuffle(locationEncounters);
        }

        if (!GoogleServicesHolder.isTutorialPassed()) {
            istanbulEncounters.add(0,4);
        }
    }

    @Override
    public Integer drawCard(String locationType) {
        switch (locationType) {
            case "San Francisco":
                return drawLocationEncounter(sanFranciscoEncounters);
            case "Buenos Aires":
                return drawLocationEncounter(buenosAiresEncounters);
            case "Arkham":
                return drawLocationEncounter(arkhamEncounters);
            case "London":
                return drawLocationEncounter(londonEncounters);
            case "Rome":
                return drawLocationEncounter(romeEncounters);
            case "Istanbul":
                return drawLocationEncounter(istanbulEncounters);
            case "Shanghai":
                return drawLocationEncounter(shanghaiEncounters);
            case "Tokyo":
                return drawLocationEncounter(tokyoEncounters);
            case "Sydney":
                return drawLocationEncounter(sydneyEncounters);
        }
        throw new IllegalArgumentException();
    }

    private Integer drawLocationEncounter(List<Integer> deck) {
        shuffleDeckIfEmpty(deck);
        return deck.remove(0);
    }

    private void shuffleDeckIfEmpty(List<Integer> deck) {
        if (deck.size() == 0) {
            for (int i = 1; i <= TOTAL_CARDS; i++) {
                deck.add(i);
            }
            Collections.shuffle(deck);
        }
    }
}
