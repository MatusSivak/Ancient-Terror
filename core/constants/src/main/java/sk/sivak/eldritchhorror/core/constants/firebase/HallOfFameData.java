package sk.sivak.eldritchhorror.core.constants.firebase;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;

import java.util.LinkedList;
import java.util.List;

public class HallOfFameData {
    private String name;
    private long timestamp = -1; // will be replaced
    private String difficulty;
    private int investigatorsCount;
    private AncientOneId ancientOneId;
    private List<String> investigators = new LinkedList<>();
    // Null means this legacy record did not capture investigator outcomes.
    private List<String> defeatedInvestigators;
    private int doom;
    private int rounds;
    private List<String> solvedMysteries = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getInvestigators() {
        return investigators;
    }

    public void setInvestigators(List<String> investigators) {
        this.investigators = investigators;
    }

    public List<String> getDefeatedInvestigators() {
        return defeatedInvestigators;
    }

    public void setDefeatedInvestigators(List<String> defeatedInvestigators) {
        this.defeatedInvestigators = defeatedInvestigators;
    }

    public int getDoom() {
        return doom;
    }

    public void setDoom(int doom) {
        this.doom = doom;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public List<String> getSolvedMysteries() {
        return solvedMysteries;
    }

    public void setSolvedMysteries(List<String> solvedMysteries) {
        this.solvedMysteries = solvedMysteries;
    }

    public void setInvestigatorsCount(int investigatorsCount) {
        this.investigatorsCount = investigatorsCount;
    }

    public AncientOneId getAncientOneId() {
        return ancientOneId;
    }

    public void setAncientOneId(AncientOneId ancientOneId) {
        this.ancientOneId = ancientOneId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getInvestigatorsCount() {
        return investigatorsCount;
    }
}
