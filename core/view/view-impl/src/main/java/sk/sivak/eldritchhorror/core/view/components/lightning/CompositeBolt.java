package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.Viewport;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;

public class CompositeBolt extends Group {
    private final List<Bolt> bolts;
    private float intensity = 1f;
    private Object textureRegion;

    public CompositeBolt(List<Bolt> bolts) {
        this.bolts = bolts;
        for (Bolt bolt : bolts) {
            addActor(bolt);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        intensity *= 0.9f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textureRegion == null) {

        }
        super.draw(batch, parentAlpha * intensity);
    }

    public void init() {
        intensity = 1f;
    }

    public Vector2 getStart() {
        return bolts.get(0).getStart();
    }

    public Vector2 getEnd() {
        return bolts.get(0).getEnd();
    }

}
