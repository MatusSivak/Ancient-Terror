package sk.sivak.eldritchhorror.core.constants.location;

/**
 * @author msivak
 */
public enum LocationType {
    CITY("City"),
    SEA("Sea"),
    WILDERNESS("Wilderness");

    private String value;

    LocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
