package sk.sivak.eldritchhorror.core.eventtype.data.rumor;

public class InitRumorCardData {
    private String rumorId;
    private Integer cluesRequired;

    public String getRumorId() {
        return rumorId;
    }

    public void setRumorId(String rumorId) {
        this.rumorId = rumorId;
    }

    public Integer getCluesRequired() {
        return cluesRequired;
    }

    public void setCluesRequired(Integer cluesRequired) {
        this.cluesRequired = cluesRequired;
    }
}
