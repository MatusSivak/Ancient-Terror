package sk.sivak.eldritchhorror.core.view.components.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import java8.features.function.Function;
import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.CardClickListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class CardsCollectionTable<E extends Enum<?>, C extends CardInfo, O> extends VisTable {

    private List<E> unlocked = new LinkedList<>();
    private List<E> discovered = new LinkedList<>();
    private Supplier<E[]> enumValuesSupplier;
    private Function<E, Boolean> filterOutFunction;
    private Function<E, C> enumToCardInfoFunction;
    private Comparator<? super C> cardsComparator;
    private O previousInitValue;
    private Function<C, O> valueProvider;
    private Function<O, String> labelNameFunction;
    private Function<C, E> cardInfoToIdFunction;

    public void setUnlocked(List<E> unlocked) {
        this.unlocked = unlocked;
    }

    public void setDiscovered(List<E> discovered) {
        this.discovered = discovered;
    }

    public void setEnumValuesSupplier(Supplier<E[]> enumValuesSupplier) {
        this.enumValuesSupplier = enumValuesSupplier;
    }

    public void setFilterOutFunction(Function<E, Boolean> filterOutFunction) {
        this.filterOutFunction = filterOutFunction;
    }

    public void setEnumToCardInfoFunction(Function<E, C> enumToCardInfoFunction) {
        this.enumToCardInfoFunction = enumToCardInfoFunction;
    }

    public void setCardsComparator(Comparator<? super C> cardsComparator) {
        this.cardsComparator = cardsComparator;
    }

    public void setPreviousInitValue(O previousInitValue) {
        this.previousInitValue = previousInitValue;
    }

    public void setValueProvider(Function<C, O> valueProvider) {
        this.valueProvider = valueProvider;
    }

    public void setLabelNameFunction(Function<O, String> labelNameFunction) {
        this.labelNameFunction = labelNameFunction;
    }

    public void setCardInfoToIdFunction(Function<C, E> cardInfoToIdFunction) {
        this.cardInfoToIdFunction = cardInfoToIdFunction;
    }

    public List<E> getUnlocked() {
        return unlocked;
    }

    public List<E> getDiscovered() {
        return discovered;
    }

    public void init() {
        List<C> cards = new LinkedList<>();
        for (E enumValue : enumValuesSupplier.get()) {
            if (filterOutFunction.apply(enumValue)) {
                continue;
            }
            cards.add(enumToCardInfoFunction.apply(enumValue));
        }

        Collections.sort(cards, cardsComparator);

        float scale = 0.145f;
//        float scale = 0.4f;
        int cardsInRow = 0;
        List<CardTemplate> allCardTemplates = new LinkedList<>();
        for (int i = 0; i < cards.size(); i++) {
            if (previousInitValue == null || !previousInitValue.equals(valueProvider.apply(cards.get(i)))) {
                previousInitValue = valueProvider.apply(cards.get(i));
                row();
                if (cardsInRow != 0) {
                    addSeparator().colspan(7).pad(0);
                }
                Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.LIGHT_GRAY);
                Label label = new Label(labelNameFunction.apply(previousInitValue), labelStyle) {
                    @Override
                    public void draw(Batch batch, float parentAlpha) {
                        batch.setColor(Color.DARK_GRAY);
                        batch.draw(CustomAssetManager.getTexture(PURE_WHITE_BACKGROUND), getX(), getY(), getWidth(), getHeight());
                        super.draw(batch, parentAlpha);
                    }
                };
                label.setFontScale(0.5f);
                label.setAlignment(Align.center);
                add(label).pad(5).grow().colspan(7).row();
                addSeparator().colspan(7).pad(0);
                cardsInRow = 0;
            }
            cardsInRow++;

            E cardId = cardInfoToIdFunction.apply(cards.get(i));
            if (unlocked.contains(cardId) && discovered.contains(cardId)) {
                CardTemplate card = CardTemplate.buildCard(cards.get(i));
                allCardTemplates.add(card);
                card.setScale(scale);
                add(card).width(1191 * scale).height(1254 * scale).pad(5);
            } else if (unlocked.contains(cardId) && !discovered.contains(cardId)) {
                Image hiddenCard = new Image();
                add(hiddenCard).width(1191 * scale).height(1254 * scale).pad(5);
                CustomAssetManager.getTextureAsync("card/card_template_hidden.png").subscribe(texture -> {
                    hiddenCard.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
                });
            } else {
                Image lockedCard = new Image();
                add(lockedCard).width(1191 * scale).height(1254 * scale).pad(5);
                CustomAssetManager.getTextureAsync("card/card_template_locked.png").subscribe(texture -> {
                    lockedCard.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
                });

            }

            if (i == cards.size()-1) {
                break;
            }
            if (cardsInRow == 4) {
                row();
                addSeparator().colspan(7).pad(0);
                cardsInRow = 0;
            } else {
                addSeparator(true).pad(0);
            }
        }
        pack();
        setBackground(CustomAssetManager.getTextureRegionDrawable(WHITE_BACKGROUND));

        createListeners(allCardTemplates);
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

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        Color colorBefore = new Color(getColor());
        setColor(new Color(0f, 0f, 0f, colorBefore.a));
        super.drawBackground(batch, parentAlpha * 0.8f, x, y);
        batch.draw(CustomAssetManager.getTexture(PURE_WHITE_BACKGROUND), x - 50, y - 5, 50, getHeight() + 10);
        setColor(colorBefore);
    }
}
