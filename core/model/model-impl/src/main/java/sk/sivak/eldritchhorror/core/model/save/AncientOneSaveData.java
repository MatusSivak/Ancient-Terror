package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;

public class AncientOneSaveData implements SaveDataWrite.AncientOneSaveDataWrite {
    private AncientOneId ancientOneId;
    private boolean awaken;
    private int power;

    public void setAncientOneId(AncientOneId ancientOneId) {
        this.ancientOneId = ancientOneId;
    }

    @Override
    public AncientOneId getAncientOneId() {
        return ancientOneId;
    }

    public void setAwaken(boolean awaken) {
        this.awaken = awaken;
    }

    @Override
    public boolean isAwaken() {
        return awaken;
    }

    @Override
    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
