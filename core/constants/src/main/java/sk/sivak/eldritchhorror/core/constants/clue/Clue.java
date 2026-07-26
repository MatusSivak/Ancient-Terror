package sk.sivak.eldritchhorror.core.constants.clue;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.Objects;

/**
 * @author msivak
 */
public class Clue implements ClueInfo {

    private LocationId spawnLocationId;
    private LocationId currentLocationId;

    public Clue(LocationId spawnLocationId) {
        this.spawnLocationId = spawnLocationId;
        this.currentLocationId = spawnLocationId;
    }

    // Just for the json
    public Clue() {
    }

    @Override
    public LocationId getSpawnLocationId() {
        return spawnLocationId;
    }

    @Override
    public LocationId getCurrentLocationId() {
        return currentLocationId;
    }

    public void setCurrentLocationId(LocationId currentLocationId) {
        this.currentLocationId = currentLocationId;
    }

    public void setSpawnLocationId(LocationId spawnLocationId) {
        this.spawnLocationId = spawnLocationId;
    }

    @Override
    public String toString() {
        return "" + spawnLocationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clue clue = (Clue) o;
        return getSpawnLocationId() == clue.getSpawnLocationId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpawnLocationId());
    }
}
