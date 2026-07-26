package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class ButtonUtils {

    public static ClickListener addClickListener(Actor actor, Runnable action) {
        ClickListener listener = new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        };
        actor.addListener(listener);
        return listener;
    }

    public static Button[] buildNoYesButtons(float delay, Runnable noAction, Runnable yesAction) {
        Button[] buttons = new Button[2];
        TextButton noButton = ButtonBuilder.buildButton("No");
        TextButton yesButton = ButtonBuilder.buildButton("Yes");

        buttons[0] = noButton;
        buttons[1] = yesButton;

        addClickListener(noButton, noAction);
        addClickListener(yesButton, yesAction);

        noButton.getStyle().fontColor = new Color(0x800000ff);
        yesButton.getStyle().fontColor = new Color(0x008000ff);

        float bottomHeight = InfoStage.getBottomHeight();
        InfoStage.setBottomHeight(bottomHeight + noButton.getHeight() + 5);



        InfoStage.addActionToInfoStage(new FastForwardAction(Actions.sequence(Actions.delay(delay), Actions.run(() -> {
            InfoStage.showButton(noButton, VIEWPORT_WIDTH / 2 - yesButton.getWidth(), bottomHeight);
            InfoStage.showButton(yesButton, VIEWPORT_WIDTH / 2, bottomHeight);
        }))));
        return buttons;
    }

}
