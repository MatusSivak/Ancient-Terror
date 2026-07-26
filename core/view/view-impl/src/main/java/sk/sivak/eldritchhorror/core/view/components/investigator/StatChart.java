package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class StatChart extends Group {

    private final Image background;
    private final ShapeRenderer shapeRenderer;
    private StatChartData statChartData;
    private float parentAlpha;

    StatChart() {
        background = new Image(CustomAssetManager.getTexture(CustomAssetManager.SKILLS));
        background.setScaling(Scaling.fit);
        background.setColor(Color.BLACK);
        background.setSize(0,0);
        shapeRenderer = new ShapeRenderer(3);
        addActor(background);
    }

    public void init(StatChartData statChartData) {
        this.statChartData = statChartData;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.parentAlpha = parentAlpha;
        batch.end();
        drawStats();
        batch.begin();
        super.draw(batch, parentAlpha);
    }

    private void drawStats() {
        if (statChartData == null) {
            return;
        }
        shapeRenderer.setProjectionMatrix(getStage().getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawHealth(statChartData.getHealth());
        drawSanity(statChartData.getSanity());
        drawWil(statChartData.getWill());
        drawLore(statChartData.getLore());
        drawInfluence(statChartData.getInfluence());
        drawObservation(statChartData.getObservation());
        drawStrength(statChartData.getStrength());
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private float getCenterX() {
        return localToStageCoordinates(new Vector2()).x + getWidth() / 2f;
    }

    private float getCenterY() {
        return localToStageCoordinates(new Vector2()).y + getHeight() / 2f - getHeight() * 0.025f;
    }

    private void drawHealth(int amount) {
        drawTriangle(new Color(0xff4040ff), 6, amount);
    }

    private void drawSanity(int amount) {
        drawTriangle(new Color(0x4040ffff), 0, amount);
    }

    private void drawWil(int amount) {
        drawStat(1, amount);
    }

    private void drawLore(int amount) {
        drawStat(2, amount);
    }

    private void drawInfluence(int amount) {
        drawStat(3, amount);
    }

    private void drawObservation(int amount) {
        drawStat(4, amount);
    }

    private void drawStrength(int amount) {
        drawStat(5, amount);
    }

    private void drawStat(int sector, int amount) {
        drawTriangle(new Color(0x40ff40ff), sector, amount*2);
    }

    private void drawTriangle(Color color, int sector, int amount) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.getColor().a = parentAlpha;
        float centerX = getCenterX();
        float centerY = getCenterY();
        float length = getHeight()/2f * 1.01f * (amount/8f);
        shapeRenderer.triangle(
                centerX, centerY,
                centerX + getDirectionX(sector) * length,
                centerY + getDirectionY(sector) * length,
                centerX + getDirectionX(sector+1) * length,
                centerY + getDirectionY(sector+1)  * length);
        shapeRenderer.end();
    }

    private float getDirectionX(int i) {
        return MathUtils.cosDeg((360 / 7f) * i + 90);
    }

    private float getDirectionY(int i) {
        return MathUtils.sinDeg((360 / 7f) * i + 90);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        background.setSize(getWidth(), getHeight());
    }
}
