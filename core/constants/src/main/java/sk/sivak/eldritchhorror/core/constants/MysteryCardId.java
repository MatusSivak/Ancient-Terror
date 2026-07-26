package sk.sivak.eldritchhorror.core.constants;

public interface MysteryCardId {

    enum Azathoth implements MysteryCardId {
        SEED_OF_THE_DAEMON_SULTAN,
        OMEN_OF_DEVASTATION,
        OCCULT_RESEARCH,
        THE_TRUE_NAME,
        THE_GREEN_FLAME,
        VOICE_OF_AZATHOTH
    }

    enum Cthulhu implements MysteryCardId {
        QUEEN_OF_THE_DEEP_ONES,
        THREATENING_SEAS,
        RLYEH_RISEN,
        THE_DEEP_ONES_ATTACK,
        WATCHING_THE_STARS,
        THE_STARS_ARE_RIGHT,
        RISEN_FROM_THE_SEA // FINAL

    }

    enum ShubNiggurath implements MysteryCardId {
        BLASPHEMY_OF_THE_BLACK_GOAT,
        HOUR_OF_THE_MOON_LENS,
        SPAWN_OF_THE_BLACK_GOAT,
        NATURE_OF_THE_ALL_MOTHER,
        RITUALS_IN_THE_WILD,
        HUNTING_THE_THOUSAND,
        BATTLE_IN_THE_WOODS // FINAL
    }

    enum YogSothoth implements MysteryCardId {
        SPAWN_OF_YOG_SOTHOTH,
        WHERE_THE_OLD_ONES_BROKE_THROUGH,
        THE_BEYOND_ONE,
        THE_STONE_CIRCLES,
        VOID_BETWEEN_WORLDS,
        ARCANE_UNDERSTANDING,
        THE_KEY_AND_THE_GATE
    }

}
