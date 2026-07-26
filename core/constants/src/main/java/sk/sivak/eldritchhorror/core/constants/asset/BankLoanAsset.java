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
        return "When performing\n" +
                "an Acquire Assets action,\n" +
                "you may take a Debt Condition\n" +
                "to immediately add\n" +
                "two successes to your result.";
    }
}
