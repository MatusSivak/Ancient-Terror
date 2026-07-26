package sk.sivak.eldritchhorror.core.constants.spell;

import sk.sivak.eldritchhorror.core.constants.card.CardId;
import sk.sivak.eldritchhorror.core.constants.card.CardIdUtils;

public enum SpellId implements CardId {
    VOICE_OF_RA,
    FEED_THE_MIND,
    PLUMB_THE_VOID,
    WITHER,
    SHRIVELING,
    MISTS_OF_RELEH,
    POISON_MIST,
    HEALING_WORDS,
    FLESH_WARD,
    CONJURATION,
    INSTILL_BRAVERY,
    BLESSING_OF_ISIS,
    CLAIRVOYANCE,
    BANISHMENT,
    STORM_OF_SPIRITS,
    ARCANE_INSIGHT,
    INTERVENE;

    @Override
    public String asString() {
        return CardIdUtils.asString(this);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (SpellId spellId : SpellId.values()) {
            String className = spellId.getSpellClassName();
            String backSpellClassName = spellId.getSpellBackClassName(3);
            Class.forName(className).newInstance();
            System.out.println(className);
            System.out.println(backSpellClassName);
        }
    }

    public String getSpellClassName() {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.constants.spell.")
                .append(name().replaceAll("_", "").toLowerCase())
                .append(".");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
        }
        stringBuilder.append("Spell");
        return stringBuilder.toString();
    }

    public String getSpellBackClassName(int suffix) {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.constants.spell.")
                .append(name().replaceAll("_", "").toLowerCase())
                .append(".");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
        }
        stringBuilder.append("SpellBack");
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }
}
