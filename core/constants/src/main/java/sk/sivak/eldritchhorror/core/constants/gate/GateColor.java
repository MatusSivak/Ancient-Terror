package sk.sivak.eldritchhorror.core.constants.gate;

/**
 * @author msivak
 */
public enum GateColor {
    RED("Red"),
    GREEN("Green"),
    BLUE("Blue");


    private String prettyString;

    GateColor(String prettyString) {
        this.prettyString = prettyString;
    }

    public String getPrettyString() {
        return prettyString;
    }
}
