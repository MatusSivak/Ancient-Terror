package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Group;

public class StatChart extends Group {
    private static final Color HEALTH = new Color(0xc55e59ff);
    private static final Color SANITY = new Color(0x658fcaff);
    private static final Color SKILL = new Color(0x659d83ff);
    private static final Color GRID = new Color(0xd1bf8fff);
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final Matrix4 transform = new Matrix4();
    private StatChartData data;

    public void init(StatChartData data) {
        this.data = data;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = parentAlpha * getColor().a;
        batch.end();
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(transform.set(batch.getTransformMatrix()).translate(getX(), getY(), 0));
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) * 0.49f;
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.06f, 0.09f, 0.08f, alpha);
        for (int i = 0; i < 7; i++) triangle(cx, cy, radius, i);
        if (data != null) {
            int[] values = {data.getSanity(), data.getWill(), data.getLore(), data.getInfluence(),
                    data.getObservation(), data.getStrength(), data.getHealth()};
            for (int i = 0; i < 7; i++) {
                Color color = i == 0 ? SANITY : i == 6 ? HEALTH : SKILL;
                renderer.setColor(color.r, color.g, color.b, alpha * 0.78f);
                float maximum = i == 0 || i == 6 ? 8f : 4f;
                triangle(cx, cy, radius * MathUtils.clamp(values[i] / maximum, 0f, 1f), i);
            }
        }
        renderer.end();
        renderer.begin(ShapeRenderer.ShapeType.Line);
        // Four rings: one skill point or two health/sanity points per ring.
        for (int ring = 1; ring <= 4; ring++) {
            renderer.setColor(GRID.r, GRID.g, GRID.b, alpha * (ring == 4 ? 0.65f : 0.25f));
            float r = radius * ring / 4f;
            for (int i = 0; i < 7; i++) {
                renderer.line(cx + dx(i) * r, cy + dy(i) * r,
                        cx + dx(i + 1) * r, cy + dy(i + 1) * r);
            }
        }
        renderer.setColor(GRID.r, GRID.g, GRID.b, alpha * 0.35f);
        for (int i = 0; i < 7; i++) renderer.line(cx, cy, cx + dx(i) * radius, cy + dy(i) * radius);
        renderer.end();
        batch.begin();
    }

    private void triangle(float cx, float cy, float radius, int sector) {
        renderer.triangle(cx, cy, cx + dx(sector) * radius, cy + dy(sector) * radius,
                cx + dx(sector + 1) * radius, cy + dy(sector + 1) * radius);
    }

    private static float dx(int sector) {
        return MathUtils.cosDeg(90f + sector * 360f / 7f);
    }

    private static float dy(int sector) {
        return MathUtils.sinDeg(90f + sector * 360f / 7f);
    }
}