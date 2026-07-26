package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.card.CardId;
import sk.sivak.eldritchhorror.core.constants.card.CardIdUtils;

public enum ArtifactId implements CardId{
    BONE_PIPES,
    CULTES_DES_GOULES,
    CURSED_SPHERE,
    DE_VERMIS_MYSTERIIS,
    DRAGON_IDOL,
    ELIXIR_OF_LIFE,
    FLUTE_OF_THE_OUTER_GODS,
    GATE_BOX,
    GLASS_OF_MORTLAN,
    GROTESQUE_STATUE,
    KHOPESH_OF_THE_ABYSS,
    LIGHTNING_GUN,
    MASK_OF_THE_WATCHER,
    REQUIEM_PER_SHUGGAY,
    MI_GO_BRAIN_CASE,
    MILK_OF_SHUB_NIGGURATH,
    NECRONOMICON,
    PALLID_MASK,
    RUBY_OF_RLYEH,
    SATCHEL_OF_THE_VOID,
    SERPENT_CROWN,
    THE_SILVER_KEY,
    SWORD_OF_SAINT_JEROME,
    SWORD_OF_YHA_TALLA,
    TTKA_HALOT,
    ZANTHU_TABLETS,
    ALIEN_DEVICE,
    HYPERBOREAN_CRYSTAL,
    ELTDOWN_SHARDS,
    CRYSTAL_OF_THE_ELDER_THINGS,
    DHOL_CHANTS,
    PNAKOTIC_MANUSCRIPTS,
    LIVRE_D_IVON;

    @Override
    public String asString() {
        return CardIdUtils.asString(this);
    }

    public String getArtifactClassName() {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.constants.artifact.");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase() + s.substring(1));
        }
        stringBuilder.append("Artifact");
        return stringBuilder.toString();
    }

    public String getArtifactListenerClassName() {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.eventlistener.artifact.");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase() + s.substring(1));
        }
        stringBuilder.append("Listener");
        return stringBuilder.toString();
    }
}
