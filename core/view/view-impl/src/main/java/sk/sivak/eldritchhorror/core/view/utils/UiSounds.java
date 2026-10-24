package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager.BigActorKey;
import sk.sivak.eldritchhorror.core.view.components.hud.HudButton;

/** UI gestures stay audible during fast-forward; automatic panel animations do not. */
public final class UiSounds {
    public enum Cue {
        BUTTON_CLICK("button_click", .75f), HUD_CLICK("hud_click", .80f),
        CARD_OPEN("card_open", .80f), CARD_CLOSE("card_close", .72f),
        SHEET_OPEN("sheet_open", .80f), SHEET_CLOSE("sheet_close", .72f),
        MYSTERY_OPEN("mystery_open", .82f), MYSTERY_CLOSE("mystery_close", .72f),
        ANCIENT_OPEN("ancient_open", .85f), ANCIENT_CLOSE("ancient_close", .75f),
        POPUP_OPEN("popup_open", .70f), POPUP_CLOSE("popup_close", .65f);

        public final String path;
        public final float volume;

        Cue(String name, float volume) {
            path = "sounds/ui_" + name + ".wav";
            this.volume = volume;
        }
    }

    private static int inputDepth;

    private UiSounds() { }

    public static void play(Cue cue) {
        SoundPlayer.play(CustomAssetManager.getLoadedSound(cue.path), cue.volume, 1f, 0f);
    }

    public static void transition(Cue cue) {
        if (!FastForwardAction.isOn() || inputDepth > 0) play(cue);
    }

    public static void userAction(Runnable action) {
        inputDepth++;
        try {
            action.run();
        } finally {
            inputDepth--;
        }
    }

    public static Cue sheetCue(BigActorKey key, boolean opening) {
        if (key == BigActorKey.ANCIENT_ONE) return opening ? Cue.ANCIENT_OPEN : Cue.ANCIENT_CLOSE;
        if (key == BigActorKey.MYSTERY_CARD) return opening ? Cue.MYSTERY_OPEN : Cue.MYSTERY_CLOSE;
        if (key == BigActorKey.MONSTER_CARD || key == BigActorKey.RUMOR_CARD)
            return opening ? Cue.CARD_OPEN : Cue.CARD_CLOSE;
        return opening ? Cue.SHEET_OPEN : Cue.SHEET_CLOSE;
    }

    /** Safe to call from both a button factory and its action binding. No ChangeEvent sounds. */
    public static void attachButton(Button button) {
        if (clickListener(button) == null) button.addCaptureListener(new ButtonSoundListener(button));
    }

    public static void attachButtons(Actor actor) {
        if (actor instanceof Button) attachButton((Button) actor);
        if (actor instanceof Group) {
            for (Actor child : ((Group) actor).getChildren()) attachButtons(child);
        }
    }

    private static ButtonSoundListener clickListener(Actor actor) {
        for (EventListener listener : actor.getCaptureListeners()) {
            if (listener instanceof ButtonSoundListener) return (ButtonSoundListener) listener;
        }
        return null;
    }

    private static final class ButtonSoundListener extends ClickListener {
        private final Button owner;

        ButtonSoundListener(Button owner) { this.owner = owner; }

        @Override public void clicked(InputEvent event, float x, float y) {
            if (owner.isDisabled() || (owner instanceof HudButton && ((HudButton) owner).looksDisabled())) return;
            for (Actor actor = owner; actor != null; actor = actor.getParent()) {
                if (!actor.isVisible() || actor.getTouchable() == Touchable.disabled) return;
            }
            // Nested buttons bubble through their parents; only the closest control clicks.
            for (Actor actor = event.getTarget(); actor != null && actor != owner; actor = actor.getParent()) {
                if (clickListener(actor) != null) return;
            }
            play(owner instanceof HudButton ? Cue.HUD_CLICK : Cue.BUTTON_CLICK);
        }
    }
}
