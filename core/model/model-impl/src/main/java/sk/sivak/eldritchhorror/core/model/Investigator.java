package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.HashMap;
import java.util.Map;

import static java8.features.util.MapUtils.getOrDefault;

/**
 * @author msivak
 */
public class Investigator implements InvestigatorWrite {

    private InvestigatorInfo info;
    private int currentHealth;
    private int currentSanity;
    private LocationId currentLocationId;
    private boolean leadInvestigator;
    private int trainTickets = 0;
    private int shipTickets = 0;
    private int focusTokens = 0;
    private boolean delayed = false;
    private boolean passiveAbilityDisabled = false;
    private boolean lostInTimeAndSpace = false;
    private Map<Stat, Integer> statBonusMap = new HashMap<>();

    @Override
    public boolean isPassiveAbilityDisabled() {
        return passiveAbilityDisabled;
    }

    @Override
    public void setPassiveAbilityDisabled(boolean passiveAbilityDisabled) {
        this.passiveAbilityDisabled = passiveAbilityDisabled;
    }

    @Override
    public InvestigatorInfo getInfo() {
        return info;
    }

    @Override
    public void setInfo(InvestigatorInfo info) {
        this.info = info;
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    @Override
    public int getCurrentSanity() {
        return currentSanity;
    }

    @Override
    public void setCurrentSanity(int currentSanity) {
        this.currentSanity = currentSanity;
    }

    @Override
    public boolean isLeadInvestigator() {
        return leadInvestigator;
    }

    @Override
    public void setLeadInvestigator(boolean leadInvestigator) {
        this.leadInvestigator = leadInvestigator;
    }

    @Override
    public LocationId getCurrentLocationId() {
        return currentLocationId;
    }

    @Override
    public void setCurrentLocationId(LocationId locationId) {
        this.currentLocationId = locationId;
    }

    @Override
    public int getTrainTickets() {
        return trainTickets;
    }

    @Override
    public void setTrainTickets(int trainTickets) {
        this.trainTickets = trainTickets;
    }

    @Override
    public int getShipTickets() {
        return shipTickets;
    }

    @Override
    public void setShipTickets(int shipTickets) {
        this.shipTickets = shipTickets;
    }

    @Override
    public int getFocusTokens() {
        return focusTokens;
    }

    public void setFocusTokens(int focusTokens) {
        this.focusTokens = focusTokens;
    }

    @Override
    public int getStatBonus(Stat stat) {
        return getOrDefault(statBonusMap, stat, 0);
    }

    @Override
    public Map<Stat, Integer> getStatBonusMap() {
        return statBonusMap;
    }

    @Override
    public void improveStat(Stat stat) {
        statBonusMap.put(stat, getStatBonus(stat) + 1);
    }

    @Override
    public void loseImprovement(Stat stat, int amount) {
        Integer amountBefore = statBonusMap.get(stat);
        statBonusMap.put(stat, Math.max(0, amountBefore-amount));
    }

    @Override
    public void startDelayed() {
        delayed = true;
    }

    @Override
    public void stopDelayed() {
        delayed = false;
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
    public void setLostInTimeAndSpace(boolean lostInTimeAndSpace) {
        this.lostInTimeAndSpace = lostInTimeAndSpace;
    }
}
