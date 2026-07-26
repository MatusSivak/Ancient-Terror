package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.draganddrop.impl.ColorUtils.createColorAction;

/**
 * @author msivak
 */
public class TargetActor extends HorizontalGroup {

    private Actor colorActor;
    private boolean docking;
    private CardTemplate preview;

    public TargetActor() {
        align(Align.left);
        colorActor = new Actor();
        createColorAction(colorActor, new Color(0x00cc0055), new Color(0x33ff3355));

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        colorActor.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(colorActor.getColor().r,
                colorActor.getColor().g,
                colorActor.getColor().b,
                colorActor.getColor().a * parentAlpha);
        batch.draw(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND), getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor superHit = super.hit(x, y, touchable);
        if (superHit != null) {
            return superHit;
        }
        if (x > 0 && x < getWidth() && y > 0 && y < getHeight()) {
            return this;
        }
        return null;
    }

    public void init(CardTemplate... cardTemplates) {
        if (cardTemplates.length == 0) {
            return;
        }
        setSize(cardTemplates[0].getPrefWidth() * cardTemplates.length,
                cardTemplates[0].getPrefHeight());
    }

    public List<CardTemplate> getCardTemplates() {
        List<CardTemplate> result = new LinkedList<>();
        for (Actor actor : getChildren()) {
            if (actor instanceof CardTemplate) {
                result.add((CardTemplate) actor);
            }
        }
        return result;
    }

    public boolean isDocking() {
        return docking;
    }

    public void setDocking(boolean docking) {
        this.docking = docking;
    }

    public CardTemplate getPreview() {
        return preview;
    }

    public void setPreview(CardTemplate preview) {
        this.preview = preview;
    }
}
