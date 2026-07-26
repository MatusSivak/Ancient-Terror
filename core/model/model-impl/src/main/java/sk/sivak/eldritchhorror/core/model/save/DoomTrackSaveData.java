package sk.sivak.eldritchhorror.core.model.save;

public class DoomTrackSaveData implements SaveDataWrite.DoomTrackSaveDataWrite {
    private int currentDoom;

    public void setCurrentDoom(int currentDoom) {
        this.currentDoom = currentDoom;
    }

    @Override
    public int getCurrentDoom() {
        return currentDoom;
    }
}