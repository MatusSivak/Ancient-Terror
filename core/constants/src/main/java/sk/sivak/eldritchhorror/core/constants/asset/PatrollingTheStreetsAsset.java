package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PatrollingTheStreetsAsset extends AbstractAssetInfo {

    public PatrollingTheStreetsAsset() {
        traits.add(AssetTrait.TASK);
    }

    @Override
    public AssetId getId() {
        return AssetId.PATROLLING_THE_STREETS;
    }

    @Override
    public String getName() {
        return "Patrolling the Streets";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Defeat a Monster with toughness\n" +
                "3 or more in combat:\n" +
                "Discard this card → retreat Doom.";
    }
}
