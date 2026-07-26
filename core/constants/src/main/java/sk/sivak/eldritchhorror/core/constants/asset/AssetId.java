package sk.sivak.eldritchhorror.core.constants.asset;

import sk.sivak.eldritchhorror.core.constants.card.CardId;
import sk.sivak.eldritchhorror.core.constants.card.CardIdUtils;

/**
 * @author msivak
 */
public enum AssetId implements CardId {
    AGENCY_QUARANTINE,
    AGENT_OF_SECRETS,
    APPEAL_TO_THE_COUNCIL,
    ARCANE_BLADE,
    ARCANE_MANUSCRIPTS,
    ARCANE_TOME, // YES
    ARCANE_SCHOLAR, // YES
    AXE, // YES
    BANDAGES, // YES
    BANK_LOAN,
    BODYGUARD,
    BULL_WHIP,
    CARBINE_RIFLE,
    CAT_BURGLAR, // YES
    CHARTER_FLIGHT,
    CONSECRATION,
    CONSPIRACY_THEORIST,
    DELIVERY_SERVICE,
    DOT_18_DERRINGER,
    DOT_38_REVOLVER, // YES
    DOT_45_COLT_REVOLVER,
    DOT_45_AUTOMATIC,
    DOUBLE_BARRELED_SHOTGUN, // YES
    DYNAMITE,
    ELDER_SIGN,
    ENCHANTED_BLADE,
    EXPEDITION_GUIDE,
    EXPEDITION_MAP,
    FINE_CLOTHES,
    FISHING_NET,
    FRESH_FRUIT,
    GRUESOME_TALISMAN,
    HANDCUFFS, // YES
    HIRED_MUSCLE,
    HOLY_CROSS, // YES
    HOLY_SPEAR,
    HOLY_WATER, // YES
    INTELLIGENCE_REPORT,
    KEROSENE, //YES
    KING_JAMES_BIBLE,
    LANTERN,
    LAVISH_FEAST,
    LODGE_RESEARCHER, // YES
    LUCKY_CIGARETTE_CASE,
    LUCKY_RABBITS_FOOT,
    LUCKY_RING,
    MEDICAL_JOURNAL,
    MUSEUM_CURATOR, // TODO LATER gain relic unique asset
    MYSTERIOUS_TOME,
    MYSTIC_BOUNTY_HUNTER,
    OPUS_ARCANA,
    PERSONAL_ASSISTANT,
    POCKET_WATCH, // YES
    POLICE_ASSISTANCE,
    POLICE_LEDGER,
    PRIVATE_CARE,
    PRIVATE_INVESTIGATOR,
    PROTECTIVE_AMULET,
    PUZZLE_BOX, //YES
    RESEARCH_STUDENT,
    RITUAL_DAGGER,
    SANCTUARY, // YES
    SILVER_TWILIGHT_RITUAL,
    SPECIALIZED_TRAINING,
    SPIRIT_DAGGER,
    SYNDICATE_AGENT,
    TEAR_GAS,
    TOME_OF_SECRETS,
    URBAN_GUIDE,
    VATICAN_MISSIONARY,
    WHISKEY,
    WIRELESS_REPORT,
    WITCH_DOCTOR,
    BLUNDERBUSS,
    PROFANE_TOME,
    GAMBLERS_DICE,
    GENEALOGY_RESEARCH,
    ASTRONOMY_GUIDEBOOK,
    CULTISTS_JOURNAL,
    PATROLLING_THE_STREETS,
    NEWSPAPER_REPORT,
    MINERALOGY_RESEARCH,
    MAP_OF_THE_LEY_LINES,
    MONSTER_HUNTER,
    SEARCH_THE_ARCHIVES,
    SECRET_PAGE,
    TOME_OF_HORRORS,
    ;

    @Override
    public String asString() {
        return CardIdUtils.asString(this);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (AssetId assetId : AssetId.values()) {
            String assetName = assetId.getAssetListenerClassName();
            Class.forName(assetName).newInstance();
            System.out.println(assetName);
        }
    }

    public String getAssetClassName() {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.constants.asset.");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase() + s.substring(1));
        }
        stringBuilder.append("Asset");
        return stringBuilder.toString();
    }

    public String getAssetListenerClassName() {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.eventlistener.asset.");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase() + s.substring(1));
        }
        stringBuilder.append("Listener");
        return stringBuilder.toString();
    }
}
