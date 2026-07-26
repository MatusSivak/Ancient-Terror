package sk.sivak.eldritchhorror.core.constants.action;

/**
 * @author msivak
 */
public interface ActionPhaseAction {

    ActionButtonData getActionButtonData();

    String getName();

    boolean isDisabled();

    void execute();

    String getDisabledReason();

    boolean isNotRecommended();

    String getNotRecommendedReason();

    String getGeneralDescription();

    String getAdditionalInfo();
}
