package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.encounter.CombatEncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private static final int TABLE_WIDTH = 700;
    private static final int CONTENT_WIDTH = TABLE_WIDTH - 64;
    private static final int OPTION_WIDTH = (CONTENT_WIDTH - 12) / 2;
    private static final int COMPACT_MAX_OPTIONS = 4;
    private static final int CLOSE_SIZE = 30;
    private static final int CLOSE_INSET_X = 24;
    private static final int CLOSE_INSET_TOP = 9;
    private Actor closeButton;
    private static final String DEFEAT_MONSTERS_REASON = "encounter.disabled.defeatMonsters";
    private static final String NON_EPIC_FIRST_REASON = "encounter.disabled.nonEpicFirst";

    /** Drops a disabled Skip and moves the options that must be resolved first to the top. */
    private static List<EncounterButtonData> visibleOptions(Collection<EncounterButtonData> all) {
        List<EncounterButtonData> options = new ArrayList<>();
        for (EncounterButtonData data : all) {
            if (!data.isEnabled() && EncounterIconStyle.isSkip(data)) {
                continue;
            }
            options.add(data);
        }
        if (isCombatRequired(options)) {
            List<EncounterButtonData> sorted = new ArrayList<>();
            for (EncounterButtonData data : options) {
                if (isRequiredCombat(data)) {
                    sorted.add(data);
                }
            }
            for (EncounterButtonData data : options) {
                if (!isRequiredCombat(data)) {
                    sorted.add(data);
                }
            }
            return sorted;
        }
        return options;
    }

    private static boolean isCombatRequired(Collection<EncounterButtonData> options) {
        for (EncounterButtonData data : options) {
            if (!data.isEnabled() && (DEFEAT_MONSTERS_REASON.equals(data.getDisabledReason())
                    || NON_EPIC_FIRST_REASON.equals(data.getDisabledReason()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean blocksForMonsters(Collection<EncounterButtonData> options) {
        for (EncounterButtonData data : options) {
            if (!data.isEnabled() && DEFEAT_MONSTERS_REASON.equals(data.getDisabledReason())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void layout() {
        super.layout();
        if (closeButton != null) {
            closeButton.setBounds(getWidth() - CLOSE_SIZE - CLOSE_INSET_X, getHeight() - CLOSE_SIZE - CLOSE_INSET_TOP,
                    CLOSE_SIZE, CLOSE_SIZE);
            closeButton.toFront();
        }
    }

    /** Ornate gold cross in the header; brightens while hovered. */
    private static final class CloseCross extends Actor {
        private static final Color NORMAL = new Color(0xbda16aff);
        private static final Color OVER = new Color(0xf0dca8ff);
        private final ClickListener hover = new ClickListener();

        CloseCross() {
            addListener(hover);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color before = batch.getColor();
            float r = before.r, g = before.g, b = before.b, a = before.a;
            Color tint = hover.isOver() ? OVER : NORMAL;
            float alpha = getColor().a * parentAlpha;
            batch.setColor(0.07f, 0.12f, 0.11f, alpha);
            batch.draw(EncounterIconStyle.disc(), getX(), getY(), getWidth(), getHeight());
            batch.setColor(tint.r, tint.g, tint.b, alpha);
            batch.draw(EncounterIconStyle.ring(), getX(), getY(), getWidth(), getHeight());
            float inset = getWidth() * 0.27f;
            batch.draw(EncounterIconStyle.cross(), getX() + inset, getY() + inset, getWidth() - 2 * inset, getHeight() - 2 * inset);
            batch.setColor(r, g, b, a);
        }
    }

    private static boolean isRequiredCombat(EncounterButtonData data) {
        return data.isEnabled() && data instanceof CombatEncounterButtonData;
    }

    public void init(Collection<EncounterButtonData> encounterButtonDataList) {
        clear();
        setTransform(true);
        displayHide.setBeforeHideAction(this::beforeHide);
        top();
        NinePatch background = CustomAssetManager.createMenuDialogPatch();
        background.scale(0.5f, 0.5f);
        setBackground(new NinePatchDrawable(background));
        pad(8, 32, 24, 32);
        List<EncounterButtonData> options = visibleOptions(encounterButtonDataList);
        boolean combatRequired = isCombatRequired(options);
        boolean monstersBlock = blocksForMonsters(options);
        int requiredCount = 0;
        for (EncounterButtonData data : options) {
            if (isRequiredCombat(data)) {
                requiredCount++;
            }
        }
        combatRequired = combatRequired && requiredCount > 0;
        String heading = combatRequired
                ? get(requiredCount > 1 ? "encounter.defeatTitle.many" : "encounter.defeatTitle.one")
                : get("encounter.chooseTitle");
        Label title = new Label(heading, new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 30),
                new Color(0xeee1c5ff)));
        title.setAlignment(Align.center);
        add(title).width(CONTENT_WIDTH - 2 * CLOSE_SIZE).height(36).padBottom(monstersBlock ? 12 : 24).row();
        Label.LabelStyle hintStyle = new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 18),
                new Color(0xcbb990ff));
        if (monstersBlock) {
            Label hint = new Label(get("encounter.defeatHint"), hintStyle);
            hint.setAlignment(Align.center);
            hint.setWrap(true);
            add(hint).width(CONTENT_WIDTH).padBottom(14).row();
        }

        Table buttonsTable = new Table();
        buttonsTable.align(Align.topLeft);
        boolean compact = options.size() <= COMPACT_MAX_OPTIONS;
        boolean left = true;
        for (EncounterButtonData encounterButtonData : options) {
            boolean required = combatRequired && isRequiredCombat(encounterButtonData);
            boolean hideReason = DEFEAT_MONSTERS_REASON.equals(encounterButtonData.getDisabledReason());
            EncounterButton encounterButton = new EncounterButton(encounterButtonData, compact, required, hideReason);
            encounterButton.setSelectEncounterListener(this);
            buttonsTable.add(encounterButton)
                    .width(OPTION_WIDTH)
                    .minHeight(compact ? 64 : 70)
                    .fillY()
                    .padLeft(left ? 0 : 12)
                    .padBottom(compact ? 8 : 10);
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
            Label scrollHint = new Label(get("encounter.scrollHint"), hintStyle);
            scrollHint.setAlignment(Align.center);
            add(scrollHint).height(22).row();
        }

        closeButton = new CloseCross();
        ButtonUtils.addClickListener(closeButton, BigActorsManager::displayOrHideEncounterTable);
        addActor(closeButton);
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
