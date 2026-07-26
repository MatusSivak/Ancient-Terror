package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class Dot45AutomaticAsset extends AbstractAssetInfo {

    public Dot45AutomaticAsset() {
        traits.add(AssetTrait.WEAPON);
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.DOT_45_AUTOMATIC;
    }

    @Override
    public String getName() {
        return ".45 Automatic";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Gain +3 Strength during Combat Encounters.";
    }
}
