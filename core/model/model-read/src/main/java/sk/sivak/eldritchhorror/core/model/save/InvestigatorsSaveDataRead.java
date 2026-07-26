package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;
import java.util.Map;

public interface InvestigatorsSaveDataRead {

    List<InvestigatorId> getAvailableInvestigators();

    List<? extends InvestigatorSaveDataRead> getSelectedInvestigators();

    List<InvestigatorId> getInvestigatorOrder();

    List<? extends DefeatedInvestigatorSaveDataRead> getDefeatedInvestigators();

    int getDefeatedOrDevouredCount();

    interface InvestigatorSaveDataRead {

        InvestigatorId getInvestigatorId();

        int getHealth();

        int getSanity();

        LocationId getLocationId();

        boolean isLeadInvestigator();

        int getTrainTickets();

        int getShipTickets();

        int getFocusTokens();

        boolean isDelayed();

        boolean isLostInTimeAndSpace();

        Map<String, Integer> getStatBonusMap();

        boolean isPassiveAbilityDisabled();
    }

    interface DefeatedInvestigatorSaveDataRead {
        InvestigatorId getInvestigatorId();

        boolean getDefeatedByHealth();

        LocationId getLocationId();

        int getTrainTickets();

        int getShipTickets();
    }
}
