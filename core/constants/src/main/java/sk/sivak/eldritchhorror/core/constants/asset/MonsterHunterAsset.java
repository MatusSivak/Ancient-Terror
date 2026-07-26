package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class MonsterHunterAsset extends AbstractAssetInfo {

    public MonsterHunterAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.MONSTER_HUNTER;
    }

    @Override
    public String getName() {
        return "Monster Hunter";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Gain +2 Strength\n" +
                "during Combat Encounters.\n" +
                "\n" +
                "ACTION: A Monster of your choice\n" +
                "on your space loses one Health.";
    }
}
