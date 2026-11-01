package sk.sivak.eldritchhorror.core.view.reward;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.displayasset.RewardOfferType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.SoundDialog;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
import sk.sivak.eldritchhorror.core.view.utils.StoreDialogStyle;

import java.util.ArrayList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** Asks a free player whether to rate the game or watch a video in exchange for one random locked card. */
public final class RewardOfferDialog extends SoundDialog {
    static final String CARD_BACK = "card/card_template_locked.jpg";
    private static final int STARS = 5;
    private static final float CARD_HEIGHT = 138f;
    private static final float CARD_FRAME_PAD = 4f;

    private final SingleSubscriber<? super Boolean> subscriber;
    private final List<Image> stars = new ArrayList<>();
    private Image glow;
    private boolean answered;

    private RewardOfferDialog(RewardOfferType type, int lockedCards, SingleSubscriber<? super Boolean> subscriber) {
        super(get(prefix(type) + ".title"), StoreDialogStyle.window());
        this.subscriber = subscriber;
        setModal(true);
        setMovable(false);
        setTransform(true);
        setKeepWithinStage(false);
        getTitleLabel().setFontScale(0.38f);
        getTitleLabel().setAlignment(Align.center);
        getContentTable().pad(6f, 14f, 0f, 14f);

        Table offer = new Table();
        offer.add(mysteryCard(type)).size(170f, 179f).padRight(18f);
        Table details = new Table();
        if (type == RewardOfferType.RATE_GAME) details.add(starRow()).padBottom(10f).row();
        Label description = StoreDialogStyle.label(get(prefix(type) + ".description"), 0.26f);
        details.add(description).width(300f).padBottom(10f).row();
        if (lockedCards > 0) {
            Label remaining = StoreDialogStyle.label(get("reward.lockedCards", lockedCards), 0.22f);
            remaining.setColor(StoreDialogStyle.MUTED);
            details.add(remaining).width(300f);
        }
        offer.add(details).width(300f);
        getContentTable().add(offer).padBottom(8f);

        TextButton accept = StoreDialogStyle.primaryButton(get(prefix(type) + ".accept"));
        if (type == RewardOfferType.WATCH_AD) {
            Image play = icon("icon/play.png");
            accept.clearChildren();
            accept.add(play).size(30f).padRight(10f);
            accept.add(accept.getLabel());
        }
        TextButton decline = StoreDialogStyle.textLink(get(prefix(type) + ".decline"), 0.24f);
        getButtonTable().defaults().pad(1f, 6f, 1f, 6f);
        button(accept, true);
        getButtonTable().getCell(accept).width(420f).height(54f);
        getButtonTable().row();
        button(decline, false);
        getButtonTable().getCell(decline).width(170f).height(30f).padBottom(2f);
        key(Input.Keys.ESCAPE, false);
        key(Input.Keys.BACK, false);
    }

    public static Single<Boolean> show(Stage stage, RewardOfferType type, int lockedCards) {
        return Single.create(subscriber -> {
            if (stage == null) {
                subscriber.onSuccess(false);
                return;
            }
            new RewardOfferDialog(type, lockedCards, subscriber).show(stage);
        });
    }

    static String prefix(RewardOfferType type) {
        return type == RewardOfferType.RATE_GAME ? "reward.rate" : "reward.video";
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        super.show(stage, action);
        StoreDialogStyle.fitToStage(this);
        animate();
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        StoreDialogStyle.fitToStage(this);
    }

    @Override
    protected void result(Object answer) {
        if (answered) return;
        answered = true;
        subscriber.onSuccess(Boolean.TRUE.equals(answer));
    }

    private Stack mysteryCard(RewardOfferType type) {
        Stack stack = new Stack();
        glow = icon("icon/light.png");
        glow.setColor(new Color(1f, 0.78f, 0.38f, 0.55f));
        stack.add(glow);
        Table framed = new Table();
        framed.setBackground(SelectionPanelStyle.panel("0C1714", "89774B"));
        framed.pad(CARD_FRAME_PAD);
        Image cardBack = icon(CARD_BACK);
        float cardWidth = cardWidthFor(CARD_HEIGHT, cardBack.getPrefWidth(), cardBack.getPrefHeight());
        framed.add(cardBack).size(cardWidth, CARD_HEIGHT);
        Table inset = new Table();
        inset.add(framed);
        stack.add(inset);
        if (type == RewardOfferType.WATCH_AD) {
            Table badge = new Table();
            badge.add(icon("icon/play.png")).size(54f).expand().bottom().right().pad(0f, 0f, 4f, 2f);
            stack.add(badge);
        }
        return stack;
    }

    static float cardWidthFor(float height, float textureWidth, float textureHeight) {
        return height * textureWidth / textureHeight;
    }

    private Table starRow() {
        Table row = new Table();
        for (int i = 0; i < STARS; i++) {
            Stack slot = new Stack();
            slot.add(icon("icon/star_empty.png"));
            Image star = icon("icon/star.png");
            stars.add(star);
            slot.add(star);
            row.add(slot).size(40f).pad(0f, 3f, 0f, 3f);
        }
        return row;
    }

    private void animate() {
        validate();
        for (int i = 0; i < stars.size(); i++) {
            Actor star = stars.get(i);
            star.setOrigin(Align.center);
            star.setScale(0f);
            star.addAction(Actions.sequence(Actions.delay(0.25f + i * 0.12f),
                    Actions.scaleTo(1f, 1f, 0.28f, Interpolation.swingOut)));
        }
        if (glow != null) {
            glow.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(0.3f, 1.2f, Interpolation.sine),
                    Actions.alpha(0.6f, 1.2f, Interpolation.sine))));
        }
    }

    private static Image icon(String texture) {
        Image image = new Image(CustomAssetManager.getTexture(texture));
        image.setScaling(Scaling.fit);
        return image;
    }
}
