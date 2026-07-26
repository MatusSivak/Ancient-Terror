package sk.sivak.eldritchhorror.core.constants.ancientone;

/**
 * @author msivak
 */
public enum AncientOneId {
    AZATHOTH,
    CTHULHU,
    SHUB_NIGGURATH,
    YOG_SOTHOTH;

    public String toPrettyString() {
        if (this == AZATHOTH) {
            return "Azathoth";
        } else if (this == CTHULHU) {
            return "Cthulhu";
        } else if (this == SHUB_NIGGURATH){
            return "Shub-Niggurath";
        } else if (this == YOG_SOTHOTH){
            return "Yog-Sothoth";
        } else {
            return "Error";
        }
    }
}
