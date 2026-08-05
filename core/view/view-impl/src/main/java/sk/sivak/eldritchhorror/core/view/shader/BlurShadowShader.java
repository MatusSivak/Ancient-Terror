package sk.sivak.eldritchhorror.core.view.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class BlurShadowShader {

    private static final String VERTEX_SHADER =
            "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "\n" +
            "uniform mat4 u_projTrans;\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "\n" +
            "void main() {\n" +
            "    v_color = a_color;\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}";

    // 5x5 Gaussian blur kernel — accumulates only alpha, outputs black shadow.
    // u_texelSize: (1/textureWidth, 1/textureHeight)
    // Kernel step is 3 texels so the blur is visually noticeable.
    private static final String FRAGMENT_SHADER =
            "#ifdef GL_ES\n" +
            "    precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform vec2 u_texelSize;\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 s = u_texelSize * 3.0;\n" +
            "    float a = 0.0;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-2.0, -2.0) * s).a * 0.00391;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-1.0, -2.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 0.0, -2.0) * s).a * 0.02344;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 1.0, -2.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 2.0, -2.0) * s).a * 0.00391;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-2.0, -1.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-1.0, -1.0) * s).a * 0.06250;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 0.0, -1.0) * s).a * 0.09375;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 1.0, -1.0) * s).a * 0.06250;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 2.0, -1.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-2.0,  0.0) * s).a * 0.02344;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-1.0,  0.0) * s).a * 0.09375;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 0.0,  0.0) * s).a * 0.14063;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 1.0,  0.0) * s).a * 0.09375;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 2.0,  0.0) * s).a * 0.02344;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-2.0,  1.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-1.0,  1.0) * s).a * 0.06250;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 0.0,  1.0) * s).a * 0.09375;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 1.0,  1.0) * s).a * 0.06250;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 2.0,  1.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-2.0,  2.0) * s).a * 0.00391;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2(-1.0,  2.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 0.0,  2.0) * s).a * 0.02344;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 1.0,  2.0) * s).a * 0.01563;\n" +
            "    a += texture2D(u_texture, v_texCoords + vec2( 2.0,  2.0) * s).a * 0.00391;\n" +
            "    gl_FragColor = vec4(0.35, 0.0, 0.0, a * v_color.a);\n" +
            "}";

    private static ShaderProgram instance;

    public static ShaderProgram get() {
        if (instance == null) {
            instance = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        }
        return instance;
    }
}
