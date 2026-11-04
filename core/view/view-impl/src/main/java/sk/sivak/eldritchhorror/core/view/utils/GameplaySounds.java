package sk.sivak.eldritchhorror.core.view.utils;

import rx.Completable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Short, preloaded cues for game events. Source credits: sounds/GAMEPLAY_AUDIO.md. */
public final class GameplaySounds {
    public enum Cue {
        TOKEN_IMPACT("map_token_impact", .85f),
        INVESTIGATOR_IMPACT("map_investigator_impact", 1f),
        GATE_IMPACT("gate_resonance", 1f),
        TRAVEL_SEA("travel_sea", .90f),
        TRAVEL_RAIL("travel_rail", .90f),
        TRAVEL_WALK("travel_walk", .90f),
        CLUE_SPAWN("clue_spawn", .80f),
        EXPEDITION_SPAWN("expedition_spawn", .80f),
        CLUE_GAIN("clue_gain", .85f),
        CLUE_LAND("clue_land", .85f),
        FOCUS_GAIN("focus_gain", .60f),
        FOCUS_LAND("focus_land", .80f),
        RESOURCE_SPEND("resource_spend", .70f),
        DICE_SORT("dice_sort", .60f),
        // Unmodified downloads meant to be swapped freely; credits in sounds/DICE_SUCCESS_AUDIO.md.
        DICE_SUCCESS_COMBAT("dice_success_combat.wav", .80f),
        DICE_SUCCESS_ROLL("dice_success_roll.wav", .90f),
        DICE_SUCCESS_LORE("dice_success_lore.wav", .85f),
        DICE_SUCCESS_INFLUENCE("dice_success_influence.wav", .90f),
        DICE_SUCCESS_OBSERVATION("dice_success_observation.ogg", .45f),
        DICE_SUCCESS_STRENGTH("dice_success_strength.wav", .80f),
        DICE_SUCCESS_WILL("dice_success_will.ogg", .70f),
        TICKET_GAIN("ticket_gain", .85f),
        SANITY_GAIN("sanity_gain", .90f),
        TICKET_LAND("ticket_land", .82f),
        TICKET_SPEND("ticket_spend", .78f),
        CARD_GAIN("card_gain", .75f),
        CARD_COLLECT("card_collect", .38f),
        CARD_DISCARD("card_discard", .75f),
        CARD_DISABLE("card_disable", .80f),
        GATE_OPEN("gate_open", .85f),
        GATE_CLOSE("gate_close", .85f),
        MONSTER_SPAWN("monster_spawn", .85f),
        EPIC_MONSTER_SPAWN("epic_monster_spawn", .95f),
        MONSTER_DEFEAT("monster_defeat", .85f),
        MONSTER_DISCARD("monster_discard", .85f),
        RUMOR_SPAWN("rumor_spawn", .80f),
        MONSTER_SHEET_TEAR("monster_sheet_tear", .85f),
        MONSTER_SHEET_GROWL("monster_sheet_growl", .80f),
        EPIC_MONSTER_SHEET_ROAR("epic_monster_sheet_roar", .90f),
        MONSTER_DEFEAT_FLY("monster_defeat_fly", .85f),
        MYSTERY_PROGRESS("mystery_progress", .72f),
        MYSTERY_SOLVED("mystery_solved", .90f),
        SKILL_GAIN("skill_gain", .82f),
        SKILL_LOSS("skill_loss", .78f),
        HAZARD_SPAWN("hazard_spawn", .85f),
        INVESTIGATOR_SPAWN("investigator_spawn", .90f),
        INVESTIGATOR_DEFEAT("investigator_defeat", .90f),
        INVESTIGATOR_DEVOURED("investigator_devoured", .90f),
        INVESTIGATOR_DELAYED("investigator_delayed", .82f),
        INVESTIGATOR_DETAINED("investigator_detained", .85f),
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
        ACTION_PHASE("action_phase", .78f),
        ENCOUNTER_PHASE("encounter_phase", .78f),
        MYTHOS_PHASE("mythos_phase", .78f);

        public final String path;
        public final float volume;

        Cue(String name, float volume) {
            this.path = "sounds/" + name + (name.contains(".") ? "" : ".wav");
            this.volume = volume;
        }
    }

    private GameplaySounds() { }

    public static Cue travelCue(sk.sivak.eldritchhorror.core.constants.location.PathType type) {
        if (type == sk.sivak.eldritchhorror.core.constants.location.PathType.SHIP) return Cue.TRAVEL_SEA;
        if (type == sk.sivak.eldritchhorror.core.constants.location.PathType.TRAIN) return Cue.TRAVEL_RAIL;
        return Cue.TRAVEL_WALK;
    }

    /** Cue for a thrown die landing as a success: combat, the tested skill, or plain rolls (Reckonings...). */
    public static Cue diceSuccessCue(sk.sivak.eldritchhorror.core.constants.investigator.Stat stat, boolean combat) {
        if (combat) return Cue.DICE_SUCCESS_COMBAT;
        if (stat == null) return Cue.DICE_SUCCESS_ROLL;
        switch (stat) {
            case LORE: return Cue.DICE_SUCCESS_LORE;
            case INFLUENCE: return Cue.DICE_SUCCESS_INFLUENCE;
            case OBSERVATION: return Cue.DICE_SUCCESS_OBSERVATION;
            case STRENGTH: return Cue.DICE_SUCCESS_STRENGTH;
            case WILL: return Cue.DICE_SUCCESS_WILL;
            default: return null;
        }
    }

    public static void play(Cue cue) {
        if (FastForwardAction.isOn()) return;
        // Never synchronously decode audio on the render thread. Missing cues stay silent.
        SoundPlayer.play(CustomAssetManager.getLoadedSound(cue.path), cue.volume, 1f, 0f);
    }

    public static Cue monsterSheetCue(boolean epic) {
        return epic ? Cue.EPIC_MONSTER_SHEET_ROAR : Cue.MONSTER_SHEET_GROWL;
    }

    /** User gesture, so it stays audible during fast-forward. */
    public static void playMonsterVoice(boolean epic) {
        Cue cue = monsterSheetCue(epic);
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
