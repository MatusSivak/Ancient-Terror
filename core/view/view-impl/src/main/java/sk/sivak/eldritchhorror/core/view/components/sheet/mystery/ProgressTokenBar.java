package sk.sivak.eldritchhorror.core.view.components.sheet.mystery;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** A readable count paired with a smoothly filled, segmented mystery track. */
public class ProgressTokenBar extends Table {
    public static final float ACTION_DURATION = 1.1f;

    private final Label counter;
    private int total;
    private int progress;
    private int shownCount = -1;
    private float displayedProgress;
    private float highlight;

    public ProgressTokenBar() {
        setTouchable(Touchable.disabled);
        setBackground(SelectionPanelStyle.panel("142523", "8E7953"));
        pad(10);
        counter = new Label("", new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.valueOf("F4E7C8")));
        counter.setFontScale(0.55f);
        counter.setAlignment(Align.center);
        add(new ProgressTrack()).minWidth(100).growX().height(22).padRight(12);
        add(counter).width(82).height(28);
    }

    public void init(int tokensCount, Integer progress) {
        clearActions();
        total = Math.max(0, tokensCount);
        this.progress = MathUtils.clamp(progress == null ? 0 : progress, 0, total);
        displayedProgress = this.progress;
        highlight = 0;
        shownCount = -1;
        updateCounter(this.progress);
    }

    public void activate() {
        if (progress >= total) {
            return;
        }
        // Multiple advances share one animation; interrupted fills resume smoothly.
        final float start = displayedProgress;
        final int targetProgress = ++progress;
        clearActions();
        addAction(new TemporalAction(ACTION_DURATION) {
            @Override
            protected void update(float percent) {
                displayedProgress = MathUtils.lerp(start, targetProgress, Interpolation.sine.apply(percent));
                highlight = MathUtils.sin(percent * MathUtils.PI);
                updateCounter(Math.min(targetProgress, (int) (displayedProgress + 0.0001f)));
            }

            @Override
            protected void end() {
                displayedProgress = targetProgress;
                highlight = 0;
                updateCounter(targetProgress);
            }
        });
    }

    private void updateCounter(int count) {
        if (shownCount != count) {
            counter.setText(count + " / " + total);
            shownCount = count;
        }
    }

    private class ProgressTrack extends Actor {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            Texture white = getTexture(PURE_WHITE_BACKGROUND);
            float previous = batch.getPackedColor();
            float alpha = getColor().a * parentAlpha;
            float x = getX(), y = getY(), width = getWidth(), height = getHeight();
            batch.setColor(0.50f, 0.47f, 0.35f, alpha);
            batch.draw(white, x, y, width, height);
            batch.setColor(0.035f, 0.065f, 0.065f, alpha);
            batch.draw(white, x + 1, y + 1, width - 2, height - 2);
            float innerWidth = width - 4;
            float fill = total == 0 ? 0 : innerWidth * displayedProgress / total;
            if (fill > 0) {
                batch.setColor(0.36f + highlight * 0.15f, 0.68f + highlight * 0.13f,
                        0.57f + highlight * 0.12f, alpha);
                batch.draw(white, x + 2, y + 2, fill, height - 4);
                batch.setColor(0.85f, 0.91f, 0.68f, alpha * (0.35f + highlight * 0.45f));
                batch.draw(white, x + 2, y + height - 5, fill, 3);
                batch.setColor(0.95f, 0.95f, 0.77f, alpha * highlight);
                batch.draw(white, x + 2 + fill - Math.min(3, fill), y + 2,
                        Math.min(3, fill), height - 4);
            }
            // Keep individual steps legible without crowding large objectives.
            if (total > 1 && total <= 24) {
                batch.setColor(0.08f, 0.14f, 0.13f, alpha);
                for (int i = 1; i < total; i++) {
                    batch.draw(white, x + 2 + innerWidth * i / total - 1, y + 1, 2, height - 2);
                }
            }
            batch.setColor(previous);
        }
    }
}