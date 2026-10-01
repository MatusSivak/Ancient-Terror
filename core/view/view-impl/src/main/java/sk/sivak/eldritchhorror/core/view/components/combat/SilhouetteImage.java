package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/** Draws a drawable as a flat silhouette in the actor colour; behind a slightly smaller portrait it reads as a glowing rim. */
public class SilhouetteImage extends Image {

    private static ShaderProgram silhouetteShader;

    public SilhouetteImage(Drawable drawable) {
        super(drawable);
    }

    private static ShaderProgram getShader() {
        if (silhouetteShader == null) {
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
                    + "  gl_FragColor = vec4(v_color.rgb, v_color.a * texture2D(u_texture, v_texCoords).a);\n"
                    + "}\n";
            silhouetteShader = new ShaderProgram(vertex, fragment);
            if (!silhouetteShader.isCompiled()) {
                Gdx.app.error("SilhouetteImage", "Silhouette shader failed: " + silhouetteShader.getLog());
            }
        }
        return silhouetteShader;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!getShader().isCompiled()) {
            return;
        }
        ShaderProgram previous = batch.getShader();
        batch.setShader(silhouetteShader);
        super.draw(batch, parentAlpha);
        batch.setShader(previous);
    }
}
