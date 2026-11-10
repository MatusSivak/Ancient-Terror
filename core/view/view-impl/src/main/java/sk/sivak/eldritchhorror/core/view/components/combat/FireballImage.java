package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Looping flame artwork shared by combat projectiles, stat tokens and cards. */
public class FireballImage extends Image {
    public enum Kind { HORROR, DAMAGE }

    private static ShaderProgram blueShader;
    private final TextureRegionDrawable[] frames = CustomAssetManager.getFireballFrames();
    private final int framesPerSecond = MathUtils.random(24, 30);
    protected float stateTime;
    protected boolean blue;

    public FireballImage(Kind kind) {
        blue = kind == Kind.HORROR;
        setDrawable(frames[0]);
        setScaling(Scaling.fit);
        if (!blue) setColor(1f, 0.42f, 0.38f, 1f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        setDrawable(frames[(int)(stateTime * framesPerSecond) % frames.length]);
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
}
