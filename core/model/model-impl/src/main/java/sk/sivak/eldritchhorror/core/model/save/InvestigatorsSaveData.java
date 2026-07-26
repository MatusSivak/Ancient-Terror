package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InvestigatorsSaveData implements SaveDataWrite.InvestigatorsSaveDataWrite {

    private List<InvestigatorId> availableInvestigators = new LinkedList<>();
    private List<? extends InvestigatorSaveDataRead> selectedInvestigators = new LinkedList<>();
    private List<? extends DefeatedInvestigatorSaveDataRead> defeatedInvestigators = new LinkedList<>();
    private List<InvestigatorId> investigatorOrder = new LinkedList<>();
    private int defeatedOrDevouredCount;

    public void setAvailableInvestigators(List<InvestigatorId> availableInvestigators) {
        this.availableInvestigators = availableInvestigators;
    }

    public void setSelectedInvestigators(List<? extends InvestigatorSaveDataRead> selectedInvestigators) {
        this.selectedInvestigators = selectedInvestigators;
    }

    public void setInvestigatorOrder(List<InvestigatorId> investigatorOrder) {
        this.investigatorOrder = investigatorOrder;
    }

    public void setDefeatedInvestigators(List<? extends DefeatedInvestigatorSaveDataRead> defeatedInvestigators) {
        this.defeatedInvestigators = defeatedInvestigators;
    }

    public void setDefeatedOrDevouredCount(int defeatedOrDevouredCount) {
        this.defeatedOrDevouredCount = defeatedOrDevouredCount;
    }

    @Override
    public List<InvestigatorId> getAvailableInvestigators() {
        return availableInvestigators;
    }

    @Override
    public List<? extends InvestigatorSaveDataRead> getSelectedInvestigators() {
        return selectedInvestigators;
    }

    @Override
    public List<InvestigatorId> getInvestigatorOrder() {
        return investigatorOrder;
    }

    @Override
    public List<? extends DefeatedInvestigatorSaveDataRead> getDefeatedInvestigators() {
        return defeatedInvestigators;
    }

    @Override
    public int getDefeatedOrDevouredCount() {
        return defeatedOrDevouredCount;
    }

    public static class InvestigatorSaveData implements InvestigatorSaveDataRead {
        private InvestigatorId investigatorId;
        private int health;
        private int sanity;
        private LocationId locationId;
        private boolean leadInvestigator;
        private int trainTickets;
        private int shipTickets;
        private int focusTokens;
        private boolean delayed;
        private boolean lostInTimeAndSpace;
        private boolean passiveAbilityDisabled;

        private Map<String, Integer> statBonusMap = new HashMap<>();

        public void setInvestigatorId(InvestigatorId investigatorId) {
            this.investigatorId = investigatorId;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public void setSanity(int sanity) {
            this.sanity = sanity;
        }

        public void setLocationId(LocationId locationId) {
            this.locationId = locationId;
        }

        public void setLeadInvestigator(boolean leadInvestigator) {
            this.leadInvestigator = leadInvestigator;
        }

        public void setTrainTickets(int trainTickets) {
            this.trainTickets = trainTickets;
        }

        public void setShipTickets(int shipTickets) {
            this.shipTickets = shipTickets;
        }

        public void setFocusTokens(int focusTokens) {
            this.focusTokens = focusTokens;
        }

        public void setDelayed(boolean delayed) {
            this.delayed = delayed;
        }

        public void setLostInTimeAndSpace(boolean lostInTimeAndSpace) {
            this.lostInTimeAndSpace = lostInTimeAndSpace;
        }

        public void setStatBonusMap(Map<String, Integer> statBonusMap) {
            this.statBonusMap = statBonusMap;
        }

        public void setPassiveAbilityDisabled(boolean passiveAbilityDisabled) {
            this.passiveAbilityDisabled = passiveAbilityDisabled;
        }

        @Override
        public InvestigatorId getInvestigatorId() {
            return investigatorId;
        }

        @Override
        public int getHealth() {
            return health;
        }

        @Override
        public int getSanity() {
            return sanity;
        }

        @Override
        public LocationId getLocationId() {
            return locationId;
        }

        @Override
        public boolean isLeadInvestigator() {
            return leadInvestigator;
        }

        @Override
        public int getTrainTickets() {
            return trainTickets;
        }

        @Override
        public int getShipTickets() {
            return shipTickets;
        }

        @Override
        public int getFocusTokens() {
            return focusTokens;
        }

        @Override
        public boolean isDelayed() {
            return delayed;
        }

        @Override
        public boolean isLostInTimeAndSpace() {
            return lostInTimeAndSpace;
        }

        @Override
        public Map<String, Integer> getStatBonusMap() {
            return statBonusMap;
        }

        @Override
        public boolean isPassiveAbilityDisabled() {
            return passiveAbilityDisabled;
        }
    }

    public static class DefeatedInvestigatorSaveData implements DefeatedInvestigatorSaveDataRead {
        private InvestigatorId investigatorId;
        private boolean defeatedByHealth;
        private LocationId locationId;
        private int trainTickets;
        private int shipTickets;

        @Override
        public InvestigatorId getInvestigatorId() {
            return investigatorId;
        }

        public void setInvestigatorId(InvestigatorId investigatorId) {
            this.investigatorId = investigatorId;
        }

        @Override
        public boolean getDefeatedByHealth() {
            return defeatedByHealth;
        }

        public void setDefeatedByHealth(boolean defeatedByHealth) {
            this.defeatedByHealth = defeatedByHealth;
        }

        @Override
        public LocationId getLocationId() {
            return locationId;
        }

        public void setLocationId(LocationId locationId) {
            this.locationId = locationId;
        }

        @Override
        public int getTrainTickets() {
            return trainTickets;
        }

        public void setTrainTickets(int trainTickets) {
            this.trainTickets = trainTickets;
        }

        @Override
        public int getShipTickets() {
            return shipTickets;
        }

        public void setShipTickets(int shipTickets) {
            this.shipTickets = shipTickets;
        }
    }
}
