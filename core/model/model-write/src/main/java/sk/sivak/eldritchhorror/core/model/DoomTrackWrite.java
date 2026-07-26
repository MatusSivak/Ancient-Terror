package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.model.save.DoomTrackSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

/**
 * @author msivak
 */
public interface DoomTrackWrite extends DoomTrackRead {

    void initDoomTrack(int value);

    int advanceDoom();

    int retreatDoom();

    SaveDataWrite.DoomTrackSaveDataWrite save();

    void load(DoomTrackSaveDataRead doomTrackSaveData);
}
