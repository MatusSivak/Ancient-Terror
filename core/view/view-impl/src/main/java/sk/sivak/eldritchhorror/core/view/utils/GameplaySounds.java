package sk.sivak.eldritchhorror.core.view.utils;

import rx.Completable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Short, preloaded cues for game events. Source credits: sounds/GAMEPLAY_AUDIO.md. */
public final class GameplaySounds {
    public enum Cue {
        CLUE_SPAWN("clue_spawn", .42f),
        CLUE_GAIN("clue_gain", .45f),
        FOCUS_GAIN("focus_gain", .40f),
        RESOURCE_SPEND("resource_spend", .35f),
        TICKET_GAIN("ticket_gain", .45f),
        TICKET_SPEND("ticket_spend", .40f),
        CARD_GAIN("card_gain", .40f),
        CARD_DISCARD("card_discard", .40f),
        GATE_OPEN("gate_open", .50f),
        GATE_CLOSE("gate_close", .50f),
        MONSTER_SPAWN("monster_spawn", .48f),
        EPIC_MONSTER_SPAWN("epic_monster_spawn", .58f),
        MONSTER_DEFEAT("monster_defeat", .48f),
        MYSTERY_PROGRESS("mystery_progress", .35f),
        MYSTERY_SOLVED("mystery_solved", .55f),
        SKILL_GAIN("skill_gain", .45f),
        SKILL_LOSS("skill_loss", .40f),
        HAZARD_SPAWN("hazard_spawn", .48f),
        INVESTIGATOR_DEFEAT("investigator_defeat", .55f),
        INVESTIGATOR_DEVOURED("investigator_devoured", .55f),
        MYTHOS_PHASE("mythos_phase", .40f);

        public final String path;
        public final float volume;

        Cue(String name, float volume) {
            this.path = "sounds/" + name + ".wav";
            this.volume = volume;
        }
    }

    private GameplaySounds() { }

    public static void play(Cue cue) {
        if (FastForwardAction.isOn()) return;
        // Never synchronously decode audio on the render thread. Missing cues stay silent.
        SoundPlayer.play(CustomAssetManager.getLoadedSound(cue.path), cue.volume, 1f, 0f);
    }

    /** The cue belongs to execution of the animation, not construction of the event queue. */
    public static Completable withCue(Cue cue, Completable animation) {
        return Completable.defer(() -> {
            play(cue);
            return animation;
        });
    }
}
