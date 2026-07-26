package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.List;

public final class CompositeBoltBuilder {

    private CompositeBoltBuilder() {
    }

    public static CompositeBolt build(Vector2 source, Vector2 destination, float thickness, Color color, int count) {
        List<Bolt> bolts = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            color = new Color(color).mul(1f,1f,1f,MathUtils.random(0.5f,1f));
            bolts.add(BoltBuilder.createBolt(source, destination, thickness, color));
        }
        return new CompositeBolt(bolts);
    }
}
