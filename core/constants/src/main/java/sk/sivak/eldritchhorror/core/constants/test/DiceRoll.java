package sk.sivak.eldritchhorror.core.constants.test;

public class DiceRoll {

    private int diceNr;
    private int diceValue;
    private Score score;

    public DiceRoll() {
    }

    public DiceRoll(Score score) {
        this.score = score;
    }

    public int getDiceNr() {
        return diceNr;
    }

    public void setDiceNr(int diceNr) {
        this.diceNr = diceNr;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public enum Score {
        BAD(0),
        GOOD(1),
        VERY_GOOD(2); // each 6 you roll counts as 2 successes

        private int value;

        Score(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
