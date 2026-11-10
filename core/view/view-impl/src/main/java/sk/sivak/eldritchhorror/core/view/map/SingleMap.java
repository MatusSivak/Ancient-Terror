package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.gate.NewGateAnimatedImage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.shader.LitImage;

/**
 * @author msivak
 */
public class SingleMap extends Image {

    private static final float LIGHT_RADIUS_IN_GATE_WIDTHS = 1.45f;
    private static final float LIGHT_INTENSITY = 0.42f;
    private static final float SPOTLIGHT_RADIUS_IN_STAND_HEIGHTS = 0.9f;
    private static final float SPOTLIGHT_INTENSITY = 0.5f;
    private static final Color SPOTLIGHT_COLOR = new Color(1f, 0.98f, 0.94f, 1f);
    // Each light quad samples the map underneath it. Clip at tile boundaries so a
    // light spanning the wrap seam adds its contribution exactly once on each side.
    // Gates glow with a soft point falloff; the spotlight has a bright core and a narrow rim.
    private static final String GATE_LIGHT = "uniform vec4 u_tile;\n"
            + "uniform vec3 u_mapTint;\n"
            + "uniform float u_spot;\n"
            + "void main() {\n"
            + "  vec2 uv = (v_position - u_tile.xy) * u_tile.zw;\n"
            + "  if (min(uv.x, uv.y) < 0.0 || max(uv.x, uv.y) >= 1.0) discard;\n"
            + "  vec4 surface = texture2D(u_texture, vec2(uv.x, 1.0 - uv.y));\n"
            + "  surface.rgb *= u_mapTint;\n"
            + "  vec2 d = 2.0 * v_texCoords - 1.0;\n"
            + "  float falloff = mix(pointFalloff(d), 1.0 - smoothstep(0.45, 1.0, length(d)), u_spot);\n"
            + "  vec3 light = v_color.rgb * v_color.a * falloff;\n"
            + "  gl_FragColor = vec4(reflectedLight(surface, light), 0.0);\n"
            + "}\n";

    private static ShaderProgram gateShader;
    private static boolean shaderFailed;
    private final Vector2 center = new Vector2();
    private final Texture texture;

    public SingleMap(Texture texture) {
        super(texture);
        this.texture = texture;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Group gates = MapStage.getGateLayer();
        Group investigators = MapStage.getInvestigatorLayer();
        float alpha = parentAlpha * getColor().a;
        float gateStrength = LIGHT_INTENSITY * alpha * gates.getColor().a;
        float spotStrength = SPOTLIGHT_INTENSITY * alpha * investigators.getColor().a;
        boolean litGates = gateStrength > 0f && gates.isVisible() && gates.getChildren().size > 0;
        boolean litStands = spotStrength > 0f && investigators.isVisible() && investigators.getChildren().size > 0;
        if ((!litGates && !litStands) || shaderFailed) return;
        if (gateShader == null) {
            gateShader = LitImage.compileLightingShader(GATE_LIGHT);
            shaderFailed = gateShader == null;
            if (shaderFailed) return;
        }

        ShaderProgram previous = batch.getShader();
        float color = batch.getPackedColor();
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
        int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
        batch.setShader(gateShader);
        gateShader.setUniformf("u_tile", getX(), getY(), 1f / getWidth(), 1f / getHeight());
        gateShader.setUniformf("u_mapTint", getColor().r, getColor().g, getColor().b);
        gateShader.setUniformf("u_spot", 0f);
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        if (litGates) {
            // Only the gate matching the current omen lights the map; dormant gates stay dark.
            for (Actor actor : gates.getChildren()) {
                if (!(actor instanceof NewGateAnimatedImage) || !actor.isVisible() || actor.getColor().a <= 0f) continue;
                NewGateAnimatedImage gate = (NewGateAnimatedImage) actor;
                float level = gate.getLightLevel();
                if (level <= 0f) continue;
                actor.localToParentCoordinates(center.set(actor.getWidth() / 2f, actor.getHeight() / 2f));
                float radius = actor.getWidth() * actor.getScaleX() * LIGHT_RADIUS_IN_GATE_WIDTHS;
                drawLight(batch, gate.getLightColor(), gateStrength * level * actor.getColor().a, radius);
            }
        }
        if (litStands) {
            batch.flush();
            gateShader.setUniformf("u_spot", 1f);
            for (Actor actor : investigators.getChildren()) {
                if (!(actor instanceof InvestigatorImage) || !actor.isVisible() || actor.getColor().a <= 0f) continue;
                float level = ((InvestigatorImage) actor).getSpotlight();
                if (level <= 0f) continue;
                actor.localToParentCoordinates(center.set(actor.getWidth() / 2f, actor.getHeight() * 0.35f));
                float radius = actor.getHeight() * Math.abs(actor.getScaleY()) * SPOTLIGHT_RADIUS_IN_STAND_HEIGHTS;
                drawLight(batch, SPOTLIGHT_COLOR, spotStrength * level * actor.getColor().a, radius);
            }
        }
        batch.setShader(previous);
        batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
        batch.setColor(color);
    }

    /** Adds a light disc centered on {@link #center}, skipped when it falls outside this tile. */
    private void drawLight(Batch batch, Color tint, float strength, float radius) {
        if (strength <= 0f || radius <= 0f || center.x + radius <= getX() || center.x - radius >= getRight()
                || center.y + radius <= getY() || center.y - radius >= getTop()) return;
        batch.setColor(tint.r, tint.g, tint.b, strength);
        batch.draw(texture, center.x - radius, center.y - radius, 2f * radius, 2f * radius);
    }
}
