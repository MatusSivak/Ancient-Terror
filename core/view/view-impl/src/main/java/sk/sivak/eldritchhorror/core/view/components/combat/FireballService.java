package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import java8.features.function.Supplier;
import java8.features.util.MapUtils;
import rx.Completable;
import rx.Observable;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FireballService {

    private final Stage stage;
    private final Group explosionGroup;
    private final Group smokeGroup;
    private final Group fireballGroup;
    private int targetHealth;
    private float minDelay;
    private float maxDelay;
    private float midpointDisplacementDirection;
    private Map<Vector2, Runnable> onLastThrowActions = new HashMap<>();
    private Map<Vector2, Runnable> onThrowActions = new HashMap<>();
    private Map<Vector2, Runnable> onLandActions = new HashMap<>();
    private Map<Vector2, Runnable> onLandLastActions = new HashMap<>();
    private Runnable onLandLastAction;
    private Runnable onLandFirstAction;

    public FireballService(Stage stage) {
        this.stage = stage;
        explosionGroup = new Group();
        smokeGroup = new Group();
        fireballGroup = new Group();
        reset();
    }

    public void reset() {
        weakReset();
        explosionGroup.clear();
        smokeGroup.clear();
        fireballGroup.clear();
        smokeGroup.remove();
        explosionGroup.remove();
        fireballGroup.remove();
        stage.addActor(smokeGroup);
        stage.addActor(explosionGroup);
        stage.addActor(fireballGroup);
    }

    public void weakReset() {
        onLandActions.clear();
        onThrowActions.clear();
        onLastThrowActions.clear();
        onLandLastActions.clear();
        onLandLastAction = null;
        onLandFirstAction = null;
        midpointDisplacementDirection = 0f;
        targetHealth = 3;
        minDelay = 0.3f;
        maxDelay = 0.6f;
    }

    public void launchFireballs(List<Vector2> sources, List<Vector2> targets) {
        List<Vector2> allTargets = createAllTargets(sources, targets);
        Map<Vector2, List<Vector2>> sourceTargetsMap = createSourceTargetsMap(sources, allTargets);
        Map<Vector2, Explosion> targetExplosionMap = createTargetExplosionMap(targets);

        addGroupsToStage(targetExplosionMap.values(), explosionGroup, smokeGroup, fireballGroup);
        Map<Vector2, List<Fireball>> fireballsMap = createFireballs(sourceTargetsMap, targetExplosionMap, smokeGroup, fireballGroup);
        initOnLandLastActions(fireballsMap);
        initOnLandFirstAction(fireballsMap);
        initOnLandLastAction(fireballsMap);
    }

    public void setMidpointDisplacementDirection(float midpointDisplacementDirection) {
        this.midpointDisplacementDirection = midpointDisplacementDirection;
    }

    private void initOnLandLastAction(Map<Vector2, List<Fireball>> fireballsMap) {
        if (onLandLastAction != null) {
            List<Completable> completables = new LinkedList<>();
            Observable.from(fireballsMap.values())
                    .flatMapIterable(fireballs -> fireballs)
                    .subscribe(fireball -> {
                        completables.add(Completable.create(onSub -> fireball.addOnLandAction(onSub::onCompleted)));
                    });

            Completable.merge(completables).subscribe(() -> onLandLastAction.run());
        }
    }

    private void initOnLandFirstAction(Map<Vector2, List<Fireball>> fireballsMap) {
        if (onLandFirstAction != null) {
            List<Completable> completables = new LinkedList<>();
            Observable.from(fireballsMap.values())
                    .flatMapIterable(fireballs -> fireballs)
                    .subscribe(fireball -> {
                        completables.add(Completable.create(onSub -> fireball.addOnLandAction(onSub::onCompleted)));
                    });

            Completable.amb(completables).subscribe(() -> onLandFirstAction.run());;
        }
    }

    private void initOnLandLastActions(Map<Vector2, List<Fireball>> fireballsMap) {
        for (Map.Entry<Vector2, Runnable> entry : onLandLastActions.entrySet()) {
            List<Fireball> fireballs = fireballsMap.get(entry.getKey());
            List<Completable> completables = new LinkedList<>();
            Observable.from(fireballs)
                    .subscribe(fireball -> {
                completables.add(Completable.create(onSub -> fireball.addOnLandAction(onSub::onCompleted)));
            });
            Completable.merge(completables).subscribe(() -> entry.getValue().run());
        }
    }

    public void setOnLandLastAction(Runnable onLandLastAction) {
        this.onLandLastAction = onLandLastAction;
    }

    public void setOnLandFirstAction(Runnable onLandFirstAction) {
        this.onLandFirstAction = onLandFirstAction;
    }

    public void setOnLandLastAction(Vector2 target, Runnable onLandLastAction) {
        onLandLastActions.put(target, onLandLastAction);
    }

    public void setOnLandAction(Vector2 target, Runnable onLandAction) {
        onLandActions.put(target, onLandAction);
    }

    public void setOnThrowAction(Vector2 source, Runnable onThrowAction) {
        onThrowActions.put(source, onThrowAction);
    }

    public void setOnLastThrowActions(Vector2 source, Runnable onLastThrowAction) {
        onLastThrowActions.put(source, onLastThrowAction);
    }

    private void addGroupsToStage(Collection<Explosion> explosions, Group explosionGroup, Group smokeGroup, Group fireballGroup) {

        stage.addActor(smokeGroup);
        stage.addActor(explosionGroup);
        stage.addActor(fireballGroup);
        for (Explosion explosion : explosions) {
            explosionGroup.addActor(explosion);
        }
    }

    private Map<Vector2, List<Fireball>> createFireballs(Map<Vector2, List<Vector2>> sourceTargetsMap, Map<Vector2, Explosion> targetExplosionMap, Group smokeGroup, Group fireballGroup) {
        HashMap<Vector2, List<Fireball>> fireballsMap = new HashMap<>();
        for (Map.Entry<Vector2, List<Vector2>> entry : sourceTargetsMap.entrySet()) {
            List<Vector2> targets = entry.getValue();
            Iterator<Vector2> targetsIterator = targets.iterator();
            SequenceAction sequenceAction = new SequenceAction();
            sequenceAction.addAction(new VariableDelayAction(() -> MathUtils.random(0, (minDelay + maxDelay)/2f)));
            while (targetsIterator.hasNext()) {
                Vector2 target = targetsIterator.next();
                Fireball fireball = new Fireball();
                Vector2 source = entry.getKey();
                if (fireballsMap.get(target) == null) {
                    fireballsMap.put(target, new LinkedList<>());
                }
                fireballsMap.get(target).add(fireball);
                sequenceAction.addAction(Actions.run(() -> {
                    fireball.setExplosion(targetExplosionMap.get(target));
                    fireball.setSmokeGroup(smokeGroup);
                    fireball.setMidpointDisplacementDirection(midpointDisplacementDirection);
                    fireball.addOnLandAction(MapUtils.getOrDefault(onLandActions, target, () -> {}));
                    MapUtils.getOrDefault(onThrowActions, source, () -> {}).run();
                    fireball.throwIt(source, target);
                    fireballGroup.addActor(fireball);

                }));
                if (targetsIterator.hasNext()) {
                    sequenceAction.addAction(new VariableDelayAction(() -> MathUtils.random(minDelay, maxDelay)));
                } else {
                    sequenceAction.addAction(Actions.run(() -> MapUtils.getOrDefault(onLastThrowActions, source, () -> {}).run()));
                }
            }
            stage.addAction(new FastForwardAction<>(sequenceAction));
        }
        return fireballsMap;
    }

    private Map<Vector2, Explosion> createTargetExplosionMap(List<Vector2> targets) {
        Map<Vector2, Explosion> targetExplosionMap = new HashMap<>();
        for (Vector2 target : targets) {
            Explosion explosion = new Explosion();
            targetExplosionMap.put(target, explosion);
        }
        return targetExplosionMap;
    }

    private Map<Vector2, List<Vector2>> createSourceTargetsMap(List<Vector2> sources, List<Vector2> allTargets) {
        Map<Vector2, List<Vector2>> sourceTargetsMap = new HashMap<>();
        for (int i = 0; i < targetHealth; i++) {
            for (Vector2 source : sources) {
                Vector2 target = allTargets.remove(0);
                if (!sourceTargetsMap.containsKey(source)) {
                    sourceTargetsMap.put(source, new LinkedList<>());
                }
                sourceTargetsMap.get(source).add(target);
            }
        }
        return sourceTargetsMap;
    }

    private List<Vector2> createAllTargets(List<Vector2> sources, List<Vector2> targets) {
        List<Vector2> allTargets = new LinkedList<>();
        int targetIndex = 0;
        for (int i = 0; i < targetHealth * sources.size(); i++) {
            Vector2 target = targets.get(targetIndex++);
            if (targetIndex == targets.size()) {
                targetIndex = 0;
            }
            allTargets.add(target);
        }
        Collections.shuffle(allTargets);
        return allTargets;
    }

    public void setTargetHealth(int targetHealth) {
        this.targetHealth = targetHealth;
    }

    private static class VariableDelayAction extends DelayAction {

        private final Supplier<Float> durationSupplier;


        public VariableDelayAction(Supplier<Float> durationSupplier) {
            super(durationSupplier.get());
            this.durationSupplier = durationSupplier;
        }

        @Override
        public void restart() {
            super.restart();
            setDuration(durationSupplier.get());
        }
    }
}
