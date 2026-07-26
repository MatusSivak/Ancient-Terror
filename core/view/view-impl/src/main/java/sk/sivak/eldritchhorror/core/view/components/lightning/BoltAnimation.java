package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.graphics.g2d.Animation;

public class BoltAnimation extends Animation<Bolt> {

    public BoltAnimation(Bolt... keyFrames) {
        super(0.2f, keyFrames);
        setPlayMode(PlayMode.LOOP);
    }
}
