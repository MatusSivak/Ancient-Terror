package sk.sivak.eldritchhorror.core.action.impl.action;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.PerformedActionsRead;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId.THE_SAILOR;
import static sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId.THE_SOLDIER;
import static sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId.THE_SPY;

public class WithAfterActionPerformedDataTest {

    private static final List<InvestigatorId> ORDER = Arrays.asList(THE_SPY, THE_SAILOR, THE_SOLDIER);

    @Test public void continuesWithNextInTurnOrder() {
        assertEquals(THE_SAILOR, next(ORDER, THE_SPY, withActions(THE_SPY, THE_SAILOR, THE_SOLDIER)));
    }

    @Test public void skipsInvestigatorsWithoutActions() {
        assertEquals(THE_SOLDIER, next(ORDER, THE_SPY, withActions(THE_SOLDIER)));
    }

    @Test public void wrapsAroundToInvestigatorsSkippedBySwitching() {
        assertEquals(THE_SPY, next(ORDER, THE_SOLDIER, withActions(THE_SPY)));
    }

    @Test public void endsPhaseWhenNobodyElseHasActions() {
        assertNull(next(ORDER, THE_SAILOR, withActions(THE_SAILOR)));
    }

    @Test public void continuesAfterDefeatedActiveInvestigatorInTurnOrder() {
        List<InvestigatorId> inGame = Arrays.asList(THE_SPY, THE_SOLDIER);
        assertEquals(THE_SOLDIER, WithAfterActionPerformedData.findNextWithActions(
                ORDER, inGame, THE_SAILOR, withActions(THE_SPY, THE_SOLDIER)));
    }

    @Test public void ignoresInvestigatorsNoLongerInGame() {
        assertNull(WithAfterActionPerformedData.findNextWithActions(
                ORDER, Collections.singletonList(THE_SPY), THE_SPY, withActions(THE_SAILOR)));
    }

    private static InvestigatorId next(List<InvestigatorId> order, InvestigatorId active, PerformedActionsRead actions) {
        return WithAfterActionPerformedData.findNextWithActions(order, order, active, actions);
    }

    private static PerformedActionsRead withActions(InvestigatorId first, InvestigatorId... rest) {
        Set<InvestigatorId> withActions = EnumSet.of(first, rest);
        return new PerformedActionsRead() {
            @Override public int getActionsRemaining(InvestigatorId investigatorId) {
                return withActions.contains(investigatorId) ? 2 : 0;
            }

            @Override public boolean canPerformAction(InvestigatorId investigatorId, ActionPhaseAction actionPhaseAction) {
                return canPerformAction(investigatorId);
            }

            @Override public boolean canPerformAction(InvestigatorId investigatorId) {
                return withActions.contains(investigatorId);
            }

            @Override public Set<ActionPhaseAction> getPerformedActions(InvestigatorId investigatorId) {
                return Collections.emptySet();
            }

            @Override public void clearFreeActions(InvestigatorId investigatorId) {
            }

            @Override public void setDontWantToPerformAction(InvestigatorId investigatorId) {
            }

            @Override public boolean wantsToPerformAction(InvestigatorId investigatorId) {
                return true;
            }
        };
    }
}
