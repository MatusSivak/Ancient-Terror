package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.action.provider.InvestigatorActionProvider;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;

/**
 * @author msivak
 */
public class BasicActionServiceImpl extends AbstractCommonService implements BasicActionService {

    private InvestigatorActionProvider investigatorActionProvider;

    public void setInvestigatorActionProvider(InvestigatorActionProvider investigatorActionProvider) {
        this.investigatorActionProvider = investigatorActionProvider;
    }

    @Override
    public void travel() {
        hold();
        executeHookable(new TravelData(false), investigatorActionProvider.selectTravelLocation());
        travelToLocation();
        selectTravelTicketLocation();
        release();
    }

    @Override
    public void travelToLocation(LocationInfo.Connection location) {
        executeHookable(location, investigatorActionProvider.travelToLocation());
    }

    @Override
    public void travelToLocation() {
        executeHookable(investigatorActionProvider.travelToLocation());
    }

    @Override
    public void moveSpaces(TravelData travelData, int amount) {
        hold();
        for (int i = 0; i < amount; i++) {
            executeHookable(travelData, investigatorActionProvider.selectTravelLocation());
            this.<LocationInfo.Connection>addEventCommand(in -> {
                if (in == null) {
                    skipAfterEvent(BeforeAfterEvent.END_OF_MOVE_SPACES, null);
                }
            });
            travelToLocation();
        }
        executeHookable(investigatorActionProvider.endOfMoveSpaces());
        release();
    }

    @Override
    public void selectTravelTicketLocation() {
        executeHookable(investigatorActionProvider.selectTravelTicketLocation());
    }

    @Override
    public void loseTicket() {
        executeHookable(investigatorActionProvider.loseTicket());
    }

    @Override
    public void buyTravelTicket() {
        hold();
        executeHookable(investigatorActionProvider.selectTravelTicket());
        executeHookable(investigatorActionProvider.gainTravelTicket());
        release();
    }

    @Override
    public void gainTravelTicket(PathType pathType) {
        executeHookable(pathType, investigatorActionProvider.gainTravelTicket());
    }

    @Override
    public void discardTicket() {
        executeHookable(investigatorActionProvider.discardTicket());
    }

    @Override
    public void discardTicket(PathType pathType) {
        executeHookable(pathType, investigatorActionProvider.discardTicket());
    }

    @Override
    public void rest() {
        hold();
        executeHookable(investigatorActionProvider.createRestDataAction());
        executeHookable(investigatorActionProvider.rest());
        convertFromTo(RestData.class, Void.class, in -> null);
        release();
    }

    @Override
    public void focus() {
        hold();
        executeHookable(investigatorActionProvider.createFocusDataAction());
        executeHookable(investigatorActionProvider.focus());
        convertFromTo(FocusData.class, Void.class, in -> null);
        release();
    }

    @Override
    public void trade() {
        hold();
        executeHookable(investigatorActionProvider.findInvestigatorsForTrade());
        executeHookable(investigatorActionProvider.selectInvestigatorForTrade());
        executeHookable(investigatorActionProvider.collectItemsForTrade());
        executeHookable(investigatorActionProvider.confirmTrade());
        executeHookable(investigatorActionProvider.transferTradedItems());
        convertToNull();
        release();
    }

    @Override
    public void psychicAction(TradeData tradeData) {
        hold();
        executeHookable(tradeData, investigatorActionProvider.confirmTrade());
        executeHookable(investigatorActionProvider.transferTradedItems());
        convertToNull();
        release();
    }

    @Override
    public void removePerformedAction(ActionPhaseAction actionPhaseAction) {
        executeHookable(actionPhaseAction, investigatorActionProvider.removePerformedAction());
    }

    @Override
    public void addFreeAction() {
        executeHookable(investigatorActionProvider.addFreeAction());
    }
}
