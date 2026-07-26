package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static java8.features.stream.Stream.*;
import static java8.features.util.IterableUtils.forEach;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.components.card.CardDuplicator.duplicate;
import static sk.sivak.eldritchhorror.core.view.components.card.CardTemplate.CARD_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.components.card.CardTemplate.CARD_WIDTH;

/**
 * @author msivak
 */
public class CardClickListener extends ClickListener {

    public static final float SCALE_AMOUNT = 0.53f;
    public static final int CARD_ORIGIN_X = CARD_WIDTH / 2;
    public static final int CARD_ORIGIN_Y = CARD_HEIGHT / 2;
    private static final float DURATION = FADING_EFFECT_DURATION / 1.5f;
    private boolean zoomed = false;
    private float defaultScaleX;
    private float defaultScaleY;

    private ScaledCardClickListener scaledCardClickListener;

    private List<CardTemplate> allTemplates = new LinkedList<>();
    private List<CardClickListener> otherListeners = new LinkedList<>();
    private CardTemplate duplicate;
    private Map<CardTemplate, Color> cardColorMap = new HashMap<>();

    public CardClickListener() {
        scaledCardClickListener = new ScaledCardClickListener();
    }

    public void setAllTemplates(List<CardTemplate> allTemplates) {
        this.allTemplates = allTemplates;
    }

    public void setOtherListeners(List<CardClickListener> otherListeners) {
        this.otherListeners = Stream.collectToList(otherListeners, otherListener -> otherListener != this);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        scaleUp(event);
    }

    private void scaleUp(InputEvent event) {
        if (isZoomed()) {
            return;
        }
        if (!(event.getTarget().getParent() instanceof CardTemplate)) {
            return;
        }
        boolean otherCardIsZoomed = anyMatch(otherListeners, CardClickListener::isZoomed);
        if (otherCardIsZoomed) {
            this.cardColorMap = findFirstOrException(otherListeners, CardClickListener::isZoomed).getCardColorMap();
        } else {
            darkenOtherCards();
        }
        forEach(allTemplates, it -> it.setTouchable(Touchable.disabled));
        forEach(collectToList(otherListeners, CardClickListener::isZoomed), CardClickListener::scaleDown);

        CardTemplate cardTemplate = ((CardTemplate) event.getTarget().getParent());
        if (!otherCardIsZoomed) {
            cardColorMap.put(cardTemplate, new Color(cardTemplate.getForeground().getColor()));
        }
        defaultScaleX = cardTemplate.getScaleX();
        defaultScaleY = cardTemplate.getScaleY();
        cardTemplate.setTouchable(Touchable.disabled);
        cardTemplate.getColor().a = 0f;
        duplicate = duplicate(cardTemplate);
        cardTemplate.getStage().addActor(duplicate);
        Vector2 position = cardTemplate.localToStageCoordinates(new Vector2(0, 0));
        duplicate.setOrigin(CARD_ORIGIN_X, CARD_ORIGIN_Y);
        duplicate.setPosition(position.x + cardTemplate.getPrefWidth() / 2 - CARD_ORIGIN_X,
                position.y + cardTemplate.getPrefHeight() / 2 - CARD_ORIGIN_Y);

        ScaleToAction scaleToAction = Actions.scaleTo(SCALE_AMOUNT, SCALE_AMOUNT, DURATION);
        MoveToAction moveToAction = Actions.moveTo(
                VIEWPORT_WIDTH / 2 - CARD_ORIGIN_X,
                VIEWPORT_HEIGHT / 2 - CARD_ORIGIN_Y,
                DURATION);
        duplicate.addAction(sequence(parallel(scaleToAction, moveToAction), Actions.run(() -> {
            forEach(allTemplates, it -> it.setTouchable(Touchable.enabled));
            zoomed = true;
        })));

        scaledCardClickListener.setDuplicate(duplicate);
        scaledCardClickListener.setOriginal(cardTemplate);
        duplicate.addListener(scaledCardClickListener);
    }

    private void scaleDown() {
        scaledCardClickListener.scaleDown();
    }

    public void hideDuplicate() {
        for (CardTemplate otherTemplate : allTemplates) {
            otherTemplate.setTouchable(Touchable.disabled);
        }
        if (duplicate != null) {
            InfoStage.hideActor(duplicate);
        }
    }

    public boolean isZoomed() {
        return zoomed;
    }

    private void darkenOtherCards() {
        cardColorMap.clear();
        forEach(collectToList(allTemplates, it -> !it.getForeground().getColor().equals(new Color(0, 0, 0, 0.66f))),
                it -> {
                    cardColorMap.put(it, new Color(it.getForeground().getColor()));
                    it.setForegroundColor(new Color(0f, 0f, 0f, 0f));
                    it.getForeground().addAction(Actions.color(new Color(0, 0, 0, 0.66f), DURATION));
                });
    }

    protected void afterScaledDown(CardTemplate cardTemplate) {

    }

    public Map<CardTemplate, Color> getCardColorMap() {
        return cardColorMap;
    }

    private class ScaledCardClickListener extends ClickListener {

        private CardTemplate duplicate;
        private CardTemplate original;

        public void setDuplicate(CardTemplate duplicate) {
            this.duplicate = duplicate;
        }

        public void setOriginal(CardTemplate original) {
            this.original = original;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            forEach(allTemplates, it -> it.setTouchable(Touchable.disabled));
            forEach(allTemplates, otherTemplate -> {
                Color color = cardColorMap.get(otherTemplate);
                if (color != null) {
                    otherTemplate.getForeground().addAction(Actions.color(color, DURATION));
                }
            });
            scaleDown();
            if (cardColorMap.get(original) != null) {
                original.getForeground().setColor(cardColorMap.get(original));
            }
            forEach(allTemplates, it -> it.setTouchable(Touchable.enabled));
        }

        void scaleDown() {
            ScaleToAction scaleToAction = Actions.scaleTo(defaultScaleX, defaultScaleY, DURATION);
            Vector2 position = original.localToStageCoordinates(new Vector2(0, 0));
            MoveToAction moveToAction = Actions.moveTo(
                    position.x + original.getPrefWidth() / 2 - CARD_ORIGIN_X,
                    position.y + original.getPrefHeight() / 2 - CARD_ORIGIN_Y,
                    DURATION);
            duplicate.addAction(sequence(parallel(scaleToAction, moveToAction), run(() -> {
                duplicate.remove();
                original.getColor().a = 1f;
                original.setTouchable(Touchable.enabled);
                afterScaledDown(original);
                zoomed = false;
            })));
        }
    }
}
