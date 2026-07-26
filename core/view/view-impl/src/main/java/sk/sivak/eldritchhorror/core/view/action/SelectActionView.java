package sk.sivak.eldritchhorror.core.view.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.CountdownEventAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;

/**
 * @author msivak
 */
public class SelectActionView {

    private Skin skin;
    private GameController controller;

    public SelectActionView(GameController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
    }

    public Single<ActionPhaseAction> selectAction(List<ActionPhaseAction> actionPhaseActions) {
        return Single.create(onSub -> {
            if (BigActorsManager.isActionTableOnStage()) {
                Gdx.app.postRunnable(() -> {
                    onSub.onError(new IllegalArgumentException());
                });
                return;
            }
            Gdx.app.postRunnable(() -> {
                BigActorsManager.initActionsTable(actionPhaseActions, onSub);
                BigActorsManager.displayOrHideActionsTable();
            });
        });
    }
}
