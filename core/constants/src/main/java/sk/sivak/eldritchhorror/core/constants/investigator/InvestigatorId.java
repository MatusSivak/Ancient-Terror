package sk.sivak.eldritchhorror.core.constants.investigator;

/**
 * @author msivak
 */
public enum InvestigatorId {
    THE_BOUNTY_HUNTER,
    THE_SPY,
    THE_SAILOR,
    THE_ASTRONOMER,
    THE_ENTERTAINER,
    THE_SOLDIER,
    THE_REDEEMED_CULTIST,
    THE_EX_CONVICT,
    THE_EXPEDITION_LEADER,
    THE_MUSICIAN,
    THE_POLITICIAN,
    THE_PSYCHIC,
    THE_MARTIAL_ARTIST,
    THE_SHAMAN,
    THE_ROOKIE_COP,
    THE_ACTRESS,
    // In-App Purchase
    THE_BOOTLEGGER,
    THE_HANDYMAN,
    THE_VIOLINIST,
    THE_WAITRESS;

    @Override
    public String toString() {
        if (this == THE_EX_CONVICT) {
            return "Ex-Convict";
        }
        String[] words = name().split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            word = word.toLowerCase();
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1, word.length()).toLowerCase()).append(" ");
        }
        result = new StringBuilder(result.substring(0, result.length() - 1));
        return result.toString().replace("The ", "");
    }
}
