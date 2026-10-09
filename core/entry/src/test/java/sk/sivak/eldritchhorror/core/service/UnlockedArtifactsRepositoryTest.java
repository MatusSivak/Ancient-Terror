package sk.sivak.eldritchhorror.core.service;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Base64Coder;
import java.lang.reflect.Proxy;
import java.util.*;
import org.junit.*;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import static org.junit.Assert.*;

public class UnlockedArtifactsRepositoryTest {
    private Application original;
    private final Map<String, String> data = new HashMap<>();
    @Before public void setup() {
        original = Gdx.app;
        Preferences preferences = (Preferences) Proxy.newProxyInstance(Preferences.class.getClassLoader(), new Class[]{Preferences.class}, (p, m, a) -> {
            if (m.getName().equals("getString")) return data.getOrDefault((String) a[0], "");
            if (m.getName().equals("putString")) { data.put((String) a[0], (String) a[1]); return p; }
            if (m.getName().equals("flush")) return null;
            throw new AssertionError(m.getName());
        });
        Gdx.app = (Application) Proxy.newProxyInstance(Application.class.getClassLoader(), new Class[]{Application.class}, (p, m, a) -> preferences);
    }
    @After public void cleanup() { Gdx.app = original; }
    @Test public void oldProfilesAndImportedSavesKeepZanthuPermanentlyUnlocked() {
        String old = new String(Base64Coder.encode(new byte[]{(byte) ArtifactId.SERPENT_CROWN.ordinal()}));
        data.put("unlockedArtifacts", old);
        UnlockedArtifactsRepository repository = new UnlockedArtifactsRepository();
        assertTrue(repository.loadData().contains(ArtifactId.ZANTHU_TABLETS));
        assertTrue(repository.loadData().contains(ArtifactId.SERPENT_CROWN));
        data.put("unlockedArtifacts", old);
        assertTrue(repository.loadData().contains(ArtifactId.ZANTHU_TABLETS));
        repository.saveData(Collections.singletonList(ArtifactId.SERPENT_CROWN));
        assertTrue(new UnlockedArtifactsRepository().loadData().contains(ArtifactId.ZANTHU_TABLETS));
    }
    @Test public void freshProfilesIncludeZanthu() {
        assertTrue(new UnlockedArtifactsRepository().loadData().contains(ArtifactId.ZANTHU_TABLETS));
    }
}
