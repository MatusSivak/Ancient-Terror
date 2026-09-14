package sk.sivak.eldritchhorror.core.view.components.sheet.mystery;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class ProgressTokenBar extends Table {

    public static final int TOKEN_SIZE = 28;
    private static final int TOKENS_PER_ROW = 8;
    public static final float ACTION_DURATION = 1f;

    private Integer progress;

    public void init(int tokensCount, Integer progress) {
        this.progress = Math.max(0, Math.min(progress, tokensCount));

        align(Align.left);
        clearChildren();
        for (int i = 0; i < tokensCount; i++) {
            Container<ProgressToken> slot = new Container<>(new ProgressToken());
            slot.size(TOKEN_SIZE * 0.78f);
            add(slot).size(TOKEN_SIZE).padRight(4).padBottom(3);
            if ((i + 1) % TOKENS_PER_ROW == 0 && i + 1 < tokensCount) {
                row();
            }
        }
        for (int i = 0; i < this.progress; i++) {
            Container<ProgressToken> slot = (Container<ProgressToken>) getCells().get(i).getActor();
            slot.size(TOKEN_SIZE);
            slot.getActor().activateImmediately();
        }
    }

    public void activate() {
        if (getCells().size <= progress) {
            return;
        }
        Cell progressTokenCell = getCells().get(progress++);
        Container<ProgressToken> slot = (Container<ProgressToken>) progressTokenCell.getActor();
        ProgressToken progressToken = slot.getActor();
        progressToken.activate();
        MyTemporalAction action = new MyTemporalAction(slot);
        action.setDuration(ACTION_DURATION);
        action.setInterpolation(Interpolation.sine);
        progressToken.addAction(action);
    }

    private class MyTemporalAction extends TemporalAction {

        private final Container<ProgressToken> slot;

        public MyTemporalAction(Container<ProgressToken> slot) {
            this.slot = slot;
        }

        @Override
        protected void update(float percent) {
            float tokenSize = (0.78f + percent * (1 - 0.78f)) * TOKEN_SIZE;
            slot.size(tokenSize);
        }
    }
}
