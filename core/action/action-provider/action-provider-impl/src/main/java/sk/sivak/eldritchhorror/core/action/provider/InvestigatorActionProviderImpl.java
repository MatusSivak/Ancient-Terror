package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.action.general.*;
import sk.sivak.eldritchhorror.core.action.impl.action.general.trade.*;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.*;
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
public class InvestigatorActionProviderImpl extends AbstractActionProvider implements InvestigatorActionProvider {

    @Override
    public HookableAction<TravelData, LocationInfo.Connection> selectTravelLocation() {
        return new SelectTravelLocationAction();
    }

    @Override
    public HookableAction<LocationInfo.Connection, InvestigatorId> travelToLocation() {
        return new TravelToLocationAction();
    }

    @Override
    public HookableAction<Object, LocationInfo.Connection> selectTravelTicketLocation() {
        return new SelectTravelTicketLocationAction();
    }

    @Override
    public HookableAction<LocationInfo.Connection, LocationInfo.Connection> loseTicket() {
        return new LoseTicketAction();
    }

    @Override
    public HookableAction<Void, PathType> selectTravelTicket() {
        return new SelectTravelTicketAction();
    }

    @Override
    public HookableAction<PathType, Void> gainTravelTicket() {
        return new GainTravelTicketAction();
    }

    @Override
    public HookableAction<PathType, PathType> discardTicket() {
        return new DiscardTicketAction();
    }

    @Override
    public HookableAction<Object, RestData> createRestDataAction() {
        return new CreateRestDataAction();
    }

    @Override
    public HookableAction<RestData, RestData> rest() {
        return new RestActionAction();
    }

    @Override
    public HookableAction<Object, FocusData> createFocusDataAction() {
        return new CreateFocusDataAction();
    }

    @Override
    public HookableAction<FocusData, FocusData> focus() {
        return new FocusActionAction();
    }

    @Override
    public Action<Object, InvestigatorId> changeActiveInvestigator() {
        return new ChangeActiveInvestigatorAction();
    }

    @Override
    public Action<Object, InvestigatorId> changeActiveInvestigator(InvestigatorId investigatorId) {
        return new ChangeActiveInvestigatorAction(investigatorId);
    }

    @Override
    public HookableAction<InvestigatorId, Void> showActiveInvestigator(boolean displayTransition) {
        return new ShowActiveInvestigatorAction(displayTransition);
    }

    @Override
    public HookableAction<InvestigatorRestriction, InvestigatorId> selectInvestigator() {
        return new SelectInvestigatorAction();
    }

    @Override
    public HookableAction<Object, Void> addFreeAction() {
        return new AddFreeActionAction();
    }

    /* TRADE */

    @Override
    public HookableAction<Object, List<InvestigatorId>> findInvestigatorsForTrade() {
        return new FindInvestigatorsForTradeAction();
    }

    @Override
    public HookableAction<List<InvestigatorId>, InvestigatorId> selectInvestigatorForTrade() {
        return new SelectInvestigatorForTradeAction();
    }

    @Override
    public HookableAction<InvestigatorId, TradeData> collectItemsForTrade() {
        return new CollectItemsForTradeAction();
    }

    @Override
    public HookableAction<TradeData, TradeData> confirmTrade() {
        return new ConfirmTradeAction();
    }

    @Override
    public HookableAction<TradeData, TradeData> transferTradedItems() {
        return new TransferTradedItemsAction();
    }

    @Override
    public HookableAction<ActionPhaseAction, ActionPhaseAction> removePerformedAction() {
        return new RemovePerformedActionAction();
    }

    @Override
    public HookableAction<Stat, Stat> improveSkill() {
        return new ImproveSkillAction();
    }

    @Override
    public HookableAction<DelayedData, DelayedData> becomeDelayed() {
        return new BecomeDelayedAction();
    }

    @Override
    public HookableAction<InvestigatorId, InvestigatorId> endDelayed() {
        return new EndDelayedAction();
    }

    @Override
    public HookableAction<InvestigatorId, InvestigatorId> lostInTimeAndSpace() {
        return new LostInTimeAndSpaceAction();
    }

    @Override
    public Action<Object, InvestigatorId> findActiveInvestigator() {
        return new FindActiveInvestigatorAction();
    }

    @Override
    public HookableAction<InvestigatorId, InvestigatorId> showLostInTimeAndSpace() {
        return new ShowLostInTimeAndSpaceAction();
    }

    @Override
    public HookableAction<InvestigatorId, InvestigatorId> hideLostInTimeAndSpace() {
        return new HideLostInTimeAndSpaceAction();
    }

    @Override
    public HookableAction<Object, InvestigatorId> spawnInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return new SpawnInvestigatorAction(investigatorId, locationId);
    }

    @Override
    public Action<Object, Void> removeInvestigatorFromPlace(InvestigatorId investigatorId) {
        return new RemoveInvestigatorFromPlaceAction(investigatorId);
    }

    @Override
    public HookableAction<Object, Void> endOfMoveSpaces() {
        return new EndOfMoveSpacesAction();
    }

    @Override
    public Action<Object, Void> unsetActiveInvestigator() {
        return new UnsetActiveInvestigatorAction();
    }

    @Override
    public Action<Object, Void> putGateToTop(GateInfo selectedGate) {
        return new PutGateToTopAction(selectedGate);
    }

    @Override
    public Action<Object, Void> putGateToBottom(GateInfo selectedGate) {
        return new PutGateToBottomAction(selectedGate);
    }

    @Override
    public HookableAction<Integer, Boolean> spendCluesAsGroup() {
        return new SpendCluesAsGroupAction();
    }

    @Override
    public HookableAction<LoseImprovementData, LoseImprovementData> loseImprovement() {
        return new LoseImprovementAction();
    }

    @Override
    public Action<Object, Void> selectReplacingInvestigator(InvestigatorId removedInvestigator) {
        return new SelectReplacingInvestigatorAction(removedInvestigator);
    }
}
