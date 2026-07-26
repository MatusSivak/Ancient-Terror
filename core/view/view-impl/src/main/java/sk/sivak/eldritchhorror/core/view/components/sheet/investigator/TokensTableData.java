package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.location.PathType;

public class TokensTableData {
    private int currentHealth;
    private int maxHealth;

    private int currentSanity;
    private int maxSanity;

    private int focus;
    private int clue;

    private int ship;
    private int train;

    private TokensTableData() {
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentSanity() {
        return currentSanity;
    }

    public int getMaxSanity() {
        return maxSanity;
    }

    public int getFocus() {
        return focus;
    }

    public int getClue() {
        return clue;
    }

    public int getShip() {
        return ship;
    }

    public int getTrain() {
        return train;
    }

    public static class Builder {
        private TokensTableData data;

        public Builder() {
            this.data = new TokensTableData();
        }

        public Builder fromInvestigatorBasics(InvestigatorBasics investigatorBasics) {
            data.currentHealth = investigatorBasics.getCurrentHealth();
            data.maxHealth = investigatorBasics.getMaxHealth();
            data.currentSanity = investigatorBasics.getCurrentSanity();
            data.maxSanity = investigatorBasics.getMaxSanity();
            data.focus = investigatorBasics.getFocusTokens();
            data.clue = investigatorBasics.getClues();

            int ship = 0;
            int train = 0;
            for (PathType pathType : investigatorBasics.getTickets()) {
                if (PathType.SHIP == pathType) {
                    ship++;
                    continue;
                }
                if (PathType.TRAIN == pathType) {
                    train++;
                }
            }
            data.ship = ship;
            data.train = train;
            return this;
        }

        public Builder currentHealth(int currentHealth) {
            data.currentHealth = currentHealth;
            return this;
        }

        public Builder maxHealth(int maxHealth) {
            data.maxHealth = maxHealth;
            return this;
        }

        public Builder currentSanity(int currentSanity) {
            data.currentSanity = currentSanity;
            return this;
        }

        public Builder maxSanity(int maxSanity) {
            data.maxSanity = maxSanity;
            return this;
        }

        public Builder focus(int focus) {
            data.focus = focus;
            return this;
        }

        public Builder clue(int clue) {
            data.clue = clue;
            return this;
        }

        public Builder ship(int ship) {
            data.ship = ship;
            return this;
        }

        public Builder train(int train) {
            data.train = train;
            return this;
        }

        public TokensTableData build() {
            return data;
        }
    }
}
