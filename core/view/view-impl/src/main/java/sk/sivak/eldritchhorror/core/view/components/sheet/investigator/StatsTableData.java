package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class StatsTableData {
    private int lore;
    private int influence;
    private int observation;
    private int strength;
    private int will;

    private int loreBonus;
    private int influenceBonus;
    private int observationBonus;
    private int strengthBonus;
    private int willBonus;

    private StatsTableData() {
    }

    public int getLore() {
        return lore;
    }

    public int getInfluence() {
        return influence;
    }

    public int getObservation() {
        return observation;
    }

    public int getStrength() {
        return strength;
    }

    public int getWill() {
        return will;
    }

    public int getLoreBonus() {
        return loreBonus;
    }

    public int getInfluenceBonus() {
        return influenceBonus;
    }

    public int getObservationBonus() {
        return observationBonus;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public int getWillBonus() {
        return willBonus;
    }

    public static class Builder {
        private StatsTableData data;

        public Builder() {
            this.data = new StatsTableData();
        }

        public Builder lore(int lore) {
            data.lore = lore;
            return this;
        }

        public Builder loreBonus(int loreBonus) {
            data.loreBonus = loreBonus;
            return this;
        }

        public Builder influence(int influence) {
            data.influence = influence;
            return this;
        }

        public Builder influenceBonus(int influenceBonus) {
            data.influenceBonus = influenceBonus;
            return this;
        }

        public Builder observation(int observation) {
            data.observation = observation;
            return this;
        }

        public Builder observationBonus(int observationBonus) {
            data.observationBonus = observationBonus;
            return this;
        }

        public Builder strength(int strength) {
            data.strength = strength;
            return this;
        }

        public Builder strengthBonus(int strengthBonus) {
            data.strengthBonus = strengthBonus;
            return this;
        }

        public Builder will(int will) {
            data.will = will;
            return this;
        }

        public Builder willBonus(int willBonus) {
            data.willBonus = willBonus;
            return this;
        }

        public StatsTableData build() {
            return data;
        }

        public Builder fromInvestigatorBasics(InvestigatorBasics investigatorBasics) {
            data.lore = investigatorBasics.getBaseStat(Stat.LORE);
            data.influence = investigatorBasics.getBaseStat(Stat.INFLUENCE);
            data.observation = investigatorBasics.getBaseStat(Stat.OBSERVATION);
            data.strength = investigatorBasics.getBaseStat(Stat.STRENGTH);
            data.will = investigatorBasics.getBaseStat(Stat.WILL);

            data.loreBonus = investigatorBasics.getBonusStat(Stat.LORE);
            data.influenceBonus = investigatorBasics.getBonusStat(Stat.INFLUENCE);
            data.observationBonus = investigatorBasics.getBonusStat(Stat.OBSERVATION);
            data.strengthBonus = investigatorBasics.getBonusStat(Stat.STRENGTH);
            data.willBonus = investigatorBasics.getBonusStat(Stat.WILL);
            return this;
        }
    }
}
