package sk.sivak.eldritchhorror.core.view.components.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class HideOkButtons {
    private TextButton hideButton;
    private TextButton okButton;
    private LabelTable labelTable;

    private Action0 onConfirmAction;
    private String displayLabelText;
    private Actor actorToHide;
    private boolean displayed;
    private ClickListener addedListener;

    public void init(String displayLabelText, Action0 onConfirmAction, Actor actorToHide) {
        this.displayLabelText = displayLabelText;
        this.onConfirmAction = onConfirmAction;
        this.actorToHide = actorToHide;
        this.displayed = true;
    }

    public void hide() {
        if (!isFullyDisplayed(hideButton)) {
            return;
        }
        prepareYesButton(hideButton, false).run();
    }

    public void hideInstantly() {
        hideButton = ButtonBuilder.buildButton("Hide");
        okButton = ButtonBuilder.buildButton("OK");

        addedListener = addClickListener(hideButton, this::hide);
        addClickListener(okButton, () -> {
            if (!isFullyDisplayed(hideButton)) {
                return;
            }
            if (okButton.isDisabled()) {
                return;
            }
            onConfirm();
            onConfirmAction.call();
        });
        okButton.getStyle().fontColor = new Color(0x008000ff);

        float bottomHeight = InfoStage.getBottomHeight();
        InfoStage.setBottomHeight(bottomHeight + okButton.getHeight() + 5);

        hideButton.setPosition(VIEWPORT_WIDTH / 2 - okButton.getWidth(), bottomHeight);
        okButton.setPosition(VIEWPORT_WIDTH / 2, bottomHeight);
        InfoStage.addActorToButtonLayer(hideButton);
        InfoStage.addActorToButtonLayer(okButton);

        if (!isFullyDisplayed(hideButton)) {
            return;
        }
        prepareYesButton(hideButton, true).run();
    }

    public void showButtons() {
        hideButton = ButtonBuilder.buildButton("Hide");
        okButton = ButtonBuilder.buildButton("OK");

        addedListener = addClickListener(hideButton, this::hide);
        addClickListener(okButton, () -> {
            if (!isFullyDisplayed(hideButton)) {
                return;
            }
            if (okButton.isDisabled()) {
                return;
            }
            onConfirm();
            onConfirmAction.call();
        });
        okButton.getStyle().fontColor = new Color(0x008000ff);

        float bottomHeight = InfoStage.getBottomHeight();
        InfoStage.setBottomHeight(bottomHeight + okButton.getHeight() + 5);

        InfoStage.addActionToInfoStage(new FastForwardAction<>(Actions.run(() -> {
            InfoStage.showButton(hideButton, VIEWPORT_WIDTH / 2 - okButton.getWidth(), bottomHeight, false);
            InfoStage.showButton(okButton, VIEWPORT_WIDTH / 2, bottomHeight, false);
        })));
    }

    private boolean isFullyDisplayed(Actor actor) {
        return actor.getColor().a == 1f;
    }

    public void alphaToValue(float value) {
        if (hideButton != null) {
            hideButton.addAction(new FastForwardAction<>(Actions.alpha(value, 1f)));
        }
        if (displayed) {
            if (okButton != null) {
                okButton.addAction(new FastForwardAction<>(Actions.alpha(value, 1f)));
            }
            actorToHide.addAction(new FastForwardAction<>(Actions.alpha(value, 1f)));
        } else {
            if (labelTable != null) {
                labelTable.addAction(new FastForwardAction<>(Actions.alpha(value, 1f)));
            }
        }
    }

    public void disableOkButton() {
        okButton.setDisabled(true);
    }

    public void enableOkButton() {
        okButton.setDisabled(false);
    }

    protected void onConfirm() {
        actorToHide.setTouchable(Touchable.disabled);
        InfoStage.hideActor(actorToHide);
        InfoStage.hideActor(okButton);
        InfoStage.hideActor(hideButton);
        InfoStage.setBottomHeight(5);
    }


    private Runnable prepareYesButton(TextButton button, boolean instant) {
        return () -> {
            if (!isFullyDisplayed(button)) {
                return;
            }
            displayed = false;
            actorToHide.setTouchable(Touchable.disabled);
            labelTable = LabelTable.createAndShowTable(0, displayLabelText);
            button.setText("Yes");
            button.setTouchable(Touchable.disabled);
            okButton.setTouchable(Touchable.disabled);
            if (instant) {
                actorToHide.getColor().a = 0;
                okButton.getColor().a = 0;
                button.setPosition(VIEWPORT_WIDTH / 2 - button.getWidth() / 2, button.getY());
                button.setTouchable(Touchable.enabled);
                button.removeListener(addedListener);
                addedListener = addClickListener(button, prepareHideButton(button));
            } else {
                actorToHide.addAction(new FastForwardAction<>(Actions.alpha(0, FAST_ACTION_DURATION)));
                okButton.addAction(new FastForwardAction<>(Actions.alpha(0, FAST_ACTION_DURATION)));
                button.addAction(new FastForwardAction<>(sequence(
                        moveTo(VIEWPORT_WIDTH / 2 - button.getWidth() / 2, button.getY(), FAST_ACTION_DURATION),
                        Actions.run(() -> {
                            button.setTouchable(Touchable.enabled);
                            button.removeListener(addedListener);
                            addedListener = addClickListener(button, prepareHideButton(button));
                        })
                )));
            }
        };
    }

    private Runnable prepareHideButton(TextButton button) {
        return () -> {
            if (!isFullyDisplayed(button)) {
                return;
            }
            displayed = true;
            actorToHide.setTouchable(Touchable.enabled);
            InfoStage.setBottomHeight(InfoStage.getBottomHeight() - labelTable.getHeight() - 5);
            labelTable.addAction(new FastForwardAction<>(sequence(
                    Actions.alpha(0, FAST_ACTION_DURATION),
                    Actions.run(() -> labelTable.remove())
            )));
            button.setText("Hide");
            button.setTouchable(Touchable.disabled);
            actorToHide.addAction(new FastForwardAction<>(Actions.alpha(1, FAST_ACTION_DURATION)));
            okButton.addAction(new FastForwardAction<>(Actions.alpha(1, FAST_ACTION_DURATION)));
            button.addAction(new FastForwardAction<>(sequence(
                    moveTo(VIEWPORT_WIDTH / 2 - okButton.getWidth(), button.getY(), FAST_ACTION_DURATION),
                    Actions.run(() -> {
                        button.setTouchable(Touchable.enabled);
                        okButton.setTouchable(Touchable.enabled);
                        button.removeListener(addedListener);
                        addedListener = addClickListener(button, prepareYesButton(button, false));
                    })
            )));
        };
    }

    protected TextButton getOkButton() {
        return okButton;
    }

    protected TextButton getHideButton() {
        return hideButton;
    }
}
