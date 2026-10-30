package sk.sivak.eldritchhorror.core.view.question;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage;
import sk.sivak.eldritchhorror.core.view.test.TestInfoTable;
import sk.sivak.eldritchhorror.core.view.test.TestResultTable;
import sk.sivak.eldritchhorror.core.view.test.RollResultTable;
import java.util.ArrayList;
import java.util.List;

/** Temporarily occlude the pending roll without discarding its values or animation state. */
public final class TestPromptVisibility {
    private TestPromptVisibility() { }

    public static Runnable hide(Group root) {
        List<Actor> hidden = new ArrayList<>();
        hide(root, hidden);
        return () -> {
            for (Actor actor : hidden) if (actor.getStage() != null) actor.setVisible(true);
            hidden.clear();
        };
    }

    private static void hide(Group group, List<Actor> hidden) {
        for (Actor actor : group.getChildren()) {
            if (!actor.isVisible()) continue;
            if (actor instanceof DiceImage || actor instanceof TestResultTable || actor instanceof TestInfoTable || actor instanceof RollResultTable) {
                hidden.add(actor); actor.setVisible(false);
            } else if (actor instanceof Group) hide((Group) actor, hidden);
        }
    }
}
