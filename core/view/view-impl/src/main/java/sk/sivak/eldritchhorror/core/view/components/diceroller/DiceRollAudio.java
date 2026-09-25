package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import java.util.function.Function;

/** Shared single-die recordings, independent playback voices for every die. */
final class DiceRollAudio {
    private final Sound[] impacts = new Sound[3];

    DiceRollAudio(Function<String, Sound> loader) {
        for (int i = 0; i < impacts.length; i++) {
            impacts[i] = loader.apply("sounds/dice_roll_" + (i + 1) + ".ogg");
        }
    }

    Roll newRoll(int diceCount) {
        return new Roll(MathUtils.random(2), MathUtils.random(0.90f, 1.12f),
                0.5f / (float)Math.sqrt(Math.max(1, diceCount)));
    }

    final class Roll {
        private final int variation;
        private final float pitch;
        private final float volume;
        private boolean played;

        private Roll(int variation, float pitch, float volume) {
            this.variation = variation;
            this.pitch = pitch;
            this.volume = volume;
        }

        void update(float progress, float pan) {
            if (played || progress < DiceThrowMotion.impactTime(0)) return;
            played = true;
            // Each recording already includes the small rebounds of ONE die.
            // Start it at first contact, never replay a whole roll at every bounce.
            sk.sivak.eldritchhorror.core.view.utils.SoundPlayer.play(impacts[variation],
                    volume, pitch,
                    MathUtils.clamp(pan, -0.65f, 0.65f), false);
        }
    }
}
