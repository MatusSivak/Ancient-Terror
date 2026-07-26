package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.model.save.DoomTrackSaveData;
import sk.sivak.eldritchhorror.core.model.save.DoomTrackSaveDataRead;

/**
 * @author msivak
 */
public class DoomTrack implements DoomTrackWrite {

    private int currentDoom;

    @Override
    public int getCurrentDoom() {
        return currentDoom;
    }

    @Override
    public void initDoomTrack(int value) {
        currentDoom = value;
    }

    @Override
    public int advanceDoom() {
        return --currentDoom;
    }

    @Override
    public int retreatDoom() {
        return ++currentDoom;
    }

    @Override
    public DoomTrackSaveData save() {
        DoomTrackSaveData doomTrackSaveData = new DoomTrackSaveData();
        doomTrackSaveData.setCurrentDoom(currentDoom);
        return doomTrackSaveData;
    }

    @Override
    public void load(DoomTrackSaveDataRead doomTrackSaveData) {
        currentDoom = doomTrackSaveData.getCurrentDoom();
    }
}
