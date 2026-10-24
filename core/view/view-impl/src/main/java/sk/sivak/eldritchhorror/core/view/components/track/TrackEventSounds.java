package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;

/** Cues follow the clock's individual steps, with fast-forward checked at each step. */
final class TrackEventSounds {
    private TrackEventSounds() { }

    static ParallelAction doomSteps(int before, int after, float secondsPerStep) {
        after = Math.max(0, after);
        ParallelAction sounds = Actions.parallel();
        GameplaySounds.Cue cue = after < before ? GameplaySounds.Cue.DOOM_ADVANCE : GameplaySounds.Cue.DOOM_RETREAT;
        for (int step = 0; step < Math.abs(after - before); step++) {
            sounds.addAction(Actions.sequence(Actions.delay(step * secondsPerStep),
                    Actions.run(() -> GameplaySounds.play(cue))));
        }
        return sounds;
    }

    static void omenChanged(OmenId before, OmenId after) {
        if (before != after) GameplaySounds.play(GameplaySounds.Cue.OMEN_CHANGE);
    }
}
