package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;

/**
 * @author msivak
 */
public interface BasicActionService extends CommonService {

    /* Travel */

    void travel();

    void travelToLocation(LocationInfo.Connection location);

    void travelToLocation();

    void moveSpaces(TravelData travelData, int amount);

    void selectTravelTicketLocation();

    void loseTicket();

    /* Buy Travel Ticket */

    void buyTravelTicket();

    void gainTravelTicket(PathType pathType);

    void discardTicket();

    /* Rest */

    void discardTicket(PathType pathType);

    void rest();

    /* Focus*/

    void focus();

    void addFreeAction();

    void trade();

    void psychicAction(TradeData tradeData);

    void removePerformedAction(ActionPhaseAction actionPhaseAction);
}
