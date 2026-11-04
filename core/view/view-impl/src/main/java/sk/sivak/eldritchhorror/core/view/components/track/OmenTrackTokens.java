package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class OmenTrackTokens extends Group {

    public final static int TOKEN_SIZE = 125;
    public final static int TOKEN_DISTANCE = 100;
    private static final float ROTATION_SPEED = 20f;
    // The flame covers only ~70% of a fireball frame; enlarge it to match the old Orb disc.
    private static final float ORB_SCALE = 1.4f;
    private List<OmenTrackToken> tokens = new LinkedList<>();
    private float rotation;

    public void init(int tokensCount) {
        init(tokensCount, null);
    }

    public void init(int tokensCount, Runnable onAppear) {
        if (tokensCount == getTokensCount()) return;
        clearActions();
        if (!tokens.isEmpty()) {
            for (OmenTrackToken token : tokens) {
                token.clearActions();
                token.addAction(new FastForwardAction<>(Actions.alpha(0f, 0.5f, Interpolation.linear)));
            }
            // Rebuild and announce once for the group, not once for every fading token.
            addAction(Actions.sequence(new FastForwardAction<>(Actions.delay(0.5f)),
                    Actions.run(() -> replaceTokens(tokensCount, onAppear))));
        } else {
            replaceTokens(tokensCount, onAppear);
        }
    }

    private void replaceTokens(int count, Runnable onAppear) {
        clearChildren();
        tokens.clear();
        justInit(count);
        if (onAppear != null) onAppear.run();
    }

    public void justInit(int tokensCount) {
        for (int i = 0; i < tokensCount; i++) {
            tokens.add(createOmenTrackToken(i, tokensCount));
        }

        for (OmenTrackToken token : tokens) {
            addActor(token);
        }
    }

    private OmenTrackToken createOmenTrackToken(int index, int tokensCount) {
        OmenTrackToken omenTrackToken = new OmenTrackToken();
        omenTrackToken.setWidth(TOKEN_SIZE);
        omenTrackToken.setHeight(TOKEN_SIZE);

        float x = MathUtils.cosDeg((360 / tokensCount) * index);
        float y = MathUtils.sinDeg((360 / tokensCount) * index);

        omenTrackToken.setPosition(x * TOKEN_DISTANCE, y * TOKEN_DISTANCE);

        omenTrackToken.setScale(3f);
        omenTrackToken.getColor().a = 0f;
        omenTrackToken.setOrigin(omenTrackToken.getWidth() / 2, omenTrackToken.getHeight() / 2);
        omenTrackToken.addAction(Actions.parallel(
                Actions.scaleTo(ORB_SCALE, ORB_SCALE, 1f, Interpolation.swingOut),
                Actions.alpha(1f, 1f, Interpolation.linear)
        ));
        return omenTrackToken;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        rotation += ROTATION_SPEED * delta;
        if (rotation > 360) {
            rotation -= 360;
        }
        for (int i = 0; i < tokens.size(); i++) {
            actTokenIndex(i, tokens.size());
        }

    }

    private void actTokenIndex(int index, int tokensCount) {
        OmenTrackToken omenTrackToken = tokens.get(index);
        float x = MathUtils.cosDeg((360 / tokensCount) * index + rotation);
        float y = MathUtils.sinDeg((360 / tokensCount) * index + rotation);

        omenTrackToken.setPosition(x * TOKEN_DISTANCE, y * TOKEN_DISTANCE);

    }

    public int getTokensCount() {
        if (tokens == null) {
            return 0;
        }
        return tokens.size();
    }

    /** An Orb drawn as a looping green fireball (the combat fireball frames recoloured by a shader). */
    private static class OmenTrackToken extends Image {
        private static final int FRAME_COUNT = 16;
        private static final float FRAME_DURATION = 1 / 24f;
        private static ShaderProgram greenShader;

        private final Animation<TextureRegionDrawable> animation;
        private float stateTime = MathUtils.random(FRAME_COUNT * FRAME_DURATION);

        public OmenTrackToken() {
            TextureRegionDrawable[] frames = new TextureRegionDrawable[FRAME_COUNT];
            for (int i = 0; i < FRAME_COUNT; i++) {
                Texture texture = CustomAssetManager.getTexture("fireball/" +
                        String.format(Locale.ENGLISH, "%03d", FRAME_COUNT - 1 - i) + ".png");
                frames[i] = new TextureRegionDrawable(new TextureRegion(texture));
            }
            animation = new Animation<>(FRAME_DURATION, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
            setDrawable(animation.getKeyFrame(stateTime));
            setScaling(Scaling.fit);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            stateTime += delta;
            TextureRegionDrawable frame = animation.getKeyFrame(stateTime);
            if (frame != getDrawable()) setDrawable(frame);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            ShaderProgram shader = getGreenShader();
            if (!shader.isCompiled()) {
                super.draw(batch, parentAlpha);
                return;
            }
            ShaderProgram previous = batch.getShader();
            batch.setShader(shader);
            super.draw(batch, parentAlpha);
            batch.setShader(previous);
        }

        /** Rotates the colour channels of the orange flame art so it burns green. */
        private static ShaderProgram getGreenShader() {
            if (greenShader == null) {
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
                        + "  gl_FragColor = v_color * vec4(c.g * 0.45, c.r, c.b * 0.6 + c.g * 0.15, c.a);\n"
                        + "}\n";
                greenShader = new ShaderProgram(vertex, fragment);
                if (!greenShader.isCompiled()) {
                    Gdx.app.error("OmenTrackTokens", "Green orb shader failed: " + greenShader.getLog());
                }
            }
            return greenShader;
        }
    }
}
