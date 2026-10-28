package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.scenes.scene2d.Actor;
import rx.Completable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.animation.BreakApartAnimation;

public class MonsterImageEffect {
    private MonsterImageEffect() {
    }

    public static Completable tearAppart(Actor monsterImage) {
        return Completable.create(onSub -> {
            if (monsterImage.getStage() == null) {
                onSub.onCompleted();
                return;
            }
            if (monsterImage instanceof MonsterImage) {
                ((MonsterImage) monsterImage).prepareForDefeat();
            } else if (monsterImage instanceof EpicMonsterImage) {
                ((EpicMonsterImage) monsterImage).prepareForDefeat();
            }
            BreakApartAnimation.start(monsterImage,
                    monsterImage instanceof Image ? ((Image) monsterImage).getDrawable() : null,
                    new Color(1f, 0.78f, 0.48f, 1f), "monster-defeat", onSub);
        });
    }

    static float defeatScale(float width, float height) {
        return BreakApartAnimation.presentationScale(width, height);
    }
}
