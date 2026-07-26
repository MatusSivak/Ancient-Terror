package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.card.CardIdUtils;

public enum NonEpicMonsterId implements MonsterId {
    WARLOCK,
    MUMMY,
    LLOIGOR,
    WEREWOLF,
    MI_GO,
    WRAITH,
    SHOGGOTH,
    CULTIST,
    BYAKHEE,
    COLOUR_OUT_OF_SPACE,
    DEEP_ONE,
    GHOUL,
    WITCH,
    SERVITOR_OF_THE_OUTER_GODS,
    GUG,
    GNOPH_KEH,
    STAR_VAMPIRE,
    HOUND_OF_TINDALOS,
    CTHONIAN,
    VAMPIRE,
    SKELETON,
    RIOT,
    NIGHT_GAUNT,
    STAR_SPAWN,
    GOAT_SPAWN,
    DARK_YOUNG,
    ELDER_THING,
    GHOST,
    MANIAC,
    ZOMBIE,
    SERPENT_PEOPLE;

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (NonEpicMonsterId nonEpicMonsterId : NonEpicMonsterId.values()) {
            String monsterName = nonEpicMonsterId.getMonsterClassName();
            Class.forName(monsterName).newInstance();
            System.out.println(monsterName);
        }
    }

    @Override
    public String asString() {
        return CardIdUtils.asString(this);
    }

    public String getMonsterClassName() {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.constants.monster.");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase() + s.substring(1));
        }
        stringBuilder.append("Monster");
        return stringBuilder.toString();
    }
}
