package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Fireball extends Image {

    private final FireballAnimation fireballAnimation;
    private float stateTime;
    private Texture keyFrame;
    private List<Runnable> onLandActions = new LinkedList<>();
    private FireballSmokeBuilder.FireballSmoke fireballSmoke;

    private Explosion explosion;

    private Group smokeGroup;
    private float midpointDisplacementDirection;

    public Fireball() {
        List<Texture> textures = new LinkedList<>();
        for (int i = 15; i >= 0; i--) {
            Texture texture = CustomAssetManager.getTexture("fireball/" +
                    String.format(Locale.ENGLISH, "%03d", i) + ".png");
            textures.add(texture);
        }
        fireballAnimation = new FireballAnimation(textures.toArray(new Texture[0]));
        setSize(256, 256);
        setOrigin(Align.center);
        setScaling(Scaling.fit);
    }

    public void setSmokeGroup(Group smokeGroup) {
        this.smokeGroup = smokeGroup;
    }

    public void setExplosion(Explosion explosion) {
        this.explosion = explosion;
    }

    public void addOnLandAction(Runnable onLandAction) {
        onLandActions.add(onLandAction);
    }

    private static class FireballAnimation extends Animation<Texture> {

        private FireballAnimation(Texture... keyFrames) {
            super(1/(float)MathUtils.random(24,30), keyFrames);
            setPlayMode(PlayMode.LOOP);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        Texture keyFrame = fireballAnimation.getKeyFrame(stateTime);
        if (keyFrame != this.keyFrame) {
            this.keyFrame = keyFrame;
            setDrawable(new TextureRegionDrawable(new TextureRegion(this.keyFrame)));
        }
        fireballSmoke.getParticleEffect().setPosition(getX() + getWidth()/2f, getY() + getHeight()/2f);

    }

    public void setMidpointDisplacementDirection(float direction) {
        this.midpointDisplacementDirection = direction;
    }

    public void throwIt(Vector2 origin, Vector2 destination) {
        Vector2 destinationFixed = new Vector2(destination);
        fireballSmoke = FireballSmokeBuilder.get();
        smokeGroup.addActor(fireballSmoke);
        setPosition(
                origin.x - getWidth()/2f,
                origin.y - getHeight()/2f);
        setScale(0.0f);

        float velocity = MathUtils.random(250,300);
        Vector2 tangent = new Vector2(destination).sub(origin);
        float duration = tangent.len() / velocity;

        float scale = MathUtils.random(0.4f, 0.6f);
        setColor(Color.DARK_GRAY);
        float midpointDisplacement = midpointDisplacementDirection * tangent.len() / 10;//MathUtils.random(-tangent.len() / 10, tangent.len() / 10);
        TweenMoveToAction tweenMoveToAction = new TweenMoveToAction(
                duration, Interpolation.sine,
                new Vector2(origin.x - getWidth()/2f, origin.y - getHeight()/2f),
                new Vector2(destination.x - getWidth()/2f, destination.y - getHeight()/2f), midpointDisplacement);
        addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(
                        Actions.sequence(
                                Actions.color(Color.WHITE, duration/2f),
                                Actions.color(Color.DARK_GRAY, duration/2f)
                        ),
                        tweenMoveToAction,
                        Actions.sequence(
                                Actions.scaleTo(scale, scale, duration/2, Interpolation.sineOut),
                                Actions.scaleTo(scale * 0.1f, scale * 0.1f, duration/2, Interpolation.sineIn)
                        ),
                        Actions.rotateTo(MathUtils.random(-360, 360), duration)
                ),
                Actions.run(() -> {
                    if (!Gdx.app.getPreferences("AncientTerror.xml").getBoolean("vibration_disabled", false)) {
                        Gdx.input.vibrate(50);
                    }
                    for (Runnable onLandAction : onLandActions) {
                        onLandAction.run();
                    }

                    fireballSmoke.getParticleEffect().allowCompletion();
                    explosion.start(new Explosion.ExplosionConfig(destinationFixed, new Color(1f, 0.3f, 0.12f,1f)));
                }),
                Actions.removeActor()
        )));
    }

    private static class TweenMoveToAction extends TemporalAction {

        private final Vector2 origin;
        private final Vector2 destination;
        private final float midpointDisplacement;

        private Vector2 tangent;
        private Vector2 nor;

        public TweenMoveToAction(float duration, Interpolation interpolation,
                                 Vector2 origin, Vector2 destination,
                                 float midpointDisplacement) {
            super(duration, interpolation);
            this.origin = origin;
            this.destination = destination;
            this.midpointDisplacement = midpointDisplacement;
        }

        @Override
        protected void update(float value) {
            float sin = MathUtils.sin((float) (Math.PI * value));

            float middlePointX = origin.x + tangent.x * value;
            float middlePointY = origin.y + tangent.y * value;

            float middlePointDisplacementX = middlePointX + nor.x * midpointDisplacement*sin;
            float middlePointDisplacementY = middlePointY + nor.y * midpointDisplacement*sin;

            getActor().setPosition(middlePointDisplacementX, middlePointDisplacementY);
        }

        @Override
        protected void begin() {
            super.begin();
            tangent = new Vector2(destination).sub(origin);
            nor = new Vector2(tangent.y, -tangent.x).nor();
        }
    }
}
