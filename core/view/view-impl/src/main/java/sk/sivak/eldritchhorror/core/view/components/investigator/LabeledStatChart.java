package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class LabeledStatChart extends Group {

    // Same counterclockwise sector order as StatChart, starting above the left side.
    private final Image[] statIcons = new Image[7];
    private final Label[] statValues = new Label[7];
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
            Label value = new Label("", new Label.LabelStyle(
                    CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4),
                    new Color(0.95f, 0.89f, 0.73f, 1f)));
            value.setAlignment(Align.center);
            statValues[i] = value;
            addActor(value);
        }
    }

    public void init(StatChartData statChartData) {
        statChart.init(statChartData);
        int[] values = statChartData == null ? null : new int[]{
                statChartData.getSanity(), statChartData.getWill(), statChartData.getLore(),
                statChartData.getInfluence(), statChartData.getObservation(),
                statChartData.getStrength(), statChartData.getHealth()};
        for (int i = 0; i < statValues.length; i++) {
            statValues[i].setText(values == null ? "" : Integer.toString(values[i]));
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        statChart.setSize(getWidth() * 0.68f, getHeight() * 0.68f);
        statChart.setPosition((getWidth() - statChart.getWidth())/2f, (getHeight() - statChart.getHeight())/2f);
        float unit = Math.min(getWidth(), getHeight());
        float iconSize = unit * 0.11f;
        for (int i = 0; i < statIcons.length; i++) {
            float angle = 90f + (i + 0.5f) * 360f / statIcons.length;
            Image icon = statIcons[i];
            icon.setSize(iconSize, iconSize);
            float centerX = getWidth() * (0.5f + 0.42f * MathUtils.cosDeg(angle));
            float centerY = getHeight() * (0.5f + 0.42f * MathUtils.sinDeg(angle));
            icon.setPosition(centerX - unit * 0.09f, centerY - iconSize / 2f);
            Label value = statValues[i];
            value.setFontScale(unit / 900f);
            value.setBounds(centerX + unit * 0.025f, centerY - iconSize / 2f, unit * 0.065f, iconSize);
        }
    }

}
