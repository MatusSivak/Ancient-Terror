package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PoliceLedgerAsset extends AbstractAssetInfo {

    public PoliceLedgerAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.POLICE_LEDGER;
    }

    @Override
    public String getName() {
        return "Police Ledger";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Rest: you may test Observation.\n" +
                "Pass: Discard this card → gain 1 Clue.";
    }
}
