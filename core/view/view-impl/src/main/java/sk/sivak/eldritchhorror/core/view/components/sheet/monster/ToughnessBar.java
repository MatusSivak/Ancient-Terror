package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.TokenActor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class ToughnessBar extends HorizontalGroup {

    private static final int TOKEN_WIDTH = 282;
    private static final int TOKEN_HEIGHT = 354;
    private static final float SCALE = 0.15f;
    private int toughness;
    private int currentHealth;
    private float actualScale;

    public void init(int toughness, int currentHealth) {
        init(toughness, currentHealth, 1);
    }

    public void init(int toughness, int currentHealth, float scale) {
        this.actualScale = scale;
        this.toughness = toughness;
        this.currentHealth = Math.min(currentHealth, this.toughness); // because in PoisonMistSpellBackListener4 on2() I am reducing toughness

        for (int i = 0; i < this.currentHealth; i++) {
            addActor(new Container<>(new ToughnessToken(false)).width(TOKEN_WIDTH * SCALE * scale).height(TOKEN_HEIGHT * SCALE * scale));
        }

        for (int i = 0; i < toughness-this.currentHealth; i++) {
            addActor(new Container<>(new ToughnessToken(true)).width(TOKEN_WIDTH * SCALE * scale).height(TOKEN_HEIGHT * SCALE * scale));
        }
    }

    @Override
    public float getPrefHeight() {
        return TOKEN_HEIGHT * SCALE;
    }

    public Completable loseToughness(int amount) {
        List<Completable> completableList = new LinkedList<>();
        float tokenWidth = 168;
        float baseOffset = - (amount - 1) * tokenWidth / 2f;
        for (int i = amount-1; i >= 0; i--) {
            Actor healthToken = getChildren().get(currentHealth - 1 - i);
            TokenActor brokenTokenActor = new TokenActor(getTexture(HEALTH_ICON_LEFT), getTexture(HEALTH_ICON_RIGHT));
            Vector2 position = healthToken.localToStageCoordinates(new Vector2());
            brokenTokenActor.setPosition(position.x, position.y);
            brokenTokenActor.setSize(healthToken.getWidth(), healthToken.getHeight());
            brokenTokenActor.setOrigin(Align.center);
            brokenTokenActor.getColor().a = 0f;
            brokenTokenActor.setHolderLocation(position);
            getStage().addActor(brokenTokenActor);
            completableList.add(
                    brokenTokenActor.discard(((Container<ToughnessToken>) healthToken).getActor()::deplete, baseOffset)
            );
            baseOffset += tokenWidth;
        }
        return Completable.amb(completableList);
    }

    private Set<Actor> lostActors = new HashSet<>();
    public Completable loseToughnessNew(Actor healthToken, float offsetX) {
        if (lostActors.contains(healthToken)) {
            return Completable.complete();
        }
        lostActors.add(healthToken);
        TokenActor brokenTokenActor = new TokenActor(getTexture(HEALTH_ICON_LEFT), getTexture(HEALTH_ICON_RIGHT));
        Vector2 position = healthToken.localToStageCoordinates(new Vector2());
        brokenTokenActor.setPosition(position.x, position.y);
        brokenTokenActor.setSize(healthToken.getWidth(), healthToken.getHeight());
        brokenTokenActor.setOrigin(Align.center);
        brokenTokenActor.getColor().a = 0f;
        brokenTokenActor.setHolderLocation(position);
        getStage().addActor(brokenTokenActor);


        return brokenTokenActor.discard(((Container<ToughnessToken>) healthToken).getActor()::deplete, offsetX);
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for (Actor actor : getChildren()) {
            ((Container<ToughnessToken>) actor).getActor().setColor(color);
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public Completable restoreHealth(int amount) {
        int healthMissing = toughness - currentHealth;
        if (healthMissing == 0) {
            return Completable.complete();
        }
        return Completable.create(onSub -> restoreHealth(Math.min(amount, healthMissing), onSub));
    }

    private void restoreHealth(int amount, CompletableSubscriber completableSubscriber) {
        List<Actor> temporaryActors = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            ToughnessToken toughnessToken = new ToughnessToken(false);
            Container<ToughnessToken> fullActor = new Container<>(toughnessToken)
                    .width(TOKEN_WIDTH * SCALE * actualScale)
                    .height(TOKEN_HEIGHT * SCALE * actualScale);
            fullActor.getColor().a = 0f;
            float duration = 1f;
            temporaryActors.add(fullActor);
            if (i == 0) {
                fullActor.addAction(Actions.sequence(
                        Actions.alpha(1f, duration),
                        Actions.run(() -> {
                            for (Actor temporaryActor : temporaryActors) {
                                temporaryActor.remove();
                            }
                            for (int j = 0; j < amount; j++) {
                                addActorAt(currentHealth, new Container<>(new ToughnessToken(false))
                                        .width(TOKEN_WIDTH * SCALE * actualScale)
                                        .height(TOKEN_HEIGHT * SCALE * actualScale));
                            }
                            for (int j = 0; j < amount; j++) {
                                getChildren().get(currentHealth + amount).remove();
                            }

                            completableSubscriber.onCompleted();
                        })
                ));
            } else {
                fullActor.addAction(Actions.alpha(1f, duration));
            }
            getChildren().get(currentHealth + i).addAction(Actions.alpha(0,duration));

            getStage().addActor(fullActor);
            Vector2 localToStageCoordinates = localToStageCoordinates(new Vector2());
            fullActor.setPosition(
                    TOKEN_WIDTH * SCALE * actualScale * 0.5f + localToStageCoordinates.x + TOKEN_WIDTH * actualScale * SCALE * (i + currentHealth),
                    localToStageCoordinates.y + TOKEN_HEIGHT * SCALE * actualScale * 0.5f);
        }
    }

}
