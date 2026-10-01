package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class MysticBountyHunterAsset extends AbstractAssetInfo {

    public MysticBountyHunterAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.MYSTIC_BOUNTY_HUNTER;
    }

    @Override
    public String getName() {
        return "Mystic Bounty Hunter";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Combat: +1 Strength.\n" +
                "Spell effects: +2 Lore.";
    }
}
