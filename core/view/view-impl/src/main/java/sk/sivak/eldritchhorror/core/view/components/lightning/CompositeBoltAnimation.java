package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.graphics.g2d.Animation;

public class CompositeBoltAnimation extends Animation<CompositeBolt> {

    public CompositeBoltAnimation(CompositeBolt... compositeBolts) {
        super(0.4f, compositeBolts);
        setPlayMode(PlayMode.LOOP);
    }
}
