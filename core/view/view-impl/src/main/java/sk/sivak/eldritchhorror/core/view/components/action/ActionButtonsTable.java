package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.*;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

public class ActionButtonsTable extends Table {

    private final static int CELL_WIDTH = 100;
    private final static int CELL_HEIGHT = 145; //+10
    private final static int CELL_PADDING = 5;

    private ActionButtonClickedListener actionButtonClickedListener;
    private Map<ActionButtonData.ActionButtonId, ActionButtonData> specialActionButtonDataMap;

    public void init(Collection<ActionButtonData> actionButtonDataList) {
        specialActionButtonDataMap = new HashMap<>();
        actionButtonClickedListener = ActionButtonClickedListener.empty;
        clear();
        ArrayList<ActionButtonData> copyOfList = new ArrayList<>(actionButtonDataList);


        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.INVESTIGATOR);
        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.TRAVEL);
        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.REST);
        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.FOCUS);
        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.ACQUIRE_ASSETS);
        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.TICKET);
        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.TRADE);
        addToSpecialActionMap(copyOfList, ActionButtonData.ActionButtonId.SKIP);

        addSpecialButtonToTable(ActionButtonData.ActionButtonId.INVESTIGATOR);
        addSpecialButtonToTable(ActionButtonData.ActionButtonId.TRAVEL);
        addSpecialButtonToTable(ActionButtonData.ActionButtonId.REST);
        addSpecialButtonToTable(ActionButtonData.ActionButtonId.FOCUS);


        Iterator<ActionButtonData> iterator = copyOfList.iterator();
        for (int i = 0; i < Math.round(copyOfList.size() / 2f); i++) {
            ActionButtonData actionButtonData = iterator.next();
            addButtonToTable(actionButtonData);
            iterator.remove();
        }

        row();

        addSpecialButtonToTable(ActionButtonData.ActionButtonId.ACQUIRE_ASSETS);
        addSpecialButtonToTable(ActionButtonData.ActionButtonId.TICKET);
        addSpecialButtonToTable(ActionButtonData.ActionButtonId.TRADE);

        iterator = copyOfList.iterator();
        while (iterator.hasNext()) {
            ActionButtonData actionButtonData = iterator.next();
            addButtonToTable(actionButtonData);
            iterator.remove();
        }
        addSpecialButtonToTable(ActionButtonData.ActionButtonId.SKIP);

        ButtonGroup<ActionButton> buttonGroup = new ButtonGroup<>();

        for (Cell cell : getCells()) {
            buttonGroup.add(((ActionButtonWithText) cell.getActor()).getActionButton());
        }
        buttonGroup.uncheckAll();

        pack();

    }

    private void addToSpecialActionMap(List<ActionButtonData> actionButtonDataList, ActionButtonData.ActionButtonId actionButtonId) {
        if (isPresent(actionButtonDataList, actionButtonId)) {
            ActionButtonData actionButtonById = findActionButtonById(actionButtonDataList, actionButtonId);
            specialActionButtonDataMap.put(actionButtonId, actionButtonById);
            actionButtonDataList.remove(actionButtonById);
        }
    }

    private void addSpecialButtonToTable(ActionButtonData.ActionButtonId actionButtonId) {
        if (specialActionButtonDataMap.get(actionButtonId) != null) {
            addButtonToTable(specialActionButtonDataMap.get(actionButtonId));
        }
    }

    public void setActionButtonClickedListener(ActionButtonClickedListener actionButtonClickedListener) {
        this.actionButtonClickedListener = actionButtonClickedListener;
    }

    private void addButtonToTable(ActionButtonData actionButtonData) {
        ActionButtonWithText buttonWithText = new ActionButtonWithText(actionButtonData);
        ButtonUtils.addClickListener(buttonWithText, () -> onButtonClick(buttonWithText));
        add(buttonWithText).pad(CELL_PADDING).width(CELL_WIDTH).height(CELL_HEIGHT);
    }

    private void onButtonClick(ActionButtonWithText buttonWithText) {
        ActionButtonData actionButtonData = buttonWithText.getActionButton().getActionButtonData();
        buttonWithText.getActionButton().setChecked(true);
        if (actionButtonData.isEnabled()) {
            fireEnabledButtonClickedEvent(actionButtonData);
        } else {
            fireDisabledButtonClickedEvent(actionButtonData);
        }
    }

    private void fireEnabledButtonClickedEvent(ActionButtonData actionButtonData) {
        actionButtonClickedListener.onEnabledButtonClickedEvent(actionButtonData);
    }

    private void fireDisabledButtonClickedEvent(ActionButtonData actionButtonData) {
        actionButtonClickedListener.onDisabledButtonClickedEvent(actionButtonData);
    }

    private ActionButtonData findActionButtonById(List<ActionButtonData> actionButtonDataList, ActionButtonData.ActionButtonId buttonId) {
        return Stream.findFirstOrException(actionButtonDataList, actionButtonData -> buttonId == actionButtonData.getActionButtonId());
    }

    private boolean isPresent(List<ActionButtonData> actionButtonDataList, ActionButtonData.ActionButtonId buttonId) {
        return Stream.anyMatch(actionButtonDataList, actionButtonData -> buttonId == actionButtonData.getActionButtonId());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f * parentAlpha));
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY(), getPrefWidth(), getPrefHeight());
        super.draw(batch, parentAlpha);
    }
}
