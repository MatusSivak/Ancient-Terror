package sk.sivak.eldritchhorror.core.constants.investigator;

/**
 * @author msivak
 */
public enum Stat {
    LORE,
    INFLUENCE,
    OBSERVATION,
    STRENGTH,
    WILL;

    public String prettyString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
