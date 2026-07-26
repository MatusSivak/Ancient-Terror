package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;

public interface SelectComponent<Key> {

    Actor getActor();

    Key getKey();

    boolean isSelected();

    void select();

    void deselect();

    void showTicked();

    int getInTableWidth(int listSize);

    int getInTableHeight(int listSize);

    void reselect();

    void setOtherSelectComponents(List<SelectComponent<Key>> otherSelectComponents);
}
