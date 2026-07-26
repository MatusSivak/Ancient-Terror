package sk.sivak.eldritchhorror.core.eventlistener.action;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;

/**
 * @author msivak
 */
public abstract class AbstractActionPhaseAction implements ActionPhaseAction {

    private ActionButtonDataImpl actionButtonData = new ActionButtonDataImpl();
    private String disabledReason;
    private boolean notRecommended;
    private String notRecommendedReason;
    private String generalDescription;
    private String additionalInfo;

    void setActionButtonId(ActionButtonData.ActionButtonId actionButtonId) {
        actionButtonData.setActionButtonId(actionButtonId);
    }

    void setTexturePath(String texturePath) {
        actionButtonData.setTexturePath(texturePath);
    }

    void setNeedsScaleDown(boolean needsScaleDown) {
        actionButtonData.setNeedsScaleDown(needsScaleDown);
    }

    void setNeedsMask(boolean needsMask) {
        actionButtonData.setNeedsMask(needsMask);
    }

    @Override
    public String getName() {
        return actionButtonData.getActionName();
    }

    public void setName(String name) {
        actionButtonData.setActionName(name);
    }

    @Override
    public boolean isDisabled() {
        return !actionButtonData.isEnabled();
    }

    public void setDisabled(boolean disabled) {
        actionButtonData.setEnabled(!disabled);
    }

    @Override
    public String getDisabledReason() {
        return disabledReason;
    }

    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    @Override
    public boolean isNotRecommended() {
        return notRecommended;
    }

    public void setNotRecommended(boolean notRecommended) {
        this.notRecommended = notRecommended;
    }

    @Override
    public String getNotRecommendedReason() {
        return notRecommendedReason;
    }

    public void setNotRecommendedReason(String notRecommendedReason) {
        this.notRecommendedReason = notRecommendedReason;
    }

    @Override
    public String getGeneralDescription() {
        return generalDescription;
    }

    public void setGeneralDescription(String generalDescription) {
        this.generalDescription = generalDescription;
    }

    @Override
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return actionButtonData.getActionName().replace('\n',' ');
    }

    public ActionButtonDataImpl getActionButtonData() {
        return actionButtonData;
    }
}
