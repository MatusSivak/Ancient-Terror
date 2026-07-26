package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.CardClickListener;

import java.util.LinkedList;
import java.util.List;

public class SelectCardComponent extends WidgetGroup implements SelectComponent<CardInfo> {

    public static final Color DARK_COLOR = new Color(0f, 0f, 0f, 0.66f);
    private final static float SCALE_MAXIMIZED = 0.16f;
    private final CardTemplate cardTemplate;
    private final CardInfo cardInfo;

    private boolean selected;
    private CardClickListener cardClickListener;
    private List<SelectComponent<CardInfo>> otherCardComponents;

    public SelectCardComponent(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
        cardTemplate = CardTemplate.buildCard(cardInfo);
        cardTemplate.setScale(SCALE_MAXIMIZED);

        cardTemplate.setForegroundColor(DARK_COLOR);

        cardClickListener = new CardClickListener() {
            @Override
            protected void afterScaledDown(CardTemplate cardTemplate) {
                if (!isSelected()) {
                    cardTemplate.setForegroundColor(DARK_COLOR);
                }
            }
        };
    }

    @Override
    public Actor getActor() {
        return cardTemplate;
    }

    @Override
    public CardInfo getKey() {
        return cardInfo;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void select() {
        cardTemplate.setForegroundColor(new Color(0f, 0f, 0f, 0.00f));
        cardTemplate.addListener(cardClickListener);
        if (selected) {
            showDetail();
        }
        this.selected = true;
    }

    @Override
    public void reselect() {
    }

    private void showDetail() {
    }

    @Override
    public void deselect() {
        if (cardClickListener != null) {
            cardTemplate.removeListener(cardClickListener);
        }
        this.selected = false;
        cardTemplate.setForegroundColor(DARK_COLOR);
    }

    @Override
    public void showTicked() {
    }

    @Override
    public int getInTableWidth(int listSize) {
        return (int) (CardTemplate.CARD_WIDTH * SCALE_MAXIMIZED);
    }

    @Override
    public int getInTableHeight(int listSize) {
        return (int) (CardTemplate.CARD_HEIGHT * SCALE_MAXIMIZED);
    }

    CardTemplate getCardTemplate() {
        return cardTemplate;
    }

    CardClickListener getCardClickListener() {
        return cardClickListener;
    }

    @Override
    public void setOtherSelectComponents(List<SelectComponent<CardInfo>> otherSelectComponents) {
        List<CardTemplate> otherTemplates = new LinkedList<>();
        List<CardClickListener> otherListeners = new LinkedList<>();

        for (SelectComponent<CardInfo> otherSelectComponent : otherSelectComponents) {
            SelectCardComponent otherCardComponent = ((SelectCardComponent) otherSelectComponent);
            otherTemplates.add(otherCardComponent.getCardTemplate());
            otherListeners.add(otherCardComponent.getCardClickListener());
        }
        cardClickListener.setAllTemplates(otherTemplates);
        cardClickListener.setOtherListeners(otherListeners);
    }
}
