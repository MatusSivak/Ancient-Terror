package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;

/**
 * @author msivak
 */
public class Spawner {

    private static AlphaAction createAlphaAction(float finalAlpha) {
        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setAlpha(finalAlpha);
        alphaAction.setDuration(NORMAL_ACTION_DURATION);
        return alphaAction;
    }

    private static ScaleToAction createScaleToAction() {
        ScaleToAction scaleToAction = new ScaleToAction();
        scaleToAction.setDuration(NORMAL_ACTION_DURATION);
        scaleToAction.setScale(1);
        scaleToAction.setInterpolation(Interpolation.swingOut);
        return scaleToAction;
    }

    public Completable spawn(ActorSpawner actorSpawner, SpawnConfig spawnConfig, MapStage.IdLayerResolver<?> idLayerResolver, LocationId location) {
        return spawn(actorSpawner, spawnConfig, idLayerResolver, location, null);
    }

    /** Runs once for the visible spawn, after assets/camera are ready, not once per world copy. */
    public Completable spawn(ActorSpawner actorSpawner, SpawnConfig spawnConfig, MapStage.IdLayerResolver<?> idLayerResolver,
                             LocationId location, Runnable onSpawn) {
        return Completable.create(onSub -> {
            Gdx.app.postRunnable(() -> {
                Vector2 position = LocationPositionResolver.resolve(location);

                Actor centerActor = spawnActor(actorSpawner, spawnConfig, position, true);
                Actor leftActor = spawnActor(actorSpawner, spawnConfig, new Vector2(position.x - MAP_WIDTH, position.y), false);
                Actor rightActor = spawnActor(actorSpawner, spawnConfig, new Vector2(position.x + MAP_WIDTH, position.y), false);

                MapStage.addToLayer(leftActor, idLayerResolver);
                MapStage.addToLayer(centerActor, idLayerResolver);
                MapStage.addToLayer(rightActor, idLayerResolver);
                if (onSpawn != null) onSpawn.run();
                spawnConfig.preSpawnConfig(leftActor, centerActor, rightActor);
                int[] landedCopies = {0};
                for (Actor token : new Actor[]{leftActor, centerActor, rightActor}) {
                    token.addAction(Actions.sequence(new FastForwardAction<>(createLandingAction(token)), Actions.run(() -> {
                        if (++landedCopies[0] == 3) {
                            spawnConfig.postSpawnConfig(leftActor, centerActor, rightActor);
                            onSub.onCompleted();
                        }
                    })));
                }
            });

        });
    }

    private TemporalAction createLandingAction(Actor token) {
        float landingY = token.getY();
        float dropHeight = 180f;
        token.setY(landingY + dropHeight);
        token.setScale(1.2f);
        return new TemporalAction(1.1f * NORMAL_ACTION_DURATION) {
            private MapSpawnEffect dust;

            @Override protected void update(float percent) {
                float descent = Math.min(1f, percent / .4f);
                if (descent < 1f) {
                    token.setY(landingY + dropHeight * (1f - descent * descent));
                    token.setScale(MathUtils.lerp(1.2f, 1f, descent));
                } else {
                    // Smoke stays on the ground while the token makes one small settling bounce.
                    token.setY(landingY);
                    if (dust == null) {
                        dust = new MapSpawnEffect(token);
                        MapStage.getSpawnEffectLayer().addActor(dust);
                    }
                    float bounce = MathUtils.clamp((percent - .4f) / .22f, 0f, 1f);
                    token.setY(landingY + MathUtils.sin(bounce * MathUtils.PI) * 10f * (1f - bounce));
                    token.setScale(1f);
                }
            }

            @Override protected void end() {
                token.setY(landingY);
                token.setScale(1f);
                if (dust != null) dust.remove();
            }
        };
    }

    public Completable unspawn(MapStage.IdLayerResolver<?> idLayerResolver) {
        return Completable.create(onSub -> {
            List<Actor> actors = MapStage.getActor(idLayerResolver);
            int i = 0;
            for (Actor actor : actors) {
                AlphaAction alphaActionLeft = createAlphaAction(0);
                ScaleToAction scaleToAction = createScaleToAction();
                scaleToAction.setScale(0f);
                scaleToAction.setInterpolation(Interpolation.swingIn);

                if (i == 0) {
                    actor.addAction(Actions.sequence(
                            new FastForwardAction(Actions.parallel(alphaActionLeft, scaleToAction)),
                            Actions.run(() -> {
                                MapStage.removeActor(idLayerResolver);
                                onSub.onCompleted();
                            })
                    ));
                } else {
                    actor.addAction(new FastForwardAction(Actions.parallel(alphaActionLeft, scaleToAction)));
                }
                i++;
            }
        });
    }

    private Actor spawnActor(ActorSpawner actorSpawner, SpawnConfig spawnConfig, Vector2 position, boolean isCenter) {
        Actor actor = actorSpawner.spawnActor(isCenter);
        actor.setScale(1);
        actor.setColor(1, 1, 1, spawnConfig.getFinalAlpha());
        actor.setRotation(spawnConfig.getActorRotation());
        actor.setPosition(position.x - actor.getOriginX() + spawnConfig.getOffsetX(), position.y - actor.getOriginY() + spawnConfig.getOffsetY());

        return actor;
    }

    public interface ActorSpawner {
        Actor spawnActor(boolean isCenter);
    }

    public interface SpawnConfig<T extends Actor> {
        float getActorRotation(); // 0-360

        float getOffsetX();

        float getOffsetY();

        float getFinalAlpha();

        void postSpawnConfig(T leftActor, T centerActor, T rigthActor);

        void preSpawnConfig(T leftActor, T centerActor, T rightActor);
    }

    public static class SpawnConfigImpl implements SpawnConfig {
        private float actorRotation;
        private float offsetX;
        private float offsetY;
        private float finalAlpha;

        public SpawnConfigImpl(float actorRotation, float offsetX, float offsetY, float finalAlpha) {
            this.actorRotation = actorRotation;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.finalAlpha = finalAlpha;
        }

        public SpawnConfigImpl() {
        }

        @Override
        public float getActorRotation() {
            return actorRotation;
        }

        public void setActorRotation(float actorRotation) {
            this.actorRotation = actorRotation;
        }

        @Override
        public float getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(float offsetX) {
            this.offsetX = offsetX;
        }

        @Override
        public float getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(float offsetY) {
            this.offsetY = offsetY;
        }

        @Override
        public float getFinalAlpha() {
            return finalAlpha;
        }

        public void setFinalAlpha(float finalAlpha) {
            this.finalAlpha = finalAlpha;
        }


        @Override
        public void preSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor) {

        }

        @Override
        public void postSpawnConfig(Actor leftActor, Actor centerActor, Actor rigthActor) {

        }

    }


}
