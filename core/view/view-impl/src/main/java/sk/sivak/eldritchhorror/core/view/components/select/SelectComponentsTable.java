package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java8.features.function.Consumer;
import java8.features.function.Function;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

public class SelectComponentsTable<Key> extends Table {

    private LinkedList<SelectComponent<Key>> selectComponentList;
    private List<Key> disabledKeys = new LinkedList<>();

    private List<Consumer<Key>> observers = new LinkedList<>();

    private Function<Float, Color> backgroundColorFn;
    private String backgroundTexture;

    public void init(List<Key> availableKeys) {
        clear();
        selectComponentList = new LinkedList<>();
        for (Key key : availableKeys) {
            SelectComponent<Key> selectComponent = SelectComponentBuilder.build(key);
            ButtonUtils.addClickListener(selectComponent.getActor(), () -> onComponentClick(selectComponent));
            add(selectComponent.getActor())
                    .width(selectComponent.getInTableWidth(availableKeys.size()))
                    .height(selectComponent.getInTableHeight(availableKeys.size())).pad(5);
            selectComponentList.add(selectComponent);
        }
        for (SelectComponent<Key> selectComponent : selectComponentList) {
            selectComponent.setOtherSelectComponents(findOtherComponents(selectComponent));
        }
        pack();
    }

    public void setBackgroundColorFn(Function<Float, Color> backgroundColorFn) {
        this.backgroundColorFn = backgroundColorFn;
    }

    public void setBackgroundTexture(String backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    private ArrayList<SelectComponent<Key>> findOtherComponents(SelectComponent<Key> selectComponent) {
        ArrayList<SelectComponent<Key>> otherComponents = new ArrayList<>(selectComponentList);
        otherComponents.remove(selectComponent);
        return otherComponents;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawBackground(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }

    private void drawBackground(Batch batch, float parentAlpha) {
        batch.setColor(backgroundColorFn.apply(parentAlpha * getColor().a));
        batch.draw(getTexture(backgroundTexture), getX(), getY(), getPrefWidth(), getPrefHeight());
    }

    private void onComponentClick(SelectComponent<Key> selectedComponent) {
        for (SelectComponent<Key> other : selectComponentList) {
            if (selectedComponent == other) {
                if (!other.isSelected()) {
                    other.select();
                } else {
                    other.reselect();
                }
            } else {
                if (!disabledKeys.contains(other.getKey())) {
                    other.deselect();
                }

            }
        }

        if (selectedComponent == null) {
            fireComponentSelected(null);
        } else {
            fireComponentSelected(selectedComponent.isSelected() ? selectedComponent.getKey() : null);
        }
    }

    public void addObserver(Consumer<Key> observer) {
        observers.add(observer);
    }

    private void fireComponentSelected(Key selectedKey) {
        IterableUtils.forEach(observers, observer -> observer.accept(selectedKey));
    }

    public void disable(Key key) {
        disabledKeys.add(key);
        for (SelectComponent<Key> other : selectComponentList) {
            if (other.getKey().equals(key)) {
                other.getActor().clearListeners();
                other.showTicked();
            }
        }
    }
}
