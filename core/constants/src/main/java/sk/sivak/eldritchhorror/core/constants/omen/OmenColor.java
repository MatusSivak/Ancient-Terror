package sk.sivak.eldritchhorror.core.constants.omen;

import sk.sivak.eldritchhorror.core.constants.gate.GateColor;

/**
 * @author msivak
 */
public enum OmenColor {
    GREEN("Green"),
    BLUE("Blue"),
    RED("Red");

    private String prettyString;

    OmenColor(String prettyString) {
        this.prettyString = prettyString;
    }

    public String getPrettyString() {
        return prettyString;
    }

    public GateColor toGateColor() {
        return GateColor.valueOf(this.name());
    }
}
