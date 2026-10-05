package sk.sivak.eldritchhorror.core.view.components.sheet.ancientone;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** Azathoth-style parchment sheet with all current rules visible at once. */
public final class YigCard extends VisTable implements AncientOneCard {
    private static final Color INK = Color.valueOf("30271E");
    private static final Color DANGER = Color.valueOf("8C292D");
    private final DisplayHide displayHide;
    private AncientOneInfo info;
    private Label power;
    private final Image[] tokenMarkers = new Image[8];
    private Action0 beforeDisplay;

    public YigCard() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.ANCIENT_ONE);
        displayHide.setActorKey(OnScreenActors.ActorKey.ANCIENT_ONE_CARD);
        setTransform(true);
    }

    @Override public void init(AncientOneInfo info) {
        this.info = info;
        clear();
        power = null;
        java.util.Arrays.fill(tokenMarkers, null);
        pad(16, 36, 12, 36);
        setBackground(getTextureRegionDrawable(ANCIENT_ONE_DIALOG_BACKGROUND));
        Image art = new Image(getTexture("ancient_one/yig-sheet-art.png"));
        art.setScaling(Scaling.fit);
        add(art).colspan(2).width(696).height(220).padBottom(4).row();
        add(createLore()).width(278).top().padRight(18);
        add(createRules()).width(400).top();
        setSize(1067 * AzathothCard.SCALE, 748 * AzathothCard.SCALE);
        AncientOneStyles.addCloseButton(this);
        updatePower(info.getPower());
    }

    private Table createLore() {
        Table lore = new Table();
        lore.defaults().growX();
        lore.add(AncientOneStyles.title(get(info.getName()))).height(46).row();
        Label subtitle = label(get(info.getAltName()), 17, AncientOneStyles.BRONZE);
        subtitle.setAlignment(Align.center);
        lore.add(subtitle).padBottom(10).row();
        lore.add(AncientOneStyles.divider()).height(1).padBottom(12).row();
        Label flavor = label(get(info.getFlavorText()), 12, Color.DARK_GRAY);
        flavor.setAlignment(Align.top | Align.center);
        lore.add(flavor).width(278).padBottom(14).row();
        if (info.isAwaken()) {
            power = label("", 17, DANGER);
            power.setAlignment(Align.center);
            lore.add(power).padTop(4).padBottom(8).row();
            Table tokens = new Table();
            for (int i = 0; i < tokenMarkers.length; i++) {
                Image marker = new Image(getTextureRegionDrawable(COMPASS));
                marker.setScaling(Scaling.fit);
                tokenMarkers[i] = marker;
                tokens.add(marker).size(25).pad(3);
            }
            lore.add(tokens).row();
        }
        return lore;
    }

    private Table createRules() {
        Table rules = new Table();
        if (info.isAwaken()) {
            addRule(rules, get("ancientOne.selection.special"), null, get(info.getSpecialText()), false);
        }
        Image reckoning = new Image(getTextureRegionDrawable(RECKONING));
        reckoning.setScaling(Scaling.fit);
        addRule(rules, null, reckoning, get(info.getReckoningText()), false);
        if (!info.isAwaken()) {
            DoomTrackWidget clock = new DoomTrackWidget();
            clock.updateDoom(0);
            clock.setScale(42 / 593f);
            addRule(rules, null, clock, get(info.getMidnightText()), true);
        }
        addRule(rules, get("ancientOne.victory"), null, get(info.getWinText()), false);
        return rules;
    }

    private void addRule(Table rules, String heading, Actor icon, String text, boolean danger) {
        Table marker = new Table();
        if (icon != null) marker.add(icon).size(42);
        if (heading != null) {
            Label headingLabel = label(heading, 15, AncientOneStyles.BRONZE);
            headingLabel.setAlignment(Align.center);
            marker.add(headingLabel).width(68);
        }
        Label value = label(text, 14, danger ? DANGER : INK);
        rules.add(marker).width(68).padRight(10).minHeight(44).padBottom(9);
        rules.add(value).width(312).padBottom(9).row();
    }

    private Label label(String text, int size, Color color) {
        Label label = new Label(text, new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, AncientOneStyles.RULE_FONT_SIZE), color));
        label.setFontScale(size / (float) AncientOneStyles.RULE_FONT_SIZE);
        label.setWrap(true);
        label.setAlignment(Align.left);
        return label;
    }

    private void updatePower(int value) {
        if (power == null) return;
        power.setText(get("ancientOne.yig.tokens", value));
        for (int i = 0; i < tokenMarkers.length; i++) {
            tokenMarkers[i].setColor(i < value ? Color.WHITE : new Color(0.35f, 0.3f, 0.25f, 0.22f));
        }
    }

    @Override public Actor getActor() { return this; }
    @Override public void setAfterHideAction(Action0 action) { displayHide.setAfterHideAction(action); }
    @Override public void setAfterDisplayAction(Action0 action) { displayHide.setAfterDisplayAction(action); }
    @Override public void setBeforeDisplayAction(Action0 action) { beforeDisplay = action; }
    @Override public void displayOrHide() {
        updatePower(info.getPower());
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2f - getHeight() / 2f);
        displayHide.setBeforeDisplayAction(beforeDisplay != null ? beforeDisplay : () -> {
            InfoStage.getInvestigatorHud().hide().subscribe();
            HudButtons.getTrackHud().hide().subscribe();
        });
        displayHide.setBeforeHideAction(() -> {
            InfoStage.getInvestigatorHud().show().subscribe();
            HudButtons.getTrackHud().show().subscribe();
        });
        displayHide.displayOrHide().subscribe();
    }

    @Override public Completable increaseAncientOnePower(int increment) {
        return Completable.create(subscriber -> {
            updatePower(Math.max(0, info.getPower() + increment));
            setAfterHideAction(subscriber::onCompleted);
        });
    }
}
