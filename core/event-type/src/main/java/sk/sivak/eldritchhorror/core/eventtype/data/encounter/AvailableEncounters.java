package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import java8.features.function.Predicate;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

import java.util.LinkedList;
import java.util.List;

public class AvailableEncounters {
    private List<Encounter> encounters = new LinkedList<>();

    public void addEncounter(Encounter encounter) {
        encounters.add(encounter);
    }

    public List<Encounter> getEncounters() {
        return new LinkedList<>(encounters);
    }

    public void removeEncounters(Predicate<Encounter> predicate) {
        IterableUtils.removeIf(encounters, predicate);
    }

    @Override
    public String toString() {
        return "AvailableEncounters{" +
                "encounters=" + encounters +
                '}';
    }
}
