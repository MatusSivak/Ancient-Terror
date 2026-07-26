package sk.sivak.eldritchhorror.core.view.components.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class ActorFrame extends Actor {


    private final Actor target;
    private final float thickness;
    private Texture texture;

    public ActorFrame(Actor target, float thickness) {
        this.target = target;
        this.thickness = thickness;
        this.texture = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
        setColor(Color.DARK_GRAY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(texture, target.getX() - thickness, target.getY() - thickness,
                thickness, target.getHeight() + thickness * 2);
        batch.draw(texture, target.getX() - thickness, target.getY() - thickness,
                target.getWidth() + thickness * 2, thickness);

        batch.draw(texture, target.getX() + target.getWidth(), target.getY() - thickness,
                thickness, target.getHeight() + thickness * 2);
        batch.draw(texture, target.getX() - thickness, target.getY() + target.getHeight(),
                target.getWidth() + thickness * 2, thickness);


    }
}
