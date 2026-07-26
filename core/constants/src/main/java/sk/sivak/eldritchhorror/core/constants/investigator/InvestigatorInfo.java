package sk.sivak.eldritchhorror.core.constants.investigator;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public interface InvestigatorInfo {

    LocationId getLocationId();

    InvestigatorId getInvestigatorId();

    String getInvestigatorName();

    int getMaxHealth();

    int getMaxSanity();

    int getBaseStat(Stat stat);

    String getActionText();

    String getAbilityText();

    String getQuote();

    String getBio();
}
