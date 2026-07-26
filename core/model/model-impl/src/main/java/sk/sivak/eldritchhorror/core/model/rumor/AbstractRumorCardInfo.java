package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public abstract class AbstractRumorCardInfo implements RumorCardInfo {
    private String id;
    private String flavorText;
    private String titleText;
    private String objectiveText;
    private String failureText;
    private String reckoningText;
    private Integer timeRemaining;
    private String ongoingEffectText;
    private LocationId rumorLocation;
    private Integer progress;
    private Integer complexity;
    private Integer cluesRequired;
    private boolean stormSpawned;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    @Override
    public String getFlavorText() {
        return flavorText;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    @Override
    public String getObjectiveText() {
        return objectiveText;
    }

    public void setObjectiveText(String objectiveText) {
        this.objectiveText = objectiveText;
    }

    @Override
    public String getFailureText() {
        return failureText;
    }

    public void setFailureText(String failureText) {
        this.failureText = failureText;
    }

    @Override
    public String getReckoningText() {
        return reckoningText;
    }

    public void setReckoningText(String reckoningText) {
        this.reckoningText = reckoningText;
    }

    @Override
    public Integer getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(Integer timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    @Override
    public String getOngoingEffectText() {
        return ongoingEffectText;
    }

    public void setOngoingEffectText(String ongoingEffectText) {
        this.ongoingEffectText = ongoingEffectText;
    }

    @Override
    public LocationId getRumorLocation() {
        return rumorLocation;
    }

    public void setRumorLocation(LocationId rumorLocation) {
        this.rumorLocation = rumorLocation;
    }

    @Override
    public boolean isStormSpawned() {
        return stormSpawned;
    }

    public void setStormSpawned(boolean stormSpawned) {
        this.stormSpawned = stormSpawned;
    }

    @Override
    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @Override
    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    @Override
    public Integer getCluesRequired() {
        return cluesRequired;
    }

    public void setCluesRequired(Integer cluesRequired) {
        this.cluesRequired = cluesRequired;
    }
}
