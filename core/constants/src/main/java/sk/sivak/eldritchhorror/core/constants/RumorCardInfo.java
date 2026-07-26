package sk.sivak.eldritchhorror.core.constants;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public interface RumorCardInfo {
    String getId();
    String getFlavorText();
    String getTitleText();
    String getObjectiveText();
    String getFailureText();
    String getReckoningText();
    Integer getTimeRemaining();
    String getOngoingEffectText();
    LocationId getRumorLocation();
    boolean isStormSpawned();
    Integer getProgress();
    Integer getComplexity();
    Integer getCluesRequired();
}
