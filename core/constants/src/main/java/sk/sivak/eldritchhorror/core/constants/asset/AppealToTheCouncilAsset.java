package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class AppealToTheCouncilAsset extends AbstractAssetInfo {

    public AppealToTheCouncilAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.APPEAL_TO_THE_COUNCIL;
    }

    @Override
    public String getName() {
        return "Appeal to the Council";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "you may immediately spend 2 Clues\n" +
                "to retreat Doom.\n" +
                "Then discard this card.";
    }
}
