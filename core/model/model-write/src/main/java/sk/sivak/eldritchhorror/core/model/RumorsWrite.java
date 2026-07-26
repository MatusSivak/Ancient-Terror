package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.model.save.RumorsSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

public interface RumorsWrite extends RumorsRead {

    void init();

    void initActiveRumor(String rumorId, Integer cluesRequired);

    void countdown(String rumorId, int amount);

    SaveDataWrite.RumorsSaveDataWrite save();

    void load(RumorsSaveDataRead rumorsSaveData);

    void removeActiveRumor(String rumorId);
}
