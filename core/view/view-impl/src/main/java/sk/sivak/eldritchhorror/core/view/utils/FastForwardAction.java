package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.rafaskoberg.gdx.typinglabel.TypingConfig;
import sk.sivak.eldritchhorror.core.view.game.FastForwardButton;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

public class FastForwardAction<T extends Action> extends DelegateAction {
    private static Float fastForwardSpeed = 1f;

    private static boolean on;

    public FastForwardAction(T action) {
        setAction(action);
        InfoStage.getFastForwardButton().registerNewAction(this);
    }

    public T getTypedAction() {
        return (T)getAction();
    }

    /** Detached before finishing (cleared / discarded actor): stop tracking it, or it would be kept alive forever. */
    @Override
    public void setActor(Actor actor) {
        if (actor == null) {
            FastForwardButton fastForwardButton = InfoStage.getFastForwardButton();
            if (fastForwardButton != null) {
                fastForwardButton.unregisterAction(this);
            }
        }
        super.setActor(actor);
    }

    protected boolean delegate (float delta) {
        if (action == null) {
            return true;
        }
        if (action.act(delta * fastForwardSpeed)) {
            FastForwardButton fastForwardButton = InfoStage.getFastForwardButton();
            fastForwardButton.unregisterAction(this);
            return true;
        } else {
            InfoStage.getFastForwardButton().updateActionActivity(this);
            return false;
        }
    }

    public static void turnOn() {
        fastForwardSpeed = 20f;
        on = true;
    }

    public static void turnOff() {
        fastForwardSpeed = 1f;
        on = false;
    }

    public static boolean isOn() {
        return on;
    }

    public static boolean isHeld() {
        FastForwardButton fastForwardButton = InfoStage.getFastForwardButton();
        return fastForwardButton != null && fastForwardButton.isHeld();
    }

    /** Keep fast-forward available while a highlighted sheet waits for acknowledgement. */
    public static void autoConfirmHighlightWhileHeld(Actor actor, Runnable confirm,
                                                    java.util.function.BooleanSupplier confirmed) {
        actor.addAction(new FastForwardAction<>(highlightConfirmation(actor, confirm, confirmed,
                FastForwardAction::isHeld)));
    }

    static Action highlightConfirmation(Actor actor, Runnable confirm,
                                        java.util.function.BooleanSupplier confirmed,
                                        java.util.function.BooleanSupplier held) {
        return new Action() {
            @Override public boolean act(float delta) {
                if (confirmed.getAsBoolean()) return true;
                if (actor.getStage() == null || !actor.isTouchable() || !actor.isVisible()) return false;
                if (!held.getAsBoolean()) return false;
                confirm.run();
                return true;
            }
        };
    }

    /**
     * Confirms a plain OK (nothing to choose) if fast-forward is already held when the button becomes
     * clickable. Pressing fast-forward later, while the button waits, does not count as pressing it.
     */
    public static void autoConfirmWhileHeld(Button button, Runnable confirm) {
        autoConfirmWhileHeld(button, confirm, FastForwardAction::isHeld, false);
    }

    /** An opted-in reroll can also be accepted by starting to hold fast-forward while its prompt waits. */
    public static void autoAcceptRerollWhileHeld(Button button, Runnable confirm) {
        autoConfirmWhileHeld(button, confirm, FastForwardAction::isHeld, true);
    }

    static void autoConfirmWhileHeld(Button button, Runnable confirm,
                                     java.util.function.BooleanSupplier held, boolean waitForHold) {
        button.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                if (button.getStage() == null || !button.isTouchable() || button.isDisabled()) return false;
                if (!held.getAsBoolean()) return !waitForHold;
                confirm.run();
                return true;
            }
        });
    }
}
