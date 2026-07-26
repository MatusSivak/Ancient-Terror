package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.model.save.ModelSaveDataRead;

import java.util.Random;

public interface DiceRollSimulator {
    int rollDie();
    ModelSaveDataRead.Difficulty getDifficulty();

    class NormalDiceRoller implements DiceRollSimulator {

        private final Random rng;

        public NormalDiceRoller() {
            rng = new Random();
        }



        @Override
        public int rollDie() {
            return rng.nextInt(6)+1;
        }

        @Override
        public ModelSaveDataRead.Difficulty getDifficulty() {
            return ModelSaveDataRead.Difficulty.NORMAL;
        }
    }

    class EasyDiceRoller implements DiceRollSimulator {

        private final Random rng;

        public EasyDiceRoller() {
            rng = new Random();
        }

        @Override
        public int rollDie() {
            float value = rng.nextFloat();
            if (value < 0.1) {
                return 1;
            } else if (value < 0.2) {
                return 2;
            } else if (value < 0.3) {
                return 3;
            } else if (value < 0.5) {
                return 4;
            } else if (value < 0.75) {
                return 5;
            } else {
                return 6;
            }
        }

        @Override
        public ModelSaveDataRead.Difficulty getDifficulty() {
            return ModelSaveDataRead.Difficulty.EASY;
        }
    }

}
