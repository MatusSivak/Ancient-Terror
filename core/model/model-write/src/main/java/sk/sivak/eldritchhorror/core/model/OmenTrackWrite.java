package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.model.save.OmenTrackSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

/**
 * @author msivak
 */
public interface OmenTrackWrite extends OmenTrackRead {

    void initOmenTrack();

    void advanceOmen();

    void addTokenToOmen(OmenId omenId);

    void setCurrentOmen(OmenId omenId);

    void removeTokenFromOmen(OmenId omenId);

    SaveDataWrite.OmenTrackSaveDataWrite save();

    void load(OmenTrackSaveDataRead omenTrackSaveData);

}
