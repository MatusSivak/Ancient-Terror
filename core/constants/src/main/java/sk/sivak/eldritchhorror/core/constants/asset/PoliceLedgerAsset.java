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
        return "When you perform a Rest action,\n" +
                "you may attempt to decipher the ledger\n" +
                "(Test Observation).\n" +
                "If you pass, you may\n" +
                "discard this card to gain 1 Clue.";
    }
}
