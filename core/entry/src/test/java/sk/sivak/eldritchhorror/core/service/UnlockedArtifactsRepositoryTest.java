package sk.sivak.eldritchhorror.core.service;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Base64Coder;
import java.lang.reflect.Proxy;
import java.util.*;
import org.junit.*;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.ArtifactsDeck;
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

    @Test public void freshProfilesKeepPremiumCardsOutOfTheDeck() {
        List<ArtifactId> artifacts = new UnlockedArtifactsRepository().loadData();
        assertTrue(artifacts.size() < ArtifactId.values().length);
        assertFalse(artifacts.contains(ArtifactId.BONE_PIPES));
        List<sk.sivak.eldritchhorror.core.constants.asset.AssetId> assets = new UnlockedAssetsRepository().loadData();
        assertTrue(assets.size() < sk.sivak.eldritchhorror.core.constants.asset.AssetId.values().length);
    }

    @Test public void freshProfilesIncludeTheMysteryRequiredSerpentCrown() {
        assertTrue(new UnlockedArtifactsRepository().loadData().contains(ArtifactId.SERPENT_CROWN));
    }

    @Test public void legacyProfilesAndImportedSavesKeepSerpentCrownAvailable() {
        String old = new String(Base64Coder.encode(new byte[]{(byte) ArtifactId.BONE_PIPES.ordinal()}));
        data.put("unlockedArtifacts", old);
        UnlockedArtifactsRepository repository = new UnlockedArtifactsRepository();
        assertTrue(repository.loadData().contains(ArtifactId.SERPENT_CROWN));
        assertTrue(repository.loadData().contains(ArtifactId.BONE_PIPES));
        // Imported saves replace the stored unlock list, then load it again.
        data.put("unlockedArtifacts", old);
        assertTrue(repository.loadData().contains(ArtifactId.SERPENT_CROWN));
        repository.saveData(Collections.singletonList(ArtifactId.BONE_PIPES));
        List<ArtifactId> restored = new UnlockedArtifactsRepository().loadData();
        assertEquals(1, Collections.frequency(restored, ArtifactId.SERPENT_CROWN));
        assertTrue(restored.contains(ArtifactId.BONE_PIPES));
    }

    @Test public void crownStaysAvailableAfterDeckReloadWithoutDuplicatingOwnedOrDiscardedCard() {
        UnlockedArtifactsRepository repository = new UnlockedArtifactsRepository();
        ArtifactsDeck deck = new ArtifactsDeck();
        deck.setUnlockedArtifactsSupplier(repository::loadData);
        deck.createDeck();
        assertTrue(deck.isInDeckOrDiscardPile(ArtifactId.SERPENT_CROWN));
        deck.load(deck.save());
        assertTrue(deck.isInDeckOrDiscardPile(ArtifactId.SERPENT_CROWN));
        ArtifactInfo crown = deck.removeFromDeck(ArtifactId.SERPENT_CROWN);
        deck.addToInvestigator(InvestigatorId.THE_SAILOR, crown);
        deck.load(deck.save());
        assertTrue(deck.hasArtifact(InvestigatorId.THE_SAILOR, ArtifactId.SERPENT_CROWN));
        assertFalse(deck.isInDeckOrDiscardPile(ArtifactId.SERPENT_CROWN));
        deck.discard(InvestigatorId.THE_SAILOR, deck.getArtifacts(InvestigatorId.THE_SAILOR).get(0));
        deck.load(deck.save());
        assertTrue(deck.isInDeckOrDiscardPile(ArtifactId.SERPENT_CROWN));
        assertEquals(1, deck.getDiscardPile().size());
        assertNull(deck.removeFromDeck(ArtifactId.SERPENT_CROWN));
    }
}
