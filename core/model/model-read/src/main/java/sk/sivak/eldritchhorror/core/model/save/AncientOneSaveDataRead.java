package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;

public interface AncientOneSaveDataRead {
    AncientOneId getAncientOneId();
    boolean isAwaken();
    int getPower();
}
