package sk.sivak.eldritchhorror.core.controller;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.PathType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author msivak
 */
public class InvestigatorBasicsImpl implements InvestigatorBasics {

    private InvestigatorId investigatorId;
    private LocationId locationId;
    private String name;
    private int maxHealth;
    private int maxSanity;
    private int currentHealth;
    private int currentSanity;
    private List<PathType> tickets = new LinkedList<>();
    private int clues;
    private int focusTokens;
    private Map<Stat, Integer> statMap = new HashMap<>();
    private Map<Stat, Integer> statBonusMap = new HashMap<>();
    private String actionText;
    private String abilityText;
    private String bio;
    private boolean lostInTimeAndSpace;

    public InvestigatorBasicsImpl(InvestigatorId investigatorId, LocationId locationId) {
        this.investigatorId = investigatorId;
        this.locationId = locationId;
    }

    @Override
    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    @Override
    public String getInvestigatorName() {
        return name;
    }

    public void setInvestigatorName(String name) {
        this.name = name;
    }

    @Override
    public LocationId getLocationId() {
        return locationId;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    @Override
    public int getMaxSanity() {
        return maxSanity;
    }

    @Override
    public int getBaseStat(Stat stat) {
        return statMap.get(stat);
    }

    @Override
    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    @Override
    public String getAbilityText() {
        return abilityText;
    }

    @Override
    public String getQuote() {
        return null;
    }

    @Override
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAbilityText(String abilityText) {
        this.abilityText = abilityText;
    }

    @Override
    public int getBonusStat(Stat stat) {
        return statBonusMap.get(stat);
    }

    @Override
    public boolean isLostInTimeAndSpace() {
        return lostInTimeAndSpace;
    }

    public void setLostInTimeAndSpace(boolean lostInTimeAndSpace) {
        this.lostInTimeAndSpace = lostInTimeAndSpace;
    }

    public void setStatValue(Stat stat, int baseValue, int bonusValue) {
        statBonusMap.put(stat, bonusValue);
        statMap.put(stat, baseValue);
    }


    public void setMaxSanity(int maxSanity) {
        this.maxSanity = maxSanity;
    }

    @Override
    public int getCurrentSanity() {
        return currentSanity;
    }

    public void setCurrentSanity(int currentSanity) {
        this.currentSanity = currentSanity;
    }

    @Override
    public List<PathType> getTickets() {
        return tickets;
    }

    @Override
    public int getClues() {
        return clues;
    }

    public void setClues(int clues) {
        this.clues = clues;
    }

    @Override
    public int getFocusTokens() {
        return focusTokens;
    }

    public void setFocusTokens(int focusTokens) {
        this.focusTokens = focusTokens;
    }
}
