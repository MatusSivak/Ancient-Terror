package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
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
    private TextureRegionDrawable keyFrame;
    private List<Runnable> onLandActions = new LinkedList<>();
    private FireballSmokeBuilder.FireballSmoke fireballSmoke;

    private Explosion explosion;

    private Group smokeGroup;
    private float midpointDisplacementDirection;
    private boolean blue;
    private float sizeMultiplier = 1f;
    private static ShaderProgram blueShader;

    public Fireball() {
        List<TextureRegionDrawable> frames = new LinkedList<>();
        for (int i = 15; i >= 0; i--) {
            Texture texture = CustomAssetManager.getTexture("fireball/" +
                    String.format(Locale.ENGLISH, "%03d", i) + ".png");
            frames.add(new TextureRegionDrawable(new TextureRegion(texture)));
        }
        fireballAnimation = new FireballAnimation(frames.toArray(new TextureRegionDrawable[0]));
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

    private static class FireballAnimation extends Animation<TextureRegionDrawable> {

        private FireballAnimation(TextureRegionDrawable... keyFrames) {
            super(1/(float)MathUtils.random(24,30), keyFrames);
            setPlayMode(PlayMode.LOOP);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        TextureRegionDrawable keyFrame = fireballAnimation.getKeyFrame(stateTime);
        if (keyFrame != this.keyFrame) {
            this.keyFrame = keyFrame;
            setDrawable(keyFrame);
        }
        if (fireballSmoke != null) {
            fireballSmoke.moveTo(getX() + getWidth()/2f, getY() + getHeight()/2f);
        }

    }

    public void setBlue(boolean blue) {
        this.blue = blue;
    }

    public void setSizeMultiplier(float sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
    }

    /** Swaps the red and blue channels of the orange flame art, turning it into a cold blue fire. */
    private static ShaderProgram getBlueShader() {
        if (blueShader == null) {
            String vertex = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                    + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                    + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                    + "uniform mat4 u_projTrans;\n"
                    + "varying vec4 v_color;\n"
                    + "varying vec2 v_texCoords;\n"
                    + "void main() {\n"
                    + "  v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                    + "  v_color.a = v_color.a * (255.0/254.0);\n"
                    + "  v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                    + "  gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                    + "}\n";
            String fragment = "#ifdef GL_ES\nprecision mediump float;\n#endif\n"
                    + "varying vec4 v_color;\n"
                    + "varying vec2 v_texCoords;\n"
                    + "uniform sampler2D u_texture;\n"
                    + "void main() {\n"
                    + "  vec4 c = texture2D(u_texture, v_texCoords);\n"
                    + "  gl_FragColor = v_color * vec4(c.b, c.g * 0.85 + c.r * 0.1, c.r, c.a);\n"
                    + "}\n";
            blueShader = new ShaderProgram(vertex, fragment);
            if (!blueShader.isCompiled()) {
                Gdx.app.error("Fireball", "Blue fireball shader failed: " + blueShader.getLog());
            }
        }
        return blueShader;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!blue || !getBlueShader().isCompiled()) {
            super.draw(batch, parentAlpha);
            return;
        }
        ShaderProgram previous = batch.getShader();
        batch.setShader(blueShader);
        super.draw(batch, parentAlpha);
        batch.setShader(previous);
    }

    public void setMidpointDisplacementDirection(float direction) {
        this.midpointDisplacementDirection = direction;
    }

    void throwIt(Vector2 origin, Vector2 destination, boolean intercepted) {

        Vector2 aim = new Vector2(destination);
        fireballSmoke = FireballSmokeBuilder.get();
        smokeGroup.addActor(fireballSmoke);
        setPosition(
                origin.x - getWidth()/2f,
                origin.y - getHeight()/2f);
        setScale(0.0f);
        CombatSounds.playMonsterFireballLaunch();

        float velocity = MathUtils.random(450, 520);
        Vector2 tangent = new Vector2(destination).sub(origin);
        float duration = tangent.len() / velocity;
        float travelFraction = intercepted ? 0.5f : 1f;

        float scale = MathUtils.random(0.4f, 0.6f) * sizeMultiplier;
        setColor(Color.DARK_GRAY);
        float midpointDisplacement = midpointDisplacementDirection * tangent.len() / 10;//MathUtils.random(-tangent.len() / 10, tangent.len() / 10);
        TweenMoveToAction tweenMoveToAction = new TweenMoveToAction(
                duration, travelFraction,
                new Vector2(origin.x - getWidth()/2f, origin.y - getHeight()/2f),
                new Vector2(destination.x - getWidth()/2f, destination.y - getHeight()/2f), midpointDisplacement);
        SequenceAction color = Actions.sequence(Actions.color(Color.WHITE, duration / 2f));
        SequenceAction size = Actions.sequence(Actions.scaleTo(scale, scale, duration / 2f, Interpolation.sineOut));
        if (!intercepted) {
            color.addAction(Actions.color(Color.DARK_GRAY, duration / 2f));
            size.addAction(Actions.scaleTo(scale * 0.1f, scale * 0.1f, duration / 2f, Interpolation.sineIn));
        }
        addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(color, size, tweenMoveToAction,
                        Actions.rotateTo(MathUtils.random(-360, 360) * travelFraction, duration * travelFraction)
                ),
                Actions.run(() -> {
                    Vector2 impact = new Vector2(getX(Align.center), getY(Align.center));
                    if (intercepted) {
                        setVisible(false);
                        BulletTracer.fire(getParent(), aim, impact);
                    }
                    CombatSounds.playFireballImpact();
                    if (!intercepted && !Gdx.app.getPreferences("AncientTerror.xml").getBoolean("vibration_disabled", false)) {
                        sk.sivak.eldritchhorror.core.view.utils.SoundPlayer.runInBackground(() -> Gdx.input.vibrate(50));
                    }
                    fireballSmoke.getParticleEffect().allowCompletion();
                    explosion.start(new Explosion.ExplosionConfig(impact,
                            blue ? new Color(0.25f, 0.55f, 1f, 1f) : new Color(1f, 0.3f, 0.12f,1f)));
                    for (Runnable onLandAction : onLandActions) {
                        onLandAction.run();
                    }
                }),
                Actions.removeActor()
        )));
    }

    private static class TweenMoveToAction extends TemporalAction {

        private final Vector2 origin;
        private final Vector2 destination;
        private final float travelFraction;
        private final float midpointDisplacement;

        private Vector2 tangent;
        private Vector2 nor;

        public TweenMoveToAction(float duration, float travelFraction,
                                 Vector2 origin, Vector2 destination,
                                 float midpointDisplacement) {
            super(duration * travelFraction);
            this.travelFraction = travelFraction;
            this.origin = origin;
            this.destination = destination;
            this.midpointDisplacement = midpointDisplacement;
        }

        @Override
        protected void update(float progress) {
            float value = Interpolation.sine.apply(progress * travelFraction);
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
