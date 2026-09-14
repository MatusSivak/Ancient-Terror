package sk.sivak.eldritchhorror.core.view.components.sheet.reserve;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import java8.features.function.Consumer;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
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
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;

public class ReserveSheet extends VisTable {

    private static final float CARD_SCALE = 0.133f;
    private static final int CARDS_PER_ROW = 4;
    private static final float CARD_GAP = 6f;
    private static final float PANEL_PADDING = 20f;
    private static final float CONTENT_WIDTH = CARDS_PER_ROW * CardTemplate.CARD_WIDTH * CARD_SCALE
            + (CARDS_PER_ROW - 1) * CARD_GAP;

    private final DisplayHide displayHide;
    private final Label reserveLabel;
    private Image hitImage;
    private Action0 beforeDisplayAction;
    private Consumer<CardTemplate> scaledDownConsumer = cardTemplate -> {};

    public ReserveSheet() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.RESERVE);
        displayHide.setActorKey(OnScreenActors.ActorKey.RESERVE_CARD);
        setTransform(true);
        reserveLabel = new Label("", new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_CINZEL, 30), new Color(0.91f, 0.83f, 0.62f, 1f)));
        reserveLabel.setAlignment(Align.center);
        reserveLabel.setWrap(true);
        reserveLabel.setTouchable(Touchable.disabled);
    }

    public void init(List<AssetInfo> assets, Action0 onClickAction) {
        clear();
        align(Align.top);
        getColor().a = 1f;
        TextureRegionDrawable background = new TextureRegionDrawable(
                CustomAssetManager.getTextureRegion(CustomAssetManager.RESERVE_BACKGROUND));
        background.setMinWidth(0);
        background.setMinHeight(0);
        setBackground(background);
        pad(16, PANEL_PADDING, 20, PANEL_PADDING);

        List<CardTemplate> cardTemplates = toCardTemplates(assets);
        createListeners(cardTemplates);
        addHitImage(onClickAction);

        // Refresh on each opening so a language change is reflected immediately.
        reserveLabel.setText(get("reserve.label"));
        Table heading = new Table();
        heading.add(createHeaderRule()).growX().height(1).padRight(16);
        heading.add(reserveLabel).width(CONTENT_WIDTH * 0.56f);
        heading.add(createHeaderRule()).growX().height(1).padLeft(16);
        add(heading).width(CONTENT_WIDTH).padBottom(14).row();

        Table cards = new Table();
        cards.top().left();
        for (int i = 0; i < cardTemplates.size(); i++) {
            CardTemplate cardTemplate = cardTemplates.get(i);
            cardTemplate.setScale(CARD_SCALE);
            cards.add(cardTemplate).top().left()
                    .width(CardTemplate.CARD_WIDTH * CARD_SCALE)
                    .height(CardTemplate.CARD_HEIGHT * CARD_SCALE)
                    .padRight((i + 1) % CARDS_PER_ROW == 0 ? 0 : CARD_GAP)
                    .padBottom(i / CARDS_PER_ROW < (cardTemplates.size() - 1) / CARDS_PER_ROW ? CARD_GAP : 0);
            if ((i + 1) % CARDS_PER_ROW == 0) {
                cards.row();
            }
        }
        // Keep the tray's footprint when the reserve is temporarily empty during refill.
        add(cards).width(CONTENT_WIDTH).minHeight(CardTemplate.CARD_HEIGHT * CARD_SCALE);
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

    private List<CardTemplate> toCardTemplates(List<AssetInfo> assets) {
        List<CardTemplate> cardTemplates = new LinkedList<>();

        for (AssetInfo asset : assets) {
            cardTemplates.add(CardTemplate.buildCard(asset));
        }
        return cardTemplates;
    }

    private List<CardClickListener> createListeners(List<CardTemplate> cardTemplates) {
        List<CardClickListener> listeners = new LinkedList<>();
        for (CardTemplate currentTemplate : cardTemplates) {
            CardClickListener listener = new CardClickListener() {
                @Override
                protected void afterScaledDown(CardTemplate cardTemplate) {
                    scaledDownConsumer.accept(cardTemplate);
                }
            };
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
            if (beforeDisplayAction != null) {
                beforeDisplayAction.call();
            }
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

    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    public void setScaledDownConsumer(Consumer<CardTemplate> scaledDownConsumer) {
        this.scaledDownConsumer = scaledDownConsumer;
    }
}
