package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class BoltSegment extends Group {

    private final Line line;
    private final float thickness;

    private final Image linePartImage;
    private final Image leftEndImage;
    private final Image rightEndImage;

    public BoltSegment(Line line, float thickness, Color color) {
        this.line = line;
        this.thickness = thickness;

        float angle = line.getPointB().sub(line.getPointA()).angle();

        linePartImage = new Image(CustomAssetManager.getTexture("combat/line_part.png"));
        linePartImage.setPosition(line.getPointA().x, line.getPointA().y);
        linePartImage.setWidth(line.getPointA().dst(line.getPointB()));
        linePartImage.setHeight(thickness);
        linePartImage.setOrigin(0, linePartImage.getHeight()/2);
        linePartImage.setRotation(angle);
        linePartImage.setColor(new Color(color).mul(1f,1f,1f,0.75f));

        leftEndImage = new Image(CustomAssetManager.getTexture("combat/line_end.png"));
        leftEndImage.setScaling(Scaling.fit);
        leftEndImage.setHeight(thickness);
        leftEndImage.setWidth(thickness/2);
        leftEndImage.setPosition(line.getPointA().x - leftEndImage.getWidth(), line.getPointA().y);
        leftEndImage.setOrigin(leftEndImage.getWidth(), leftEndImage.getHeight()/2);
        leftEndImage.setRotation(angle);
        leftEndImage.setColor(color);

        rightEndImage = new Image(CustomAssetManager.getTexture("combat/line_end.png"));
        rightEndImage.setScaling(Scaling.fit);
        rightEndImage.setHeight(thickness);
        rightEndImage.setWidth(thickness/2);
        rightEndImage.setPosition(line.getPointB().x - rightEndImage.getWidth(), line.getPointB().y);
        rightEndImage.setOrigin(rightEndImage.getWidth(), rightEndImage.getHeight()/2);
        rightEndImage.setRotation(angle+180);
        rightEndImage.setColor(color);

        addActor(leftEndImage);
        addActor(linePartImage);
        addActor(rightEndImage);
    }

    public Vector2 getStart() {
        return line.getPointA();
    }

    public Vector2 getEnd() {
        return line.getPointB();
    }
}
