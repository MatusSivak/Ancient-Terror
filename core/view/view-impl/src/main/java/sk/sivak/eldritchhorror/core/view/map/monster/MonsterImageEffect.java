package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.scenes.scene2d.Actor;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

public class MonsterImageEffect {
    private MonsterImageEffect() {
    }

    public static Completable tearAppart(Actor monsterImage) {
        return Completable.create(onSub -> {
            if (monsterImage.getStage() == null) {
                onSub.onCompleted();
                return;
            }
            MonsterDefeatAnimation.start(monsterImage, onSub);
        });
    }

    static float defeatScale(float width, float height) {
        // Leave room for the recoil and epic rim around a much larger portrait.
        return Math.min(ViewProperties.VIEWPORT_WIDTH * 0.58f / width,
                ViewProperties.VIEWPORT_HEIGHT * 0.76f / height);
    }
}