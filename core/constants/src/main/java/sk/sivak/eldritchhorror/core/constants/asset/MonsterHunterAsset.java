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
        return "Combat: +2 Strength.\n" +
                "\n" +
                "ACTION: 1 Monster on your space\n" +
                "loses 1 Health.";
    }
}
