package sk.sivak.eldritchhorror.core.action.impl.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.artifact.AbstractArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AbstractAssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class ExecuteActionAction extends AbstractHookableAction<ActionPhaseAction, Void> {

    private static final Logger logger = LogManager.getLogger(ExecuteActionAction.class);

    public ExecuteActionAction() {
        super(BeforeAfterEvent.EXECUTE_ACTION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        ServicePlatform.get().getGameController().hideCityInfoLabels();
        input.execute();
        if (input instanceof CardInfoAware) {
            if (((CardInfoAware) input).getCardInfo() instanceof AbstractSpellInfo) {
                ((AbstractSpellInfo) ((CardInfoAware) input).getCardInfo()).enable();
            } else if (((CardInfoAware) input).getCardInfo() instanceof AbstractAssetInfo) {
                ((AbstractAssetInfo) ((CardInfoAware) input).getCardInfo()).enable();
            } else if (((CardInfoAware) input).getCardInfo() instanceof AbstractConditionInfo) {
                // we don't have disabled condition yet
            } else if (((CardInfoAware) input).getCardInfo() instanceof AbstractArtifactInfo) {
                ((AbstractArtifactInfo) ((CardInfoAware) input).getCardInfo()).enable();
            } else {
                throw new IllegalArgumentException("What type of card is this");
            }
        }
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ServicePlatform.get().getPerformedActions().registerActionPerformed(activeInvestigatorId, input);
        ss.onSuccess(null);
    }
}
