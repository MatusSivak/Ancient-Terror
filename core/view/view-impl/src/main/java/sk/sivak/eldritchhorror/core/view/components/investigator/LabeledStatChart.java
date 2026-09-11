package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class LabeledStatChart extends Group {

    // Optical corrections for the side icons; leave room below the chart for Influence.
    private static final float[] ICON_OFFSET_X = {0f, 0f, -0.015f, 0f, 0.015f, 0f, 0f};
    private static final float[] ICON_OFFSET_Y = {0f, -0.02f, -0.008f, 0f, -0.008f, -0.02f, 0f};

    // Same counterclockwise sector order as StatChart, starting above the left side.
    private final Image[] statIcons = new Image[7];
    private final StatChart statChart;

    public LabeledStatChart() {
        this.statChart = new StatChart();
        addActor(statChart);
        String[] iconPaths = {
                CustomAssetManager.SANITY_ICON,
                "glyphs/will.png",
                "glyphs/lore.png",
                "glyphs/influence.png",
                "glyphs/observation.png",
                "glyphs/strength.png",
                CustomAssetManager.HEALTH_ICON
        };
        for (int i = 0; i < statIcons.length; i++) {
            Image icon = new Image(CustomAssetManager.getTexture(iconPaths[i]));
            icon.setScaling(Scaling.fit);
            statIcons[i] = icon;
            addActor(icon);
        }
    }

    public void init(StatChartData statChartData) {
        statChart.init(statChartData);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        statChart.setSize(getWidth() * 0.8f, getHeight() * 0.8f);
        statChart.setPosition((getWidth() - statChart.getWidth())/2f, (getHeight() - statChart.getHeight())/2f);
        float iconSize = Math.min(getWidth(), getHeight()) * 0.12f;
        for (int i = 0; i < statIcons.length; i++) {
            float angle = 90f + (i + 0.5f) * 360f / statIcons.length;
            Image icon = statIcons[i];
            icon.setSize(iconSize, iconSize);
            icon.setPosition(
                    getWidth() * (0.5f + 0.44f * MathUtils.cosDeg(angle) + ICON_OFFSET_X[i]),
                    getHeight() * (0.5f + 0.44f * MathUtils.sinDeg(angle) + ICON_OFFSET_Y[i]),
                    Align.center);
        }
    }

}
