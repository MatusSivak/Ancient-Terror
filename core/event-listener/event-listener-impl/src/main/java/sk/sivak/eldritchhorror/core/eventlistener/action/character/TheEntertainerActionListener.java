package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author msivak
 */
public class TheEntertainerActionListener extends AbstractActionPhaseListener<TheEntertainerActionListener.TheEntertainerAction> {

    private static final Logger logger = LogManager.getLogger(TheEntertainerActionListener.class);

    public TheEntertainerActionListener() {
        name = "Repeat\nAction";
    }

    @Override
    protected String getAdditionalInfo() {

        if (!isDisabled()) {
            Set<ActionPhaseAction> performedActions = ServicePlatform.get().getPerformedActions().getPerformedActions(InvestigatorId.THE_ENTERTAINER);
            StringBuilder result = new StringBuilder();
            for (ActionPhaseAction performedAction : performedActions) {
                result.append(performedAction.getName().replace('\n', ' '));
                result.append("\n");
            }
            return result.toString();
        }
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Perform an action you have already performed this round.";
    }

    @Override
    protected TheEntertainerAction createAction() {
        return new TheEntertainerAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_ENTERTAINER;
    }

    @Override
    protected boolean isDisabled() {
        Set<ActionPhaseAction> performedActions = ServicePlatform.get().getPerformedActions().getPerformedActions(InvestigatorId.THE_ENTERTAINER);
        if (performedActions.isEmpty()) {
            disabledReason = "You haven't performed any action this round.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/ICON_THE_ENTERTAINER.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheEntertainerAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing the entertainer action...");
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeFilterPerformedActionsListener(), BeforeAfterEvent.FILTER_PERFORMED_ACTIONS);
            ServicePlatform.get().getGameService().justPerformAction();

        }
    }

    private class BeforeFilterPerformedActionsListener extends EventListenerImpl<CollectAvailableActionsData> {

        @Override
        public void onNotify(CollectAvailableActionsData eventData) {
            if (getActiveInvestigator().getInfo().getInvestigatorId() != InvestigatorId.THE_ENTERTAINER) {
                return;
            }
            LinkedList<ActionPhaseAction> backupActions = new LinkedList<>(eventData.getActionPhaseActions());
            List<ActionPhaseAction> actionPhaseActions = eventData.getActionPhaseActions();
            Set<ActionPhaseAction> performedActions = ServicePlatform.get().getPerformedActions().getPerformedActions(InvestigatorId.THE_ENTERTAINER);

            IterableUtils.removeIf(actionPhaseActions, actionPhaseAction -> !performedActions.contains(actionPhaseAction));
            IterableUtils.removeIf(actionPhaseActions, actionPhaseAction -> actionPhaseAction == action);

            enableComponentActions(actionPhaseActions);
            if (actionPhaseActions.isEmpty() ||
                    (actionPhaseActions.size()==1 && actionPhaseActions.get(0).isDisabled()) ||
                    (actionPhaseActions.size()==2 && actionPhaseActions.get(0).isDisabled() && actionPhaseActions.get(1).isDisabled())) {
                for (ActionPhaseAction backupAction : backupActions) {
                    eventData.addActionPhaseAction(backupAction);
                }
                ServicePlatform.get().getEventQueue().unregisterListener(this);

                Question<Boolean> question = new Question<>();
                question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
                question.setTitle("This action cannot be performed again.");
                ServicePlatform.get().getGameService().ask(question).subscribe((response) -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getBasicActionService().addFreeAction();
                    ServicePlatform.get().getBasicActionService().removePerformedAction(action);
                    ServicePlatform.get().getBasicActionService().addEventCommand(whatever -> {
                        action.setAdditionalInfo(getAdditionalInfo());
                    });
                    ServicePlatform.get().getService().convertTo(CollectAvailableActionsData.class, () -> eventData);
                    ServicePlatform.get().getService().release();
                });

            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getBasicActionService().addFreeAction();
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.FILTER_PERFORMED_ACTIONS, eventData);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
                ServicePlatform.get().getService().release();
            }
        }

        private void enableComponentActions(List<ActionPhaseAction> actionPhaseActions) {
            for (ActionPhaseAction actionPhaseAction : actionPhaseActions) {
                if ("Action already performed this round".equals(actionPhaseAction.getDisabledReason())) {
                    ((AbstractActionPhaseAction) actionPhaseAction).setDisabledReason(null);
                    ((AbstractActionPhaseAction) actionPhaseAction).setDisabled(false);
                }
            }
        }

        @Override
        public Class<CollectAvailableActionsData> getDataClass() {
            return CollectAvailableActionsData.class;
        }
    }

}
