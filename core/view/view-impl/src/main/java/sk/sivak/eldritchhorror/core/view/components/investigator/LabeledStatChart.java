package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class LabeledStatChart extends Group {

    private final Image background;
    private final StatChart statChart;

    public LabeledStatChart() {
        this.statChart = new StatChart();
        background = new Image(CustomAssetManager.getTexture(CustomAssetManager.SKILLS_LABELS));
        addActor(background);
        addActor(statChart);
    }

    public void init(StatChartData statChartData) {
        statChart.init(statChartData);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        background.setSize(getWidth(), getHeight());
        statChart.setSize(getWidth() * 913/1096f, getHeight() * 913/1096f);
        statChart.setPosition((getWidth() - statChart.getWidth())/2f, (getHeight() - statChart.getHeight())/2f);
    }

}
