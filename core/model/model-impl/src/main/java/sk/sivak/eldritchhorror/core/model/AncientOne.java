package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.model.save.AncientOneSaveData;
import sk.sivak.eldritchhorror.core.model.save.AncientOneSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.AncientOneHelper;

/**
 * @author msivak
 */
public class AncientOne implements AncientOneWrite {

    private AncientOneInfo ancientOneInfo;

    @Override
    public AncientOneInfo getAncientOneInfo() {
        return ancientOneInfo;
    }

    @Override
    public void setAncientOneInfo(AncientOneInfo ancientOneInfo) {
        this.ancientOneInfo = ancientOneInfo;
    }

    @Override
    public void awaken() {
        if (ancientOneInfo.getAncientOneId() == AncientOneId.CTHULHU) {
            ancientOneInfo = AncientOneHelper.createAwakenCthulhu();
        } else if (ancientOneInfo.getAncientOneId() == AncientOneId.SHUB_NIGGURATH) {
            ancientOneInfo = AncientOneHelper.createAwakenShubNiggurath();
        } else if (ancientOneInfo.getAncientOneId() == AncientOneId.YOG_SOTHOTH) {
            ancientOneInfo = AncientOneHelper.createAwakenYogSothoth();
        }
    }

    @Override
    public void increasePower(int power) {
        ancientOneInfo.setPower(ancientOneInfo.getPower() + power);
    }

    @Override
    public AncientOneSaveData save() {
        AncientOneSaveData ancientOneSaveData = new AncientOneSaveData();
        ancientOneSaveData.setAncientOneId(ancientOneInfo.getAncientOneId());
        ancientOneSaveData.setAwaken(ancientOneInfo.isAwaken());
        ancientOneSaveData.setPower(ancientOneInfo.getPower());
        return ancientOneSaveData;
    }

    @Override
    public void load(AncientOneSaveDataRead ancientOneSaveData) {
        switch (ancientOneSaveData.getAncientOneId()) {
            case AZATHOTH:
                ancientOneInfo = AncientOneHelper.createAzathoth();
                break;
            case CTHULHU:
                if (!ancientOneSaveData.isAwaken()) {
                    ancientOneInfo = AncientOneHelper.createCthulhu();
                } else {
                    ancientOneInfo = AncientOneHelper.createAwakenCthulhu();
                }
                break;
            case SHUB_NIGGURATH:
                if (!ancientOneSaveData.isAwaken()) {
                    ancientOneInfo = AncientOneHelper.createShubNiggurath();
                } else {
                    ancientOneInfo = AncientOneHelper.createAwakenShubNiggurath();
                }
                break;
            case YOG_SOTHOTH:
                if (!ancientOneSaveData.isAwaken()) {
                    ancientOneInfo = AncientOneHelper.createYogSothoth();
                } else {
                    ancientOneInfo = AncientOneHelper.createAwakenYogSothoth();
                }
                break;
            default:
                throw new IllegalArgumentException("Not recognized Ancient One: " + ancientOneSaveData.getAncientOneId());
        }
        ((AncientOneHelper.AncientOneInfoImpl) ancientOneInfo).setPower(ancientOneSaveData.getPower());

    }
}
