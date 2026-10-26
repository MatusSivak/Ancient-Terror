package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.view.components.VitalityLossEffect;
import sk.sivak.eldritchhorror.core.view.components.combat.CombatSounds;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        clearChildren();
        lostActors.clear();
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
        return Completable.defer(() -> {
            List<Completable> losses = new LinkedList<>();
            int count = Math.min(Math.max(0, amount), currentHealth);
            float spacing = Math.min(110f, 650f / Math.max(1, count - 1));
            for (int i = 0; i < count; i++) {
                Actor token = getChildren().get(currentHealth - 1 - i);
                losses.add(loseToughnessNew(token, (i - (count - 1) / 2f) * spacing));
            }
            return Completable.merge(losses);
        });
    }

    private final Set<Actor> lostActors = new HashSet<>();
    public Completable loseToughnessNew(Actor healthToken, float offsetX) {
        return Completable.defer(() -> {
            if (getStage() == null || currentHealth <= 0 || !lostActors.add(healthToken)) {
                return Completable.complete();
            }
            Vector2 position = healthToken.localToStageCoordinates(new Vector2());
            return VitalityLossEffect.play(getStage(), position, healthToken.getWidth(), healthToken.getHeight(),
                    offsetX, VitalityLossEffect.Kind.MONSTER, () -> {
                        currentHealth--;
                        ((Container<ToughnessToken>) healthToken).getActor().deplete();
                        CombatSounds.playMonsterHealthLeave();
                    }, CombatSounds::playMonsterHealthTear);
        });
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
                                Actor empty = getChildren().get(currentHealth + amount);
                                lostActors.remove(empty);
                                empty.remove();
                            }
                            currentHealth += amount;
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
