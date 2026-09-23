package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.Collection;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class SelectEncounterTable extends Table {

    private DisplayHide displayHide;
    private SingleSubscriber<? super String> onSub;
    private TextButton showButton;
    private LabelTable showLabel;
    private ScrollPane scrollPane;

    public SelectEncounterTable() {
        this.displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.ENCOUNTER_TABLE);
    }

    public void setOnSub(SingleSubscriber<? super String> onSub) {
        this.onSub = onSub;
    }

    private static final int TABLE_WIDTH = 744;
    private static final int CONTENT_WIDTH = TABLE_WIDTH - 64;
    private static final int OPTION_WIDTH = (CONTENT_WIDTH - 12) / 2;

    public void init(Collection<EncounterButtonData> encounterButtonDataList) {
        clear();
        setTransform(true);
        displayHide.setBeforeHideAction(this::beforeHide);
        top();
        NinePatch background = CustomAssetManager.createMenuDialogPatch();
        background.scale(0.5f, 0.5f);
        setBackground(new NinePatchDrawable(background));
        pad(8, 32, 24, 32);
        Label title = new Label(get("encounter.chooseTitle"), new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 30),
                new Color(0xeee1c5ff)));
        title.setAlignment(Align.center);
        add(title).width(CONTENT_WIDTH).height(36).padBottom(18).row();
        Label hint = new Label(get("encounter.chooseHint"), new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 18),
                new Color(0xcbb990ff)));
        hint.setAlignment(Align.center);
        add(hint).width(CONTENT_WIDTH).padBottom(14).row();

        Table buttonsTable = new Table();
        buttonsTable.align(Align.topLeft);
        boolean left = true;
        for (EncounterButtonData encounterButtonData : encounterButtonDataList) {
            EncounterButton encounterButton = new EncounterButton(encounterButtonData);
            encounterButton.setSelectEncounterListener(this);
            buttonsTable.add(encounterButton)
                    .width(OPTION_WIDTH)
                    .minHeight(70)
                    .fillY()
                    .padLeft(left ? 0 : 12)
                    .padBottom(10);
            if (!left) {
                buttonsTable.row();
            }
            left = !left;
        }

        if (!left) {
            buttonsTable.row();
        }
        buttonsTable.pack();
        scrollPane = new ScrollPane(buttonsTable, new ScrollPane.ScrollPaneStyle());
        scrollPane.setScrollingDisabled(true,false);
        scrollPane.setOverscroll(false, false);
        float listHeight = Math.min(282, buttonsTable.getPrefHeight());
        add(scrollPane).height(Math.max(70, listHeight)).width(CONTENT_WIDTH).row();
        if (buttonsTable.getPrefHeight() > listHeight) {
            Label scrollHint = new Label(get("encounter.scrollHint"), hint.getStyle());
            scrollHint.setAlignment(Align.center);
            add(scrollHint).height(22).row();
        }

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                EncounterChoiceDrawable.NORMAL, EncounterChoiceDrawable.PRESSED, null,
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 21));
        style.over = EncounterChoiceDrawable.HOVER;
        style.fontColor = new Color(0xeee1c5ff);
        TextButton hideButton = new TextButton(get("dialog.hide"), style);
        hideButton.pad(6, 18, 6, 18);
        add(hideButton)
                .height(38)
                .width(120)
                .padTop(12);

        ButtonUtils.addClickListener(hideButton, BigActorsManager::displayOrHideEncounterTable);
        pack();
        setWidth(TABLE_WIDTH);



    }

    public void displayOrHide() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2 - getHeight() / 2);
        displayHide.displayOrHide().subscribe();
    }

    private void beforeHide() {
        showButton = ButtonBuilder.buildButton(get("dialog.yes"));
        ButtonUtils.addClickListener(showButton, () -> {
            if (getParent() != null) {
                return;
            }
            BigActorsManager.displayOrHideEncounterTable();
            InfoStage.hideActor(showLabel);
            InfoStage.setBottomHeight(5);
            InfoStage.hideActor(showButton);
        });
        showButton.setPosition(VIEWPORT_WIDTH / 2 - showButton.getWidth() / 2, InfoStage.getBottomHeight());
        showButton.setColor(new Color(1f,1f,1f,0f));
        InfoStage.addSmallActorToInfoStage(showButton);
        showButton.addAction(Actions.alpha(1, 1f));
        InfoStage.setBottomHeight(InfoStage.getBottomHeight() + showButton.getHeight() + 5);
        showLabel = LabelTable.createAndShowTable(1, get("encounter.displayPrompt"));
    }

    public void onSelect(String uuid) {
        if (onSub == null) {
            return;
        }
        displayHide.setBeforeHideAction(() -> {});

        onSub.onSuccess(uuid);
    }
}
