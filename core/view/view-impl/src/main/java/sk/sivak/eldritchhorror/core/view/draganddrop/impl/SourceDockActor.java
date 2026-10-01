package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;

import static sk.sivak.eldritchhorror.core.view.draganddrop.impl.ColorUtils.createColorAction;
import static sk.sivak.eldritchhorror.core.view.draganddrop.impl.ColorUtils.drawZone;

/**
 * @author msivak
 */
public class SourceDockActor extends HorizontalGroup {

    private Actor colorActor;
    private CardTemplate preview;

    public SourceDockActor() {
        align(Align.bottomLeft);
        colorActor = new Actor();
        createColorAction(colorActor, new Color(0xb5655a66), new Color(0xd47a6ccc));
    }

    public void init(CardTemplate... cardTemplates) {
        for (CardTemplate cardTemplate : cardTemplates) {
            addActor(cardTemplate);
        }

        if (cardTemplates.length == 0) {
            return;
        }
        setSize(cardTemplates[0].getPrefWidth() * cardTemplates.length,
                cardTemplates[0].getPrefHeight());
    }

    public CardTemplate getPreview() {
        return preview;
    }

    public void setPreview(CardTemplate preview) {
        this.preview = preview;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        colorActor.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawZone(batch, colorActor.getColor(), parentAlpha, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }
}
