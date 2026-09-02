package sk.sivak.eldritchhorror.core.view.components.sheet.reserve;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import java8.features.function.Consumer;
import java8.features.function.Supplier;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
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

public class ReserveSheet extends VisTable {

    private static final float SCALE = 0.52f;
    private static final float BORDER = 120f;

    private final DisplayHide displayHide;
    private final Image reserveLabel;
    private Image hitImage;
    private Action0 beforeDisplayAction;
    private Consumer<CardTemplate> scaledDownConsumer = cardTemplate -> {};

    public ReserveSheet() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.RESERVE);
        displayHide.setActorKey(OnScreenActors.ActorKey.RESERVE_CARD);
        setTransform(true);
        reserveLabel = new Image(CustomAssetManager.getTexture(CustomAssetManager.RESERVE_LABEL));
        reserveLabel.setScaling(Scaling.fit);
        padRight(67);
    }

    public void init(List<AssetInfo> assets, Action0 onClickAction) {
        clear();
        align(Align.topLeft);
        setBackground((Drawable) null);
        getColor().a = 1f;

        List<CardTemplate> cardTemplates = toCardTemplates(assets);

        createListeners(cardTemplates);

        float cardScale = 0.133f;

        addHitImage(onClickAction);

        reserveLabel.setTouchable(Touchable.disabled);
        add(reserveLabel).height(100).align(Align.top).padTop(20).colspan(4).row();

        boolean first = true;
        for (CardTemplate cardTemplate : cardTemplates) {
            cardTemplate.setScale(cardScale);
            Cell<CardTemplate> cell = add(cardTemplate)
                    .align(Align.topLeft)
                    .width(CardTemplate.CARD_WIDTH * cardScale)
                    .height(CardTemplate.CARD_HEIGHT * cardScale);
        }
        padLeft(50);

        pack();
        setHeight(600 * SCALE);
        setWidth(1450 * SCALE);
        CustomAssetManager.getTextureAsync("wooden_background.png").subscribe(texture -> {
            TextureRegionDrawable background = CustomAssetManager.getTextureRegionDrawable("wooden_background.png");
            setBackground(background);
        });
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
