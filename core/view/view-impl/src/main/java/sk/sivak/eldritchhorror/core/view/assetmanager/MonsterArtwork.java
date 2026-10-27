package sk.sivak.eldritchhorror.core.view.assetmanager;

import com.badlogic.gdx.Gdx;
import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;

/** Resolves every monster surface to the same artwork, including asynchronous map spawns. */
public final class MonsterArtwork {
    private static Supplier<AncientOneInfo> ancientOne = () -> null;

    private MonsterArtwork() { }

    /** Native headings of the bundled portraits. Frontal/symmetric art uses right as its baseline. */
    public static boolean facesRight(String monsterClassName, boolean epic) {
        switch (path(monsterClassName, epic)) {
            case "monster/Byakhee.png":
            case "monster/Cthonian.png":
            case "monster/GiantPenguin.png":
            case "monster/RatThing.png":
            case "monster/Warlock.png":
            case "monster/Werewolf.png":
            case "monster/YoungCthonian.png":
            case "monster/Zoog.png":
            case "monster/cultist/YIG.png":
                return false;
            default:
                return true;
        }
    }

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
