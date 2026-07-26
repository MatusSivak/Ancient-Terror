package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;

import java.util.List;

/**
 * @author msivak
 */
public interface InvestigatorActionProvider {

    HookableAction<TravelData, LocationInfo.Connection> selectTravelLocation();

    HookableAction<LocationInfo.Connection, InvestigatorId> travelToLocation();

    HookableAction<Object, LocationInfo.Connection> selectTravelTicketLocation();

    HookableAction<LocationInfo.Connection, LocationInfo.Connection> loseTicket();

    HookableAction<Void, PathType> selectTravelTicket();

    HookableAction<PathType, Void> gainTravelTicket();

    HookableAction<PathType, PathType> discardTicket();

    HookableAction<Object, RestData> createRestDataAction();

    HookableAction<RestData, RestData> rest();

    HookableAction<Object, FocusData> createFocusDataAction();

    HookableAction<FocusData, FocusData> focus();

    Action<Object, InvestigatorId> changeActiveInvestigator();

    Action<Object, InvestigatorId> changeActiveInvestigator(InvestigatorId investigatorId);

    HookableAction<InvestigatorId, Void> showActiveInvestigator(boolean displayTransition);

    HookableAction<InvestigatorRestriction, InvestigatorId> selectInvestigator();

    HookableAction<Object, Void> addFreeAction();

    /* TRADE */
    HookableAction<Object, List<InvestigatorId>> findInvestigatorsForTrade();

    HookableAction<List<InvestigatorId>, InvestigatorId> selectInvestigatorForTrade();

    HookableAction<InvestigatorId, TradeData> collectItemsForTrade();

    HookableAction<TradeData, TradeData> confirmTrade();

    HookableAction<TradeData, TradeData> transferTradedItems();

    HookableAction<ActionPhaseAction, ActionPhaseAction> removePerformedAction();

    HookableAction<Stat, Stat> improveSkill();

    HookableAction<DelayedData, DelayedData> becomeDelayed();

    HookableAction<InvestigatorId, InvestigatorId> endDelayed();

    HookableAction<InvestigatorId, InvestigatorId> lostInTimeAndSpace();

    Action<Object, InvestigatorId> findActiveInvestigator();

    HookableAction<InvestigatorId, InvestigatorId> showLostInTimeAndSpace();

    HookableAction<InvestigatorId, InvestigatorId> hideLostInTimeAndSpace();

    HookableAction<Object, InvestigatorId> spawnInvestigator(InvestigatorId investigatorId, LocationId locationId);

    HookableAction<Object, Void> endOfMoveSpaces();

    Action<Object, Void> unsetActiveInvestigator();

    Action<Object, Void> removeInvestigatorFromPlace(InvestigatorId investigatorId);

    Action<Object, Void> putGateToTop(GateInfo selectedGate);

    Action<Object, Void> putGateToBottom(GateInfo selectedGate);

    HookableAction<Integer, Boolean> spendCluesAsGroup();

    HookableAction<LoseImprovementData, LoseImprovementData> loseImprovement();

    Action<Object, Void> selectReplacingInvestigator(InvestigatorId removedInvestigator);
}
