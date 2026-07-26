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
            add(sketch).width(175).height(220).pad(5);
            sketches.add(sketch);
            if (i == (investigatorIds.length-1)/2) {
                row();
            }
        }
        pack();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(new Color(0.25f, 0.25f, 0.25f, parentAlpha));
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY(), getPrefWidth(), getPrefHeight());
        super.draw(batch, parentAlpha);
    }

    private void selectSketch(InvestigatorSketch selectedSketch) {
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
        disabledInvestigators.add(investigatorId);
        for (InvestigatorSketch sketch : sketches) {
            if (sketch.getInvestigatorId().equals(investigatorId)) {
                sketch.clearListeners();
                sketch.showTicked();
            }
        }
    }
}
