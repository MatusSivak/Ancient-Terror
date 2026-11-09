package sk.sivak.eldritchhorror.core.view.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** A background picture lit per pixel by the active {@link SceneLight}s. Without lights it draws as a plain image. */
public class LitImage extends Image {

    private static final int MAX_LIGHTS = 8;
    /** How strongly a light multiplies the colors under it, and how much it adds on top, so even black areas glow a bit. */
    private static final float GAIN = 2.4f;
    private static final float GLOW = 0.07f;
    private static final float MAX_AMBIENT = 1.2f;

    private static final String VERTEX = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "uniform mat4 u_projTrans;\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "varying vec2 v_position;\n"
            + "void main() {\n"
            + "  v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "  v_color.a = v_color.a * (255.0/254.0);\n"
            + "  v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "  v_position = " + ShaderProgram.POSITION_ATTRIBUTE + ".xy;\n"
            + "  gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "}\n";

    // Light position: xy = center, z = radius, w = intensity (all in the image's stage coordinates).
    // Distances are divided by the radius first, so mediump floats cannot overflow.
    private static final String FRAGMENT = "#ifdef GL_ES\nprecision mediump float;\n#endif\n"
            + "#define MAX_LIGHTS " + MAX_LIGHTS + "\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "varying vec2 v_position;\n"
            + "uniform sampler2D u_texture;\n"
            + "uniform vec4 u_lightPos[MAX_LIGHTS];\n"
            + "uniform vec3 u_lightColor[MAX_LIGHTS];\n"
            + "uniform vec3 u_ambient;\n"
            + "void main() {\n"
            + "  vec4 c = v_color * texture2D(u_texture, v_texCoords);\n"
            + "  vec3 light = u_ambient;\n"
            + "  for (int i = 0; i < MAX_LIGHTS; i++) {\n"
            + "    vec2 d = (v_position - u_lightPos[i].xy) / u_lightPos[i].z;\n"
            + "    float falloff = max(0.0, 1.0 - dot(d, d));\n"
            + "    light += u_lightColor[i] * (falloff * falloff * u_lightPos[i].w);\n"
            + "  }\n"
            + "  gl_FragColor = vec4(c.rgb * (1.0 + light * " + GAIN + ") + light * " + GLOW + " * c.a, c.a);\n"
            + "}\n";

    private static ShaderProgram shader;
    private static boolean failed;

    private final float[] positions = new float[MAX_LIGHTS * 4];
    private final float[] colors = new float[MAX_LIGHTS * 3];
    private final Vector2 point = new Vector2();
    private final Vector2 edge = new Vector2();

    public LitImage(TextureRegion region) {
        super(region);
    }

    private static ShaderProgram getShader() {
        if (shader == null && !failed) {
            shader = new ShaderProgram(VERTEX, FRAGMENT);
            if (!shader.isCompiled()) {
                Gdx.app.error("LitImage", "Scene light shader failed: " + shader.getLog());
                shader.dispose();
                shader = null;
                failed = true;
            }
        }
        return shader;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram lit = SceneLight.anyLit() && getStage() != null ? getShader() : null;
        if (lit == null) {
            super.draw(batch, parentAlpha);
            return;
        }
        ShaderProgram previous = batch.getShader();
        batch.setShader(lit);
        uploadLights(lit);
        super.draw(batch, parentAlpha);
        batch.setShader(previous);
    }

    private void uploadLights(ShaderProgram lit) {
        Stage target = getStage();
        float ambientR = 0f, ambientG = 0f, ambientB = 0f;
        int count = 0;
        for (SceneLight light : SceneLight.ACTIVE) {
            if (light.intensity <= 0.001f) continue;
            ambientR += light.color.r * light.ambient;
            ambientG += light.color.g * light.ambient;
            ambientB += light.color.b * light.ambient;
            if (count == MAX_LIGHTS) continue;
            float x = light.x, y = light.y, radius = light.radius;
            if (light.stage != target && light.stage != null) {
                // Lights may live on another stage (fireballs fly on the info stage), so go through screen coordinates.
                light.stage.stageToScreenCoordinates(point.set(x, y));
                light.stage.stageToScreenCoordinates(edge.set(x + radius, y));
                target.screenToStageCoordinates(point);
                target.screenToStageCoordinates(edge);
                x = point.x;
                y = point.y;
                radius = point.dst(edge);
            }
            int p = count * 4, c = count * 3;
            positions[p] = x;
            positions[p + 1] = y;
            positions[p + 2] = Math.max(1f, radius);
            positions[p + 3] = light.intensity;
            colors[c] = light.color.r;
            colors[c + 1] = light.color.g;
            colors[c + 2] = light.color.b;
            count++;
        }
        for (int i = count; i < MAX_LIGHTS; i++) {
            positions[i * 4 + 2] = 1f;
            positions[i * 4 + 3] = 0f;
        }
        lit.setUniform4fv("u_lightPos", positions, 0, positions.length);
        lit.setUniform3fv("u_lightColor", colors, 0, colors.length);
        lit.setUniformf("u_ambient", MathUtils.clamp(ambientR, 0f, MAX_AMBIENT),
                MathUtils.clamp(ambientG, 0f, MAX_AMBIENT), MathUtils.clamp(ambientB, 0f, MAX_AMBIENT));
    }
}
