package sk.sivak.eldritchhorror.core.view.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/** A rotating triangle sampling a shared texture; owns no GPU resources. */
public final class TextureShard extends Actor {
    private final Texture texture;
    private final float[] triangle, vertices = new float[20];
    private final boolean framebuffer;

    public TextureShard(Texture texture, float width, float height, float[] triangle, boolean framebuffer) {
        this.texture = texture; this.triangle = triangle; this.framebuffer = framebuffer;
        setSize(width, height);
        setOrigin((triangle[0] + triangle[2] + triangle[4]) * width / 3,
                (triangle[1] + triangle[3] + triangle[5]) * height / 3);
        setTouchable(Touchable.disabled);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float alpha = getColor().a * parentAlpha;
        float rgb = framebuffer ? alpha : 1;
        float packed = Color.toFloatBits(getColor().r * rgb, getColor().g * rgb, getColor().b * rgb, alpha);
        float cos = MathUtils.cosDeg(getRotation()), sin = MathUtils.sinDeg(getRotation());
        for (int i = 0; i < 4; i++) {
            int point = Math.min(i, 2) * 2, v = i * 5;
            float u = triangle[point], y = triangle[point + 1];
            float dx = (u * getWidth() - getOriginX()) * getScaleX();
            float dy = (y * getHeight() - getOriginY()) * getScaleY();
            vertices[v] = getX() + getOriginX() + dx * cos - dy * sin;
            vertices[v + 1] = getY() + getOriginY() + dx * sin + dy * cos;
            vertices[v + 2] = packed; vertices[v + 3] = u; vertices[v + 4] = framebuffer ? y : 1 - y;
        }
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
        int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
        if (framebuffer) batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(texture, vertices, 0, vertices.length);
        if (framebuffer) batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
    }
}
