package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class CollectAvailableActionsData {

    private List<ActionPhaseAction> actionPhaseActions = new LinkedList<>();

    public void addActionPhaseAction(ActionPhaseAction actionPhaseAction) {
        actionPhaseActions.add(actionPhaseAction);
    }

    public List<ActionPhaseAction> getActionPhaseActions() {
        return actionPhaseActions;
    }

    @Override
    public String toString() {
        return "" + actionPhaseActions;
    }
}
