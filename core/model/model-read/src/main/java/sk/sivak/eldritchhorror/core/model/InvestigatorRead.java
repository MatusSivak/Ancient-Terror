package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.Map;

/**
 * @author msivak
 */
public interface InvestigatorRead {

    boolean isPassiveAbilityDisabled();

    void setPassiveAbilityDisabled(boolean passiveAbilityDisabled);

    InvestigatorInfo getInfo();

    int getCurrentHealth();

    int getCurrentSanity();

    boolean isLeadInvestigator();

    LocationId getCurrentLocationId();

    int getTrainTickets();

    int getShipTickets();

    int getFocusTokens();

    int getStatBonus(Stat stat);

    Map<Stat, Integer> getStatBonusMap();

    boolean isDelayed();

    boolean isLostInTimeAndSpace();

    void setLostInTimeAndSpace(boolean lostInTimeAndSpace);
}
