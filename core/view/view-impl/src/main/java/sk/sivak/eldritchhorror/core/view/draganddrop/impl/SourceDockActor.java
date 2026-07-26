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

/**
 * @author msivak
 */
public class SourceDockActor extends HorizontalGroup {

    private Actor colorActor;
    private CardTemplate preview;

    public SourceDockActor() {
        align(Align.bottomLeft);
        colorActor = new Actor();
        createColorAction(colorActor, new Color(0xff333355), new Color(0xcc000055));
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
        batch.setColor(colorActor.getColor().r,
                colorActor.getColor().g,
                colorActor.getColor().b,
                colorActor.getColor().a * parentAlpha);
        Texture texture = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }
}
