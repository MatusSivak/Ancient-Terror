package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

public class TypewriterEffect {

    private TypewriterViewImpl typewriterView;

    TypewriterEffect(TypewriterViewImpl typewriterView) {
        this.typewriterView = typewriterView;
    }

    public Completable finishPaper() {
        return Completable.create(onSub -> typewriterView.getTable().addAction(new FastForwardAction(createFinishPaperAction(onSub))));
    }

    public Completable hidePaper() {
        return Completable.create(onSub -> typewriterView.getTable().addAction(new FastForwardAction(createHidePaperAction(onSub))));
    }

    public Completable showPaper() {
        return Completable.create(onSub -> typewriterView.getTable().addAction(new FastForwardAction(createShowPaperAction(onSub))));
    }

    private SequenceAction createFinishPaperAction(CompletableSubscriber onSub) {
        if (typewriterView.getTable().getParent() == null) {
            InfoStage.addBigActor(OnScreenActors.ActorKey.PAPER, typewriterView.getTable(), null, null);
        }
        Table table = typewriterView.getTable();
        return Actions.sequence(
                Actions.moveTo(table.getX(), ViewProperties.VIEWPORT_HEIGHT, .5f, Interpolation.sineIn),
                Actions.run(() -> {
                    table.clear();
                    BigActorsManager.unlock(BigActorsManager.BigActorKey.PAPER);
                    BigActorsManager.displayOrHidePaper();
                    BigActorsManager.unlock(BigActorsManager.BigActorKey.PAPER);
                    table.remove();
                    onSub.onCompleted();
                }));
    }

    private SequenceAction createHidePaperAction(CompletableSubscriber onSub) {
        Table table = typewriterView.getTable();
        typewriterView.updateTablePosition(new Vector2(table.getX(), table.getY()));
        return Actions.sequence(
                Actions.moveTo(table.getX(), -table.getHeight(), .5f, Interpolation.sineIn),
                Actions.run(() -> {
                    BigActorsManager.unlock(BigActorsManager.BigActorKey.PAPER);
                    BigActorsManager.displayOrHidePaper();
                    BigActorsManager.unlock(BigActorsManager.BigActorKey.PAPER);
                    table.remove();
                    onSub.onCompleted();
                })
        );
    }

    private SequenceAction createShowPaperAction(CompletableSubscriber onSub) {
        if (typewriterView.getTable().getParent() == null) {
            InfoStage.addBigActor(OnScreenActors.ActorKey.PAPER, typewriterView.getTable(), null, null);
        }
        if (typewriterView.getTablePosition() == null) {
            Table table = typewriterView.getTable();
            table.setPosition(ViewProperties.VIEWPORT_WIDTH - table.getWidth() - 40, - table.getHeight());
            InfoStage.addBigActor(OnScreenActors.ActorKey.PAPER, table, null, null);
            BigActorsManager.displayOrHidePaper();
            return Actions.sequence(
                    Actions.moveTo(
                            ViewProperties.VIEWPORT_WIDTH - table.getWidth() - 40,
                            ViewProperties.VIEWPORT_HEIGHT/2f - table.getHeight(),
                            .5f, Interpolation.sineIn),
                    Actions.run(() -> BigActorsManager.lock(BigActorsManager.BigActorKey.PAPER)),
                    Actions.run(onSub::onCompleted)
            );
        } else {
            return Actions.sequence(
                    Actions.moveTo(typewriterView.getTablePosition().x, typewriterView.getTablePosition().y, .5f, Interpolation.sineIn),
                    Actions.run(() -> {
                        BigActorsManager.lock(BigActorsManager.BigActorKey.PAPER);
                        BigActorsManager.setCurrentBigActorKey(BigActorsManager.BigActorKey.PAPER);
                    }),
                    Actions.run(onSub::onCompleted)
            );
        }
    }
}
