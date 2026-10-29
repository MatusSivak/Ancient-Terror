package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Dims the sheet around its live ability row without moving or redrawing any text. */
public final class MonsterAbilityHighlight extends Actor {
    private final MonsterCard card;
    private final Table rule;
    private final Vector2 bottomLeft = new Vector2();
    private final Vector2 topRight = new Vector2();
    private final Vector2 clipBottomLeft = new Vector2();
    private final Vector2 clipTopRight = new Vector2();

    public MonsterAbilityHighlight(MonsterCard card, Table rule) {
        this.card = card;
        this.rule = rule;
        setName("monster-ability-highlight");
        setTouchable(Touchable.disabled);
        card.validate();
        // Bring a scrolled ability into view while keeping its original layout and clipping.
        for (Actor ancestor = rule.getParent(); ancestor != card && ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor instanceof ScrollPane) {
                ScrollPane scroll = (ScrollPane) ancestor;
                Vector2 top = rule.localToAscendantCoordinates(scroll.getWidget(), new Vector2(0, rule.getHeight()));
                scroll.setScrollY(scroll.getWidget().getHeight() - top.y);
                scroll.updateVisualScroll();
                scroll.layout();
            }
        }
        card.addActor(this);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        if (!card.isAscendantOf(rule)) return;
        rule.localToAscendantCoordinates(card, bottomLeft.set(0, 0));
        rule.localToAscendantCoordinates(card, topRight.set(rule.getWidth(), rule.getHeight()));
        for (Actor ancestor = rule.getParent(); ancestor != card && ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor instanceof ScrollPane) {
                ancestor.localToAscendantCoordinates(card, clipBottomLeft.set(0, 0));
                ancestor.localToAscendantCoordinates(card, clipTopRight.set(ancestor.getWidth(), ancestor.getHeight()));
                bottomLeft.x = Math.max(bottomLeft.x, clipBottomLeft.x);
                bottomLeft.y = Math.max(bottomLeft.y, clipBottomLeft.y);
                topRight.x = Math.min(topRight.x, clipTopRight.x);
                topRight.y = Math.min(topRight.y, clipTopRight.y);
            }
        }
        float left = MathUtils.clamp(bottomLeft.x, 0, card.getWidth());
        float bottom = MathUtils.clamp(bottomLeft.y, 0, card.getHeight());
        float right = MathUtils.clamp(topRight.x, left, card.getWidth());
        float top = MathUtils.clamp(topRight.y, bottom, card.getHeight());
        float previous = batch.getPackedColor();
        Texture white = getTexture(PURE_WHITE_BACKGROUND);
        float alpha = getColor().a * parentAlpha;
        batch.setColor(0, 0, 0, .75f * alpha);
        batch.draw(white, 0, 0, card.getWidth(), bottom);
        batch.draw(white, 0, top, card.getWidth(), card.getHeight() - top);
        batch.draw(white, 0, bottom, left, top - bottom);
        batch.draw(white, right, bottom, card.getWidth() - right, top - bottom);
        if (right > left && top > bottom) {
            batch.setColor(.78f, .65f, .35f, alpha);
            batch.draw(white, left, bottom, right - left, 1);
            batch.draw(white, left, top - 1, right - left, 1);
            batch.draw(white, left, bottom, 1, top - bottom);
            batch.draw(white, right - 1, bottom, 1, top - bottom);
        }
        batch.setColor(previous);
    }
}
