package sk.sivak.eldritchhorror.core.view.assetmanager;

import com.badlogic.gdx.Gdx;
import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;

/** Resolves every monster surface to the same artwork, including asynchronous map spawns. */
public final class MonsterArtwork {
    private static Supplier<AncientOneInfo> ancientOne = () -> null;

    private MonsterArtwork() { }

    /** Read the current game lazily so new games, loaded games and awakening use current state. */
    public static void setAncientOneSupplier(Supplier<AncientOneInfo> supplier) {
        ancientOne = supplier == null ? () -> null : supplier;
    }

    public static String path(String monsterClassName, boolean epic) {
        String name = monsterClassName.substring(0, monsterClassName.length() - "Monster".length());
        if (!epic && "Cultist".equals(name)) {
            AncientOneInfo info = ancientOne.get();
            if (info != null && info.getAncientOneId() != null && Gdx.files != null) {
                String custom = "monster/cultist/" + info.getAncientOneId().name() + ".png";
                if (Gdx.files.internal(custom).exists()) return custom;
            }
        }
        return "monster/" + (epic ? "epic/" : "") + name + ".png";
    }
}
