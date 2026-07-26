package sk.sivak.eldritchhorror.core.view.components.sheet.mystery;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class ProgressTokenBar extends Table {

    public static final int TOKEN_SIZE = 38;
    public static final float ACTION_DURATION = 1f;

    private Integer progress;

    public void init(int tokensCount, Integer progress) {
        this.progress = progress;

        align(Align.left);
        clearChildren();
        for (int i = 0; i < tokensCount; i++) {
            add(new ProgressToken()).width(TOKEN_SIZE * 0.66f).height(TOKEN_SIZE * 0.66f).padRight(5).padBottom(5);
            if (tokensCount >= 10 && i == tokensCount/2 -1) {
                row();
            }
        }
        for (int i = 0; i < progress; i++) {
            ProgressToken progressToken = (ProgressToken) getCells().get(i).getActor();
            getCells().get(i).size(TOKEN_SIZE);
            progressToken.activateImmediately();
        }
    }

    public void activate() {
        if (getCells().size == progress) {
            return;
        }
        Cell progressTokenCell = getCells().get(progress++);
        ProgressToken progressToken = (ProgressToken) progressTokenCell.getActor();
        progressToken.activate();
        MyTemporalAction action = new MyTemporalAction(progressTokenCell);
        action.setDuration(ACTION_DURATION);
        action.setInterpolation(Interpolation.sine);
        progressToken.addAction(action);
    }

    private class MyTemporalAction extends TemporalAction {

        private final Cell progressTokenCell;

        public MyTemporalAction(Cell progressTokenCell) {
            this.progressTokenCell = progressTokenCell;
        }

        @Override
        protected void update(float percent) {
            float tokenSize = (0.66f + percent * (1 - 0.66f)) * TOKEN_SIZE;
            progressTokenCell.width(tokenSize).height(tokenSize);
            ProgressTokenBar.this.invalidate();
        }
    }
}
