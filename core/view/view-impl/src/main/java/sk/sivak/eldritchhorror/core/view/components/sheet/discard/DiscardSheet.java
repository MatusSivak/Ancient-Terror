package sk.sivak.eldritchhorror.core.view.components.sheet.discard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.CardClickListener;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_CINZEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;

public class DiscardSheet extends VisTable {

    private static final float CARD_SCALE = 0.133f;
    private static final int VISIBLE_CARDS = 4;
    private static final float CARD_GAP = 6f;
    private static final float PANEL_PADDING = 20f;
    private static final float CONTENT_WIDTH = VISIBLE_CARDS * CardTemplate.CARD_WIDTH * CARD_SCALE
            + (VISIBLE_CARDS - 1) * CARD_GAP;

    private final DisplayHide displayHide;
    private final Label discardLabel;
    private Image hitImage;

    public DiscardSheet() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.DISCARD);
        displayHide.setActorKey(OnScreenActors.ActorKey.DISCARD_CARD);
        setTransform(true);
        discardLabel = new Label("", new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_CINZEL, 30), new Color(0.91f, 0.83f, 0.62f, 1f)));
        discardLabel.setAlignment(Align.center);
        discardLabel.setWrap(true);
        discardLabel.setTouchable(Touchable.disabled);
    }

    public void init(List<CardInfo> discardedCards, Action0 onClickAction) {
        clear();
        align(Align.top);
        getColor().a = 1f;
        TextureRegionDrawable background = new TextureRegionDrawable(
                CustomAssetManager.getTextureRegion(CustomAssetManager.RESERVE_BACKGROUND));
        background.setMinWidth(0);
        background.setMinHeight(0);
        setBackground(background);
        pad(16, PANEL_PADDING, 20, PANEL_PADDING);

        List<CardTemplate> cardTemplates = toCardTemplates(discardedCards);
        createListeners(cardTemplates);
        addHitImage(onClickAction);

        discardLabel.setText(get("discard.label"));
        Table heading = new Table();
        heading.add(createHeaderRule()).growX().height(1).padRight(16);
        heading.add(discardLabel).width(CONTENT_WIDTH * 0.56f);
        heading.add(createHeaderRule()).growX().height(1).padLeft(16);
        add(heading).width(CONTENT_WIDTH).padBottom(14).row();

        Table cards = new Table();
        cards.center();
        for (int i = 0; i < cardTemplates.size(); i++) {
            CardTemplate cardTemplate = cardTemplates.get(i);
            cardTemplate.setScale(CARD_SCALE);
            cards.add(cardTemplate).top()
                    .width(CardTemplate.CARD_WIDTH * CARD_SCALE)
                    .height(CardTemplate.CARD_HEIGHT * CARD_SCALE)
                    .padRight(i == cardTemplates.size() - 1 ? 0 : CARD_GAP);
        }
        if (cardTemplates.isEmpty()) {
            Label emptyLabel = new Label(get("discard.empty"), new Label.LabelStyle(
                    getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 18), new Color(0.81f, 0.79f, 0.69f, 1f)));
            emptyLabel.setAlignment(Align.center);
            emptyLabel.setWrap(true);
            cards.add(emptyLabel).width(CONTENT_WIDTH - 40);
        }

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.hScroll = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND)
                .tint(new Color(0.12f, 0.16f, 0.14f, 1f));
        scrollStyle.hScrollKnob = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND)
                .tint(new Color(0.65f, 0.55f, 0.32f, 1f));
        scrollStyle.hScroll.setMinHeight(4);
        scrollStyle.hScrollKnob.setMinHeight(4);
        scrollStyle.hScrollKnob.setMinWidth(35);
        ScrollPane scrollPane = new ScrollPane(cards, scrollStyle);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        // Keep a single row, with a visible scrollbar only when the pile exceeds four cards.
        add(scrollPane).width(CONTENT_WIDTH)
                .height(CardTemplate.CARD_HEIGHT * CARD_SCALE + (cardTemplates.size() > VISIBLE_CARDS ? 8 : 0));

        setWidth(CONTENT_WIDTH + PANEL_PADDING * 2);
        invalidateHierarchy();
        setHeight(getPrefHeight());
        validate();
        setHeight(getPrefHeight());
        validate();
    }


    private Image createHeaderRule() {
        Image rule = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        rule.setColor(0.68f, 0.55f, 0.31f, 0.6f);
        rule.setTouchable(Touchable.disabled);
        return rule;
    }

    private void addHitImage(Action0 onClickAction) {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.getColor().a = 0.0f;
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        if (onClickAction != null) {
            addClickListener(hitImage, onClickAction::call);
        }
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        if (hitImage != null) {
            hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        }
    }

    private List<CardTemplate> toCardTemplates(List<CardInfo> discardedCards) {
        List<CardTemplate> cardTemplates = new LinkedList<>();

        for (CardInfo discardedCard : discardedCards) {
            cardTemplates.add(CardTemplate.buildCard(discardedCard));
        }
        return cardTemplates;
    }

    private List<CardClickListener> createListeners(List<CardTemplate> cardTemplates) {
        List<CardClickListener> listeners = new LinkedList<>();
        for (CardTemplate currentTemplate : cardTemplates) {
            CardClickListener listener = new CardClickListener();
            listeners.add(listener);
            listener.setAllTemplates(cardTemplates);
            currentTemplate.addListener(listener);
        }

        for (CardClickListener currentListener : listeners) {
            currentListener.setOtherListeners(listeners);
        }

        return listeners;
    }

    public void displayOrHide(Action0 action) {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2 - getHeight() / 2);

        displayHide.setBeforeDisplayAction(() -> {
            InfoStage.getInvestigatorHud().hide().subscribe();
            HudButtons.getTrackHud().hide().subscribe();
        });

        displayHide.setBeforeHideAction(() -> {
            InfoStage.getInvestigatorHud().show().subscribe();
            HudButtons.getTrackHud().show().subscribe();
        });

        displayHide.displayOrHide().subscribe(() -> {
            if (action != null) {
                action.call();
            }
        });
    }

}
