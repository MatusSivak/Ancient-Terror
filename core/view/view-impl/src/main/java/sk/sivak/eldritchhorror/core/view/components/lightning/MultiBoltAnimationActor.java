package sk.sivak.eldritchhorror.core.view.components.lightning;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.LinkedList;
import java.util.List;

public class MultiBoltAnimationActor extends Group {
    private List<BoltAnimationActor> actors;

    public MultiBoltAnimationActor(int boltsCount, int framesCount,
                                   Vector2 begin, Vector2 end,
                                   float thickness, Color color) {
        actors = new LinkedList<>();
        for (int n = 0; n < boltsCount; n++) {
            List<Bolt> bolts = new LinkedList<>();
            for (int i=0; i<framesCount; i++) {
                Bolt bolt = BoltBuilder.createBolt(begin, end, thickness, color);
                bolts.add(bolt);
            }

            actors.add(new BoltAnimationActor(new BoltAnimation(bolts.toArray(new Bolt[0]))));
        }
        for (BoltAnimationActor actor : actors) {
            addActor(actor);
        }
    }
}
