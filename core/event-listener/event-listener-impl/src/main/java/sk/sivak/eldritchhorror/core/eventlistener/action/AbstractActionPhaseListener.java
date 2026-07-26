package sk.sivak.eldritchhorror.core.eventlistener.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.model.LocationMapRead;
import sk.sivak.eldritchhorror.core.service.BasicActionService;
import sk.sivak.eldritchhorror.core.service.InvestigatorService;
import sk.sivak.eldritchhorror.core.service.Service;

/**
 * @author msivak
 */
public abstract class AbstractActionPhaseListener<T extends AbstractActionPhaseAction> implements ActionPhaseListener {

    private static final Logger logger = LogManager.getLogger(AbstractActionPhaseListener.class);
    protected String disabledReason = "You already performed this action.";
    protected String notRecommendedReason = "This action will have no effect.";
    protected String name;
    protected T action;
    protected CollectAvailableActionsData eventData;

    protected AbstractActionPhaseListener() {
        action = createAction();
        name = action.getClass().getSimpleName();;
    }

    @Override
    public void onNotify(CollectAvailableActionsData eventData) {
        this.eventData = eventData;
        if (!isVisible()) {
            return;
        }
        action.setName(getName());
        action.setGeneralDescription(getGeneralDescription());
        action.setActionButtonId(getActionButtonId());
        action.setTexturePath(getTexturePath());
        action.setNeedsScaleDown(getNeedsScaleDown());
        action.setNeedsMask(getNeedsMask());

        if (isDisabled()) {
            action.setDisabled(true);
            action.setDisabledReason(getDisabledReason());
        } else {
            action.setDisabled(false);
            action.setDisabledReason(null);
        }

        if (isNotRecommended()) {
            action.setNotRecommended(true);
            action.setNotRecommendedReason(getNotRecommendedReason());
        } else {
            action.setNotRecommended(false);
            action.setNotRecommendedReason(null);
        }
        action.setAdditionalInfo(getAdditionalInfo());
        eventData.addActionPhaseAction(action);
    }

    protected String getName() {
        return name;
    }

    protected abstract String getAdditionalInfo();

    protected abstract String getGeneralDescription();

    //
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return null;
    }

    protected abstract String getTexturePath();

    protected boolean getNeedsScaleDown() {
        return false;
    }

    protected boolean getNeedsMask() {
        return false;
    }

    //

    protected String getDisabledReason() {
        return disabledReason;
    }

    protected String getNotRecommendedReason() {
        return notRecommendedReason;
    }

    protected abstract T createAction();

    protected abstract boolean isVisible();

    protected abstract boolean isDisabled();

    protected abstract boolean isNotRecommended();

    @Override
    public Class<CollectAvailableActionsData> getDataClass() {
        return CollectAvailableActionsData.class;
    }

    protected InvestigatorsRead getInvestigators() {
        return ServicePlatform.get().getInvestigators();
    }

    protected LocationMapRead getLocationMap() {
        return ServicePlatform.get().getLocationMap();
    }

    protected Service getService() {
        return ServicePlatform.get().getService();
    }

    protected InvestigatorService getInvestigatorActionService() {
        return ServicePlatform.get().getInvestigatorService();
    }

    protected BasicActionService getBasicActionService() {
        return ServicePlatform.get().getBasicActionService();
    }

    protected InvestigatorRead getActiveInvestigator() {
        return getInvestigators().getActiveInvestigator();
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }


}
