package sk.sivak.eldritchhorror.core.model.save;

import java.util.LinkedList;
import java.util.List;

public class RumorsSaveData implements SaveDataWrite.RumorsSaveDataWrite {
    private List<ActiveRumorSaveDataRead> activeRumors = new LinkedList<>();

    @Override
    public List<ActiveRumorSaveDataRead> getActiveRumors() {
        return activeRumors;
    }

    public void setActiveRumors(List<ActiveRumorSaveDataRead> activeRumors) {
        this.activeRumors = activeRumors;
    }

    public static class ActiveRumorSaveData implements ActiveRumorSaveDataRead{
        private String rumorId;
        private Integer timeRemaining;
        private Integer cluesRequired;

        @Override
        public String getRumorId() {
            return rumorId;
        }

        public void setRumorId(String rumorId) {
            this.rumorId = rumorId;
        }

        @Override
        public Integer getTimeRemaining() {
            return timeRemaining;
        }

        public void setTimeRemaining(Integer timeRemaining) {
            this.timeRemaining = timeRemaining;
        }

        @Override
        public Integer getCluesRequired() {
            return cluesRequired;
        }

        public void setCluesRequired(Integer cluesRequired) {
            this.cluesRequired = cluesRequired;
        }
    }
}
