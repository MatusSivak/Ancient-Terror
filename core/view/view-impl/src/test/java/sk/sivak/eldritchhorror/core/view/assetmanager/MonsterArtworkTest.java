package sk.sivak.eldritchhorror.core.view.assetmanager;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.game.GameViewImpl;

import static org.junit.Assert.assertEquals;

public class MonsterArtworkTest {
    private Files previousFiles;
    private AncientOneId current;
    private final Set<String> available = new HashSet<>();

    @Before public void setup() {
        previousFiles = Gdx.files;
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                (proxy, method, args) -> {
                    if (!method.getName().equals("internal")) throw new AssertionError(method);
                    String path = (String) args[0];
                    return new FileHandle(path) {
                        @Override public boolean exists() { return available.contains(path); }
                    };
                });
        available.add("monster/cultist/YIG.png");
        GameController controller = (GameController) Proxy.newProxyInstance(
                GameController.class.getClassLoader(), new Class[]{GameController.class}, (proxy, method, args) -> {
                    if (!method.getName().equals("getAncientOneInfo")) throw new AssertionError(method);
                    if (current == null) return null;
                    return Proxy.newProxyInstance(AncientOneInfo.class.getClassLoader(), new Class[]{AncientOneInfo.class},
                            (info, getter, ignored) -> current);
                });
        new GameViewImpl().setController(controller);
    }

    @After public void cleanup() {
        MonsterArtwork.setAncientOneSupplier(null);
        Gdx.files = previousFiles;
    }

    @Test public void followsCurrentGameAfterLoadOrSwitchWithoutCachingPreviousAncientOne() {
        assertEquals("monster/Cultist.png", MonsterArtwork.path("CultistMonster", false));
        current = AncientOneId.YIG;
        assertEquals("monster/cultist/YIG.png", MonsterArtwork.path("CultistMonster", false));
        current = AncientOneId.CTHULHU;
        assertEquals("monster/Cultist.png", MonsterArtwork.path("CultistMonster", false));
        current = AncientOneId.YIG;
        assertEquals("monster/cultist/YIG.png", MonsterArtwork.path("CultistMonster", false));
    }

    @Test public void futureAncientOneCultistsOnlyNeedTheirNamedAsset() {
        for (AncientOneId id : AncientOneId.values()) {
            current = id;
            String custom = "monster/cultist/" + id.name() + ".png";
            available.add(custom);
            assertEquals(custom, MonsterArtwork.path("CultistMonster", false));
            available.remove(custom);
            assertEquals("monster/Cultist.png", MonsterArtwork.path("CultistMonster", false));
        }
    }

    @Test public void otherMonstersKeepTheirArtwork() {
        current = AncientOneId.YIG;
        assertEquals("monster/Skeleton.png", MonsterArtwork.path("SkeletonMonster", false));
        assertEquals("monster/epic/Yig.png", MonsterArtwork.path("YigMonster", true));
        assertEquals("monster/epic/Cultist.png", MonsterArtwork.path("CultistMonster", true));
    }

    @Test public void standaloneWidgetsAndUnavailableFilesUseGenericCultist() {
        current = AncientOneId.YIG;
        Gdx.files = null;
        assertEquals("monster/Cultist.png", MonsterArtwork.path("CultistMonster", false));
        MonsterArtwork.setAncientOneSupplier(null);
        assertEquals("monster/Cultist.png", MonsterArtwork.path("CultistMonster", false));
    }
}
