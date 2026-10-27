package sk.sivak.eldritchhorror.core.view.utils;

import rx.Completable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Short, preloaded cues for game events. Source credits: sounds/GAMEPLAY_AUDIO.md. */
public final class GameplaySounds {
    public enum Cue {
        CLUE_SPAWN("clue_spawn", .80f),
        EXPEDITION_SPAWN("expedition_spawn", .80f),
        CLUE_GAIN("clue_gain", .85f),
        CLUE_LAND("clue_land", .85f),
        FOCUS_GAIN("focus_gain", .78f),
        FOCUS_LAND("focus_land", .80f),
        RESOURCE_SPEND("resource_spend", .70f),
        TICKET_GAIN("ticket_gain", .85f),
        TICKET_LAND("ticket_land", .82f),
        TICKET_SPEND("ticket_spend", .78f),
        CARD_GAIN("card_gain", .75f),
        CARD_DISCARD("card_discard", .75f),
        GATE_OPEN("gate_open", .85f),
        GATE_CLOSE("gate_close", .85f),
        MONSTER_SPAWN("monster_spawn", .85f),
        EPIC_MONSTER_SPAWN("epic_monster_spawn", .95f),
        MONSTER_DEFEAT("monster_defeat", .85f),
        MYSTERY_PROGRESS("mystery_progress", .72f),
        MYSTERY_SOLVED("mystery_solved", .90f),
        SKILL_GAIN("skill_gain", .82f),
        SKILL_LOSS("skill_loss", .78f),
        HAZARD_SPAWN("hazard_spawn", .85f),
        INVESTIGATOR_SPAWN("investigator_spawn", .90f),
        INVESTIGATOR_DEFEAT("investigator_defeat", .90f),
        INVESTIGATOR_DEVOURED("investigator_devoured", .90f),
        INVESTIGATOR_DELAYED("investigator_delayed", .82f),
        INVESTIGATOR_READY("investigator_ready", .80f),
        INVESTIGATOR_DISAPPEAR("investigator_disappear", .85f),
        OMEN_CHANGE("omen_change", .85f),
        OMEN_TOKEN_ADD("omen_token_add", .85f),
        OMEN_TOKEN_REMOVE("omen_token_remove", .82f),
        DOOM_ADVANCE("doom_advance", .85f),
        DOOM_RETREAT("doom_retreat", .90f),
        RUMOR_COUNTDOWN("rumor_countdown", .82f),
        RUMOR_FAILURE("rumor_failure", .90f),
        RECKONING("reckoning", .85f),
        ANCIENT_POWER("ancient_power", .88f),
        ACTION_PHASE("action_phase", .75f),
        ENCOUNTER_PHASE("encounter_phase", .78f),
        MYTHOS_PHASE("mythos_phase", .78f);

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
