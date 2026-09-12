package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import java8.features.function.Consumer;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

public class InvestigatorSketches extends Table {

    private LinkedList<InvestigatorSketch> sketches;
    private List<InvestigatorId> disabledInvestigators = new LinkedList<>();

    private List<Consumer<InvestigatorId>> observers = new LinkedList<>();

    public void init(InvestigatorId... investigatorIds) {
        clear();
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        sketches = new LinkedList<>();

        for (int i = 0; i < investigatorIds.length; i++) {
            InvestigatorSketch sketch = new InvestigatorSketch(investigatorIds[i]);
            ButtonUtils.addClickListener(sketch, () -> selectSketch(sketch));
            if (disabledInvestigators.contains(investigatorIds[i])) {
                sketch.clearListeners();
                sketch.showTicked();
            }
            add(sketch).width(175).height(196).pad(3);
            sketches.add(sketch);
            if (i == (investigatorIds.length-1)/2) {
                row();
            }
        }
        pack();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float previousColor = batch.getPackedColor();
        batch.setColor(0.035f, 0.045f, 0.04f, parentAlpha);
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY(), getPrefWidth(), getPrefHeight());
        batch.setColor(previousColor);
        super.draw(batch, parentAlpha);
    }

    private void selectSketch(InvestigatorSketch selectedSketch) {
        if (selectedSketch != null && disabledInvestigators.contains(selectedSketch.getInvestigatorId())) return;
        for (InvestigatorSketch sketch : sketches) {
            if (selectedSketch == sketch) {
                if (!sketch.isSelected()) {
                    sketch.select();
                }
            } else {
                if (!disabledInvestigators.contains(sketch.getInvestigatorId())) {
                    sketch.deselect();
                }

            }
        }

        if (selectedSketch == null) {
            fireInvestigatorSelected(null);
        } else {
            fireInvestigatorSelected(selectedSketch.isSelected() ? selectedSketch.getInvestigatorId() : null);
        }
    }

    public void addObserver(Consumer<InvestigatorId> observer) {
        observers.add(observer);
    }

    private void fireInvestigatorSelected(InvestigatorId selectedInvestigator) {
        IterableUtils.forEach(observers, observer -> observer.accept(selectedInvestigator));
    }

    public void disable(InvestigatorId investigatorId) {
        if (!disabledInvestigators.contains(investigatorId)) disabledInvestigators.add(investigatorId);
        for (InvestigatorSketch sketch : sketches) {
            if (sketch.getInvestigatorId().equals(investigatorId)) {
                sketch.clearListeners();
                sketch.showTicked();
            }
        }
    }
}
