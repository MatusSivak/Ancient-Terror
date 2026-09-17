package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.components.sheet.investigator.CharacterSheetWidgets.*;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** Reserve-style overview of the current party, refreshed each time it opens. */
public class InvestigatorsSheet extends Table {
    private static final int COLUMNS = 4;
    private static final float TILE_WIDTH = 180;
    private static final float GAP = 10;
    private final DisplayHide displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.INVESTIGATORS);
    private Image hitImage;
    private InvestigatorBasics selected;

    public InvestigatorsSheet() {
        setTransform(true);
        displayHide.setActorKey(OnScreenActors.ActorKey.INVESTIGATORS);
        displayHide.setBeforeDisplayAction(() -> {
            InfoStage.getInvestigatorHud().hide().subscribe();
            HudButtons.getTrackHud().hide().subscribe();
        });
        displayHide.setBeforeHideAction(() -> {
            InfoStage.getInvestigatorHud().show().subscribe();
            HudButtons.getTrackHud().show().subscribe();
        });
        displayHide.setAfterHideAction(() -> {
            InvestigatorBasics target = selected;
            selected = null;
            if (target != null && !target.isLostInTimeAndSpace() && target.getLocationId() != null) {
                MapUtils.moveCameraToLocation(target.getLocationId()).subscribe();
            }
        });
    }

    public void init(List<InvestigatorBasics> investigators, InvestigatorId activeInvestigatorId) {
        if (displayHide.isDisplayed()) return;
        clear();
        selected = null;
        getColor().a = 1;
        TextureRegionDrawable background = new TextureRegionDrawable(getTextureRegion(RESERVE_BACKGROUND));
        background.setMinWidth(0);
        background.setMinHeight(0);
        setBackground(background);
        pad(16, 20, 20, 20);

        hitImage = new Image(getTextureRegion(PURE_WHITE_BACKGROUND));
        hitImage.getColor().a = 0;
        addActor(hitImage);
        updateHitBounds();
        // Child clicks bubble here, including labels, tokens and empty panel space.
        // Portrait clicks start closing first, so the guard prevents a second toggle.
        addClickListener(this, () -> {
            if (!BigActorsManager.isLocked() && displayHide.isDisplayed()) {
                BigActorsManager.displayOrHideInvestigators();
            }
        });

        float width = Math.max(380, Math.min(COLUMNS, investigators.size()) * (TILE_WIDTH + GAP) - GAP);
        Table heading = new Table();
        heading.add(rule()).growX().height(1).padRight(12);
        Label title = new Label(get("investigator.overview.title"), new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_CINZEL, 30), INK));
        heading.add(title);
        heading.add(rule()).growX().height(1).padLeft(12);
        add(heading).width(width).padBottom(12).row();

        Table grid = new Table();
        grid.top().left();
        for (int i = 0; i < investigators.size(); i++) {
            grid.add(tile(investigators.get(i), investigators.get(i).getInvestigatorId() == activeInvestigatorId)).width(TILE_WIDTH).top()
                    .padRight((i + 1) % COLUMNS == 0 ? 0 : GAP).padBottom(GAP);
            if ((i + 1) % COLUMNS == 0) grid.row();
        }
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setScrollingDisabled(true, false);
        scroll.setOverscroll(false, false);
        add(scroll).width(width).height(Math.min(390, grid.getPrefHeight())).row();
        Label hint = text(get("investigator.overview.hint"), 16, SECONDARY_INK);
        hint.setWrap(true);
        add(hint).width(width).padTop(8);
        setWidth(width + 40);
        setHeight(getPrefHeight());
        validate();
    }

    private Table tile(InvestigatorBasics investigator, boolean active) {
        Table tile = new Table();
        Image portrait = new Image(getInvestigatorDrawable(investigator.getInvestigatorId()));
        portrait.setScaling(Scaling.fit);
        addClickListener(portrait, () -> {
            if (BigActorsManager.isLocked() || !displayHide.isDisplayed()) return;
            selected = investigator;
            BigActorsManager.displayOrHideInvestigators();
        });
        Color highlight = new Color(0xe8c477ff);
        Drawable frame = getTextureRegionDrawable(PURE_WHITE_BACKGROUND)
                .tint(active ? highlight : new Color(0, 0, 0, 0));
        frame.setMinWidth(0);
        frame.setMinHeight(0);
        Container<Image> framedPortrait = new Container<>(portrait);
        framedPortrait.background(frame).pad(3);
        tile.add(framedPortrait).size(162, 146).padBottom(3).row();
        tile.add(text(active ? get("investigator.overview.active") : "", 15, highlight))
                .height(20).padBottom(2).row();
        Label name = text(investigator.getInvestigatorName(), 20, INK);
        name.setWrap(true);
        tile.add(name).width(TILE_WIDTH).minHeight(46).row();
        Label job = text(get("investigator.profession." + investigator.getInvestigatorId().name().toLowerCase(Locale.ROOT)), 16, SECONDARY_INK);
        job.setWrap(true);
        tile.add(job).width(TILE_WIDTH).minHeight(38).padBottom(7).row();
        tile.add(vital(HEALTH_ICON, investigator.getCurrentHealth(), investigator.getMaxHealth(), new Color(0xf16b60ff)))
                .width(TILE_WIDTH).padBottom(5).row();
        tile.add(vital(SANITY_ICON, investigator.getCurrentSanity(), investigator.getMaxSanity(), new Color(0x7eafffff)))
                .width(TILE_WIDTH).padBottom(8).row();
        Table tokens = new Table();
        tokens.add(counter(CLUE_TOKEN, investigator.getClues())).expandX();
        tokens.add(counter(FOCUS_TOKEN, investigator.getFocusTokens())).expandX().row();
        tokens.add(counter(TICKET_TRAIN_DOWN, Collections.frequency(investigator.getTickets(), PathType.TRAIN))).expandX();
        tokens.add(counter(TICKET_SHIP_DOWN, Collections.frequency(investigator.getTickets(), PathType.SHIP))).expandX();
        tile.add(tokens).width(TILE_WIDTH).row();
        if (investigator.isLostInTimeAndSpace()) {
            Label status = text(get("lostInTimeAndSpace.title"), 14, SECONDARY_INK);
            status.setWrap(true);
            tile.add(status).width(TILE_WIDTH).padTop(5);
        }
        return tile;
    }

    private Table counter(String asset, int count) {
        Table counter = new Table();
        counter.add(icon(asset)).size(28).padRight(5);
        counter.add(text(Integer.toString(count), 20, INK)).minWidth(24);
        return counter;
    }

    private Table vital(String asset, int current, int max, Color color) {
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(new Color(0.12f, 0.12f, 0.12f, 1));
        style.knobBefore = getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(color);
        style.background.setMinWidth(0);
        style.background.setMinHeight(12);
        style.knobBefore.setMinWidth(0);
        style.knobBefore.setMinHeight(12);
        ProgressBar bar = new ProgressBar(0, Math.max(1, max), 1, false, style);
        bar.setValue(current);
        bar.setTouchable(Touchable.disabled);
        Table row = new Table();
        row.add(icon(asset)).size(24).padRight(6);
        row.add(bar).growX().minWidth(0);
        row.add(text(current + "/" + max, 16, color)).width(42);
        return row;
    }

    private Image rule() {
        Image rule = new Image(getTextureRegion(PURE_WHITE_BACKGROUND));
        rule.setColor(0.68f, 0.55f, 0.31f, 0.6f);
        return rule;
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        updateHitBounds();
    }

    private void updateHitBounds() {
        if (hitImage != null) hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    public void displayOrHide() {
        displayHide.setDisplayedY((VIEWPORT_HEIGHT - getHeight()) / 2);
        displayHide.displayOrHide().subscribe();
    }
}
