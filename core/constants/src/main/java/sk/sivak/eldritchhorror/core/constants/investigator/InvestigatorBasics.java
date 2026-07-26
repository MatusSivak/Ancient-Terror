package sk.sivak.eldritchhorror.core.constants.investigator;

import sk.sivak.eldritchhorror.core.constants.location.PathType;

import java.util.List;

public interface InvestigatorBasics extends InvestigatorInfo {

    int getCurrentHealth();

    int getCurrentSanity();

    List<PathType> getTickets();

    int getClues();

    int getFocusTokens();

    int getBonusStat(Stat stat);

    boolean isLostInTimeAndSpace();
}
