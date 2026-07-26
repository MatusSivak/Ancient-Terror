package sk.sivak.eldritchhorror.core.view.components.sheet.discard;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
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

public class DiscardSheet extends VisTable {

    private static final float SCALE = 0.52f;
    private static final float BORDER = 120f;

    private final DisplayHide displayHide;
    private final Image discardLabel;
    private List<CardInfo> discardedCards;
    private Image hitImage;

    public DiscardSheet() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.DISCARD);
        displayHide.setActorKey(OnScreenActors.ActorKey.DISCARD_CARD);
        setTransform(true);
        discardLabel = new Image(CustomAssetManager.getTexture(CustomAssetManager.DISCARD_LABEL));
        discardLabel.setScaling(Scaling.fit);
    }

    public void init(List<CardInfo> discardedCards, Action0 onClickAction) {
        clear();
        align(Align.topLeft);
        setBackground((Drawable) null);
        getColor().a = 1f;
        this.discardedCards = discardedCards;

        List<CardTemplate> cardTemplates = toCardTemplates(discardedCards);

        createListeners(cardTemplates);

        float cardScale = (1627 * SCALE - BORDER) / (4 * CardTemplate.CARD_WIDTH);

        addHitImage(onClickAction);

        discardLabel.setTouchable(Touchable.disabled);
        add(discardLabel).height(100).align(Align.top).padTop(40).padBottom(40).padLeft(53).row();

        Table innerTable = new Table();
        ScrollPane innerTableScrollPane = new ScrollPane(innerTable);
        float width = Math.min(CardTemplate.CARD_WIDTH * cardScale * 3.75f,
                CardTemplate.CARD_WIDTH * cardTemplates.size() * cardScale);
        add(innerTableScrollPane).width(width).padLeft(18);
        for (CardTemplate cardTemplate : cardTemplates) {
            cardTemplate.setScale(cardScale);
            innerTable.add(cardTemplate)
                    .align(Align.topLeft)
                    .width(CardTemplate.CARD_WIDTH * cardScale)
                    .height(CardTemplate.CARD_HEIGHT * cardScale);
        }

        pack();
        TextureRegionDrawable background = CustomAssetManager.getTextureRegionDrawable("wooden_background.png");
        setBackground(background);
        setHeight(1083 * SCALE);
        setWidth(1627 * SCALE);
    }


    private void addHitImage(Action0 onClickAction) {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.getColor().a = 0.0f;
        if (onClickAction != null) {
            addClickListener(hitImage, onClickAction::call);
        }
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
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
