package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.SoundPlayer;

/**
 * Heavy footsteps while monster tokens travel across the map; they stop as soon as the move completes.
 * Source credits: sounds/PRESENTATION_AUDIO.md.
 */
public final class MonsterFootsteps {
    public static final String[] SOUNDS = {
            "sounds/monster_prowl_1.wav", "sounds/monster_prowl_2.wav",
            "sounds/monster_prowl_3.wav", "sounds/monster_prowl_4.wav"};
    static final float STEP_INTERVAL = 0.38f;
    private static final float VOLUME = 0.72f;

    private static int lastStep = -1;

    private MonsterFootsteps() { }

    public static Completable during(Completable movement) {
        return Completable.defer(() -> {
            Stage stage = MapStage.getStage();
            if (stage == null) return movement;
            Action steps = Actions.forever(Actions.sequence(
                    Actions.run(MonsterFootsteps::step),
                    Actions.delay(STEP_INTERVAL)));
            stage.addAction(steps);
            Runnable stop = () -> stage.getRoot().removeAction(steps);
            return movement.doAfterTerminate(stop::run).doOnUnsubscribe(stop::run);
        });
    }

    static void step() {
        if (FastForwardAction.isOn()) return;
        int next = MathUtils.random(SOUNDS.length - 1);
        if (next == lastStep) next = (next + 1) % SOUNDS.length;
        lastStep = next;
        // Prepared gravel/creature layers give each step weight; vary them without a repeated loop.
        SoundPlayer.play(CustomAssetManager.getLoadedSound(SOUNDS[lastStep]),
                VOLUME * MathUtils.random(0.85f, 1f), MathUtils.random(0.92f, 1.04f), 0f);
    }
}
