package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.model.save.AncientOneSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

/**
 * @author msivak
 */
public interface AncientOneWrite extends AncientOneRead {

    void setAncientOneInfo(AncientOneInfo ancientOneInfo);

    SaveDataWrite.AncientOneSaveDataWrite save();

    void load(AncientOneSaveDataRead ancientOneSaveData);

    void awaken();

    void increasePower(int power);
}
