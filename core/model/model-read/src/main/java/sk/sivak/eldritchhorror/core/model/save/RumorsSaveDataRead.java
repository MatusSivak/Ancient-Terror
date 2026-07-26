package sk.sivak.eldritchhorror.core.model.save;

import java.util.List;

public interface RumorsSaveDataRead {
    List<ActiveRumorSaveDataRead> getActiveRumors();

    interface ActiveRumorSaveDataRead {

        String getRumorId();

        Integer getTimeRemaining();

        Integer getCluesRequired();
    }
}
