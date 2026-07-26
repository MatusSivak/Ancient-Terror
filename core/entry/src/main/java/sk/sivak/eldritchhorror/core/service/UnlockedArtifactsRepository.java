package sk.sivak.eldritchhorror.core.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UnlockedArtifactsRepository {


    private static final String UNLOCKED_ARTIFACTS = "unlockedArtifacts";
    private final Preferences preferences;

    private List<ArtifactId> unlockedArtifacts;

    public UnlockedArtifactsRepository() {
        preferences = Gdx.app.getPreferences("AncientTerror.xml");
        List<ArtifactId> defaultUnlockedArtifacts = getDefaultUnlockedArtifacts();
        if (preferences.getString(UNLOCKED_ARTIFACTS).isEmpty()) {
            saveData(defaultUnlockedArtifacts);
        } else {
            List<ArtifactId> unlockedArtifacts = loadData();
            List<ArtifactId> newUnlockedArtifacts = new LinkedList<>();

            for (ArtifactId defaultUnlockedArtifact : defaultUnlockedArtifacts) {
                if (!unlockedArtifacts.contains(defaultUnlockedArtifact)) {
                    newUnlockedArtifacts.add(defaultUnlockedArtifact);
                }
            }
            if (!newUnlockedArtifacts.isEmpty()) {
                unlockedArtifacts.addAll(newUnlockedArtifacts);
                saveData(unlockedArtifacts);
            }
        }

    }

    public void saveData(List<ArtifactId> unlockedArtifacts) {
        byte[] indexesOfUnlockedArtifacts = new byte[unlockedArtifacts.size()];
        for (int i = 0; i < unlockedArtifacts.size(); i++) {
            indexesOfUnlockedArtifacts[i] = (byte)unlockedArtifacts.get(i).ordinal();
        }
        preferences.putString(UNLOCKED_ARTIFACTS, new String(Base64Coder.encode(indexesOfUnlockedArtifacts)));
        preferences.flush();
        this.unlockedArtifacts = unlockedArtifacts;
    }

    public List<ArtifactId> getUnlockedArtifacts() {
        if (unlockedArtifacts == null) {
            loadData();
        }
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.LOCKED_COUNT, "Artifacts", String.valueOf(ArtifactId.values().length - unlockedArtifacts.size()));
        return unlockedArtifacts;
    }

    public List<ArtifactId> loadData() {
        List<ArtifactId> artifactIds = new LinkedList<>();
        ArtifactId[] values = ArtifactId.values();
        for (byte index : Base64Coder.decode(preferences.getString(UNLOCKED_ARTIFACTS))) {
            artifactIds.add(values[index]);
        }
        this.unlockedArtifacts = artifactIds;
        return artifactIds;
    }

    public List<ArtifactId> getDefaultUnlockedArtifacts() {
        return Arrays.asList(
                ArtifactId.CULTES_DES_GOULES,
                ArtifactId.CURSED_SPHERE,
                ArtifactId.DE_VERMIS_MYSTERIIS,
                ArtifactId.ELIXIR_OF_LIFE,
                ArtifactId.FLUTE_OF_THE_OUTER_GODS,
                ArtifactId.GROTESQUE_STATUE,
                ArtifactId.LIGHTNING_GUN,
                ArtifactId.REQUIEM_PER_SHUGGAY,
                ArtifactId.MI_GO_BRAIN_CASE,
                ArtifactId.MILK_OF_SHUB_NIGGURATH,
                ArtifactId.SWORD_OF_SAINT_JEROME,
                ArtifactId.NECRONOMICON,
                ArtifactId.PALLID_MASK,
                ArtifactId.RUBY_OF_RLYEH,
                ArtifactId.THE_SILVER_KEY,
                ArtifactId.SWORD_OF_YHA_TALLA,
                ArtifactId.TTKA_HALOT
        );
    }
}
