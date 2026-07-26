package sk.sivak.eldritchhorror.core.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UnlockedAssetsRepository  {


    private static final String UNLOCKED_ASSETS = "unlockedAssets";
    private final Preferences preferences;

    private List<AssetId> unlockedAssets;

    public UnlockedAssetsRepository() {
        preferences = Gdx.app.getPreferences("AncientTerror.xml");
        List<AssetId> defaultUnlockedAssets = getDefaultUnlockedAssets();
        if (preferences.getString(UNLOCKED_ASSETS).isEmpty()) {
            saveData(defaultUnlockedAssets);
        } else {
            List<AssetId> unlockedAssets = loadData();
            List<AssetId> newUnlockedAssets = new LinkedList<>();

            for (AssetId defaultUnlockedAsset : defaultUnlockedAssets) {
                if (!unlockedAssets.contains(defaultUnlockedAsset)) {
                    newUnlockedAssets.add(defaultUnlockedAsset);
                }
            }
            if (!newUnlockedAssets.isEmpty()) {
                unlockedAssets.addAll(newUnlockedAssets);
                saveData(unlockedAssets);
            }
        }

    }

    public void saveData(List<AssetId> unlockedAssets) {
        StringBuilder sb = new StringBuilder("NEW:");
        for (int i = 0; i < unlockedAssets.size(); i++) {
            sb.append(unlockedAssets.get(i).ordinal()).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        preferences.putString(UNLOCKED_ASSETS, sb.toString());
        preferences.flush();
        this.unlockedAssets = unlockedAssets;
    }

    public List<AssetId> getUnlockedAssets() {
        if (unlockedAssets == null) {
            loadData();
        }
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.LOCKED_COUNT, "Assets", String.valueOf(AssetId.values().length - unlockedAssets.size()));
        return unlockedAssets;
    }

    public List<AssetId> loadData() {
        String unlockedAssets = preferences.getString(UNLOCKED_ASSETS);
        List<AssetId> assetIds = new LinkedList<>();
        AssetId[] values = AssetId.values();

        if (unlockedAssets.startsWith("NEW:")) {
            unlockedAssets = unlockedAssets.substring(4);
            for (String oneAsset : unlockedAssets.split(",")) {
                assetIds.add(values[Integer.parseInt(oneAsset)]);
            }
            this.unlockedAssets = assetIds;
            return assetIds;
        }

        for (byte index : Base64Coder.decode(unlockedAssets)) {
            assetIds.add(values[index]);
        }
        this.unlockedAssets = assetIds;
        return assetIds;
    }

    public static List<AssetId> getDefaultUnlockedAssets() {
        return Arrays.asList(
                AssetId.BANK_LOAN,
                AssetId.ARCANE_MANUSCRIPTS,
                AssetId.BANDAGES,
                AssetId.BULL_WHIP,
                AssetId.CAT_BURGLAR,
                AssetId.CHARTER_FLIGHT,
                AssetId.DELIVERY_SERVICE,
                AssetId.DOT_18_DERRINGER,
                AssetId.DOT_38_REVOLVER,
                AssetId.KEROSENE,
                AssetId.LUCKY_RABBITS_FOOT,
                AssetId.POCKET_WATCH,
                AssetId.PROTECTIVE_AMULET,
                AssetId.WHISKEY,
                AssetId.WIRELESS_REPORT,
                AssetId.ARCANE_SCHOLAR,
                AssetId.AXE,
                AssetId.DOT_45_AUTOMATIC,
                AssetId.FINE_CLOTHES,
                AssetId.FISHING_NET,
                AssetId.HIRED_MUSCLE,
                AssetId.HOLY_CROSS,
                AssetId.HOLY_WATER,
                AssetId.KING_JAMES_BIBLE,
                AssetId.LUCKY_CIGARETTE_CASE,
                AssetId.PERSONAL_ASSISTANT,
                AssetId.PRIVATE_CARE,
                AssetId.PRIVATE_INVESTIGATOR,
                AssetId.SANCTUARY,
                AssetId.SPIRIT_DAGGER,
                AssetId.VATICAN_MISSIONARY,
                AssetId.ARCANE_TOME,
                AssetId.CARBINE_RIFLE,
                AssetId.DYNAMITE,
                AssetId.LODGE_RESEARCHER,
                AssetId.PUZZLE_BOX,
                AssetId.SILVER_TWILIGHT_RITUAL,
                AssetId.WITCH_DOCTOR,
                AssetId.AGENCY_QUARANTINE,
                AssetId.DOUBLE_BARRELED_SHOTGUN,
                AssetId.URBAN_GUIDE,
                AssetId.RITUAL_DAGGER,
                AssetId.HANDCUFFS,
                AssetId.BLUNDERBUSS,
                AssetId.PROFANE_TOME
        );
    }
}
