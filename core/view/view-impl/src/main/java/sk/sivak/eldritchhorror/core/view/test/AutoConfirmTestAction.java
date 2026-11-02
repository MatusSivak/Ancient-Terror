package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import java8.features.function.Supplier;

/** Waits for an empty available row and a settled selection while fast-forward is held. */
final class AutoConfirmTestAction extends Action {
    private final Button button;
    private final Group availableCards;
    private final Group selectedCards;
    private final int cardCount;
    private final Supplier<Boolean> fastForwardHeld;
    private final Runnable confirm;
    private boolean confirmed;

    AutoConfirmTestAction(Button button, Group availableCards, Group selectedCards, int cardCount,
                          Supplier<Boolean> fastForwardHeld, Runnable confirm) {
        this.button = button;
        this.availableCards = availableCards;
        this.selectedCards = selectedCards;
        this.cardCount = cardCount;
        this.fastForwardHeld = fastForwardHeld;
        this.confirm = confirm;
    }

    @Override
    public boolean act(float delta) {
        if (confirmed) return true;
        if (button.isDisabled() || !button.isTouchable() || !fastForwardHeld.get()
                || availableCards.getChildren().size != 0 || selectedCards.getChildren().size != cardCount) {
            return false;
        }
        // Drag animations temporarily remove cards or replace them with invisible placeholders.
        for (Actor card : selectedCards.getChildren()) {
            if (!card.isTouchable() || card.getColor().a < 1f) return false;
        }
        confirmed = true;
        confirm.run();
        return true;
    }
}
