package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.scenes.scene2d.Action;
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
}
