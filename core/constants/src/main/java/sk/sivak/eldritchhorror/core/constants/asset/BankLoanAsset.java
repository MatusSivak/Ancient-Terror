package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class BankLoanAsset extends AbstractAssetInfo {

    public BankLoanAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.BANK_LOAN;
    }

    @Override
    public String getName() {
        return "Bank Loan";
    }

    @Override
    public int getCost() {
        return -2;
    }

    @Override
    public String getDescription() {
        return "Acquire Assets action:\n" +
                "take a Debt Condition → +2 successes.";
    }
}
