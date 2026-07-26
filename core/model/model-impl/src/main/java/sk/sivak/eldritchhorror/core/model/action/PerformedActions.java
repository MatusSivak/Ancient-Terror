package sk.sivak.eldritchhorror.core.model.action;

import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.PerformedActionsWrite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.model.action.PerformedActions.ACTIONS_PER_ROUND;

public class PerformedActions implements PerformedActionsWrite {
    final static int ACTIONS_PER_ROUND = 2;
    private Map<InvestigatorId, ActionData> map;


    @Override
    public void init(InvestigatorId... investigatorIds) {
        for (InvestigatorId investigatorId : investigatorIds) {
            map.put(investigatorId, new ActionData());
        }
    }

    @Override
    public void clearPerformedActionsMap() {
        map = new HashMap<>();
    }

    @Override
    public void reset() {
        IterableUtils.forEach(map.values(), ActionData::reset);
    }

    @Override
    public int getActionsRemaining(InvestigatorId investigatorId) {
        return map.get(investigatorId).getActionsRemaining();
    }

    @Override
    public void registerActionPerformed(InvestigatorId investigatorId, ActionPhaseAction actionPhaseAction) {
        map.get(investigatorId).registerActionPerformed(actionPhaseAction);
    }

    @Override
    public boolean canPerformAction(InvestigatorId investigatorId, ActionPhaseAction actionPhaseAction) {
        return map.get(investigatorId).canPerformAction(actionPhaseAction);
    }

    @Override
    public boolean canPerformAction(InvestigatorId investigatorId) {
        return map.get(investigatorId).canPerformAction();
    }

    @Override
    public Set<ActionPhaseAction> getPerformedActions(InvestigatorId investigatorId) {
        return map.get(investigatorId).getPerformedActions();
    }

    @Override
    public void addFreeAction(InvestigatorId investigatorId) {
        map.get(investigatorId).addFreeAction();
    }

    @Override
    public void clearFreeActions(InvestigatorId investigatorId) {
        map.get(investigatorId).clearFreeActions();
    }

    @Override
    public void setDontWantToPerformAction(InvestigatorId investigatorId) {
        map.get(investigatorId).setWantsToPerformAction(false);
    }

    @Override
    public boolean wantsToPerformAction(InvestigatorId investigatorId) {
        return map.get(investigatorId).wantsToPerformAction();
    }

    @Override
    public void removePerformedAction(InvestigatorId investigatorId, ActionPhaseAction actionPhaseAction) {
        map.get(investigatorId).removePerformedAction(actionPhaseAction);
    }
}

class ActionData {
    private Set<ActionPhaseAction> performedActions;
    private int actionsRemaining;
    private boolean wantsToPerformAction;

    ActionData() {
        performedActions = new HashSet<>();
        actionsRemaining = ACTIONS_PER_ROUND;
        wantsToPerformAction = true;
    }

    public void reset() {
        performedActions.clear();
        actionsRemaining = ACTIONS_PER_ROUND;
        wantsToPerformAction = true;
    }

    int getActionsRemaining() {
        return actionsRemaining;
    }

    void registerActionPerformed(ActionPhaseAction actionPhaseAction) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.SELECT_ACTION, actionPhaseAction.getName());
        actionsRemaining--;
        performedActions.add(actionPhaseAction);
    }

    boolean canPerformAction(ActionPhaseAction actionPhaseAction) {
        return canPerformAction() && !performedActions.contains(actionPhaseAction);
    }

    public boolean canPerformAction() {
        return actionsRemaining > 0;
    }

    public boolean wantsToPerformAction() {
        return wantsToPerformAction;
    }

    public void setWantsToPerformAction(boolean wantsToPerformAction) {
        this.wantsToPerformAction = wantsToPerformAction;
    }

    public void addFreeAction() {
        actionsRemaining++;
    }


    public Set<ActionPhaseAction> getPerformedActions() {
        return performedActions;
    }

    public void removePerformedAction(ActionPhaseAction actionPhaseAction) {
        performedActions.remove(actionPhaseAction);
    }

    public void clearFreeActions() {
        actionsRemaining = 0;
    }
}

