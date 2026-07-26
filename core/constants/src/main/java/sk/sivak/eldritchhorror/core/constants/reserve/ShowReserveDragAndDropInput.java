package sk.sivak.eldritchhorror.core.constants.reserve;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;

public class ShowReserveDragAndDropInput {

    private boolean bankLoanAvailable;

    private int testScore;

    private AssetId mandatoryAssetId;

    public boolean isBankLoanAvailable() {
        return bankLoanAvailable;
    }

    public AssetId getMandatoryAssetId() {
        return mandatoryAssetId;
    }

    public void setMandatoryAssetId(AssetId mandatoryAssetId) {
        this.mandatoryAssetId = mandatoryAssetId;
    }

    public void setBankLoanAvailable(boolean bankLoanAvailable) {
        this.bankLoanAvailable = bankLoanAvailable;
    }

    public int getTestScore() {
        return testScore;
    }

    public void setTestScore(int testScore) {
        this.testScore = testScore;
    }
}
