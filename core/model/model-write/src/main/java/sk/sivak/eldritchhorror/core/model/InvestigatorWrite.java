package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.Map;

/**
 * @author msivak
 */
public interface InvestigatorWrite extends InvestigatorRead {

    void setInfo(InvestigatorInfo info);

    void setCurrentHealth(int currentHealth);

    void setCurrentSanity(int currentSanity);

    void setLeadInvestigator(boolean leadInvestigator);

    void setCurrentLocationId(LocationId locationId);

    void setTrainTickets(int trainTickets);

    void setShipTickets(int shipTickets);

    void setFocusTokens(int focusTokens);

    void improveStat(Stat stat);

    void loseImprovement(Stat stat, int amount);

    void startDelayed();

    void stopDelayed();
}
