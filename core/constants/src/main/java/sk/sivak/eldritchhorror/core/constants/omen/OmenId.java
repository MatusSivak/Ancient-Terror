package sk.sivak.eldritchhorror.core.constants.omen;

/**
 * @author msivak
 */
public enum OmenId {
    NORTH,
    EAST,
    SOUTH,
    WEST;


    public OmenId next() {
        int x = (this.ordinal() + 1) % 4;
        return OmenId.values()[x];
    }
}
