package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

class GridActionHighlight extends Actor {
    private static final int GLOW_STEPS = 12;
    private final Drawable drawable;

    GridActionHighlight(Drawable drawable) {
        this.drawable = drawable;
        setTouchable(Touchable.disabled);
        setVisible(false);
    }

    void show(GridMove move, float cellSize, float originX, float originY) {
        boolean row = move.getType() == GridMoveType.ROW_LEFT || move.getType() == GridMoveType.ROW_RIGHT;
        if (row) {
            setBounds(originX, originY + (GridBoard.SIZE - 1 - move.getIndex()) * cellSize,
                    GridBoard.SIZE * cellSize, cellSize);
        } else {
            setBounds(originX + move.getIndex() * cellSize, originY, cellSize, GridBoard.SIZE * cellSize);
        }
        flash();
    }

    void show(GridPosition position, float cellSize, float originX, float originY) {
        setBounds(originX + position.getColumn() * cellSize,
                originY + (GridBoard.SIZE - 1 - position.getRow()) * cellSize, cellSize, cellSize);
        flash();
    }

    private void flash() {
        clearActions();
        setColor(1f, 0.02f, 0.04f, 1f);
        setVisible(true);
        addAction(Actions.sequence(
                Actions.delay(0.15f),
                Actions.alpha(0f, 0.35f, Interpolation.fade),
                Actions.visible(false)
        ));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float previousColor = batch.getPackedColor();
        Color color = getColor();
        float opacity = color.a * parentAlpha;
        float glowWidth = Math.min(getWidth(), getHeight()) * 0.05f;
        float stripWidth = glowWidth / GLOW_STEPS;

        batch.setColor(color.r, color.g, color.b, 0.25f * opacity);
        drawable.draw(batch, getX(), getY(), getWidth(), getHeight());
        for (int i = 0; i < GLOW_STEPS; i++) {
            float inset = i * stripWidth;
            float strength = 1f - Math.abs(2f * (i + 0.5f) / GLOW_STEPS - 1f);
            float width = getWidth() - inset * 2f;
            float sideHeight = getHeight() - (inset + stripWidth) * 2f;
            batch.setColor(color.r, color.g, color.b, 0.70f * strength * opacity);
            drawable.draw(batch, getX() + inset, getY() + inset, width, stripWidth);
            drawable.draw(batch, getX() + inset, getTop() - inset - stripWidth, width, stripWidth);
            drawable.draw(batch, getX() + inset, getY() + inset + stripWidth, stripWidth, sideHeight);
            drawable.draw(batch, getRight() - inset - stripWidth, getY() + inset + stripWidth, stripWidth, sideHeight);
        }
        batch.setColor(previousColor);
    }

    void hide() {
        clearActions();
        setVisible(false);
    }
}
