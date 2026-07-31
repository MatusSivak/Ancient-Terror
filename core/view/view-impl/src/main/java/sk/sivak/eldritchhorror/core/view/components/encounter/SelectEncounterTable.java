package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
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

    private static final int TABLE_WIDTH = 2* (EncounterButton.LABEL_WIDTH + EncounterButton.BUTTON_SIZE + 15) + 90;
    private static final int TABLE_HEIGHT = (int) (1083f/1627f * TABLE_WIDTH);
    private static final int PAD_LEFT = 37;
    private static final int PAD_TOP = 35;

    public void init(Collection<EncounterButtonData> encounterButtonDataList) {
        clear();
        setTransform(true);
        displayHide.setBeforeHideAction(this::beforeHide);
        align(Align.topLeft);
        padTop(PAD_TOP);
        padLeft(PAD_LEFT);
        Image selectEncounterImage = new Image(CustomAssetManager.getTexture("select_encounter_label.png"));
        selectEncounterImage.setScaling(Scaling.fit);
        int imageMaxWidth = 2 * (EncounterButton.LABEL_WIDTH + EncounterButton.BUTTON_SIZE + 15) + 5;
        float imageHeight = 130/903f * imageMaxWidth;
        add(selectEncounterImage)
                .colspan(2)
                .height(80)
                .padLeft(10)
                .padRight(10)
                .maxWidth(imageMaxWidth)
                .height(imageHeight)
                .row();

        Table buttonsTable = new Table();
        buttonsTable.align(Align.topLeft);
        boolean left = true;
        for (EncounterButtonData encounterButtonData : encounterButtonDataList) {
            EncounterButton encounterButton = new EncounterButton(encounterButtonData);
            encounterButton.setSelectEncounterListener(this);
            buttonsTable.add(encounterButton)
                    .width(EncounterButton.LABEL_WIDTH + EncounterButton.BUTTON_SIZE + 15)
                    .height(EncounterButton.BUTTON_SIZE + 10)
                    .padLeft(left?0:5)
                    .padBottom(5);
            if (!left) {
                buttonsTable.row();
            }
            left = !left;
        }

        if (!left) {
            buttonsTable.row();
        }
        scrollPane = new ScrollPane(buttonsTable);
        scrollPane.setScrollingDisabled(true,false);
        add(scrollPane).height(220).width(595).align(Align.topLeft).row();

        TextButton hideButton = ButtonBuilder.buildButton(get("dialog.hide"));
        add(hideButton)
                .height(hideButton.getHeight())
                .width(hideButton.getWidth())
                .growY()
                .padBottom(34)
                .align(Align.bottom)
                .colspan(2);

        ButtonUtils.addClickListener(hideButton, BigActorsManager::displayOrHideEncounterTable);
        pack();
        // 1627 x 1083
        setSize(TABLE_WIDTH, TABLE_HEIGHT);
        CustomAssetManager.getTextureAsync("wooden_background.png").subscribe(ok -> {
            TextureRegionDrawable textureRegionDrawable = CustomAssetManager.getTextureRegionDrawable("wooden_background.png");
            setBackground(textureRegionDrawable);
        });



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
