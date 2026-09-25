package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sk.sivak.eldritchhorror.core.view.components.combat.ActorCenterPosition;

import static java8.features.util.IterableUtils.forEach;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage.ACTUAL_DICE_SIZE;
import static sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage.DICE_SIZE;

public class DiceRoller {
    private List<DiceImage> dices = new LinkedList<>();
    private DiceAreaCalculator diceAreaCalculator;
    private int dicesFinished;
    private Group diceLayer;
    private int dicesRolled;
    private CompletableSubscriber onSub;
    private int minSuccess;
    private SingleSubscriber<? super Integer> onSub2;
    private List<Vector2> dicePositionsBeforeHide = new LinkedList<>();

    public void init(List<DiceRoll> diceRolls) {
//        diceLayer.clearChildren();
        dices.clear();
        Texture texture = CustomAssetManager.getTexture("dice_sheet.png");
        DiceRollAudio audio = new DiceRollAudio(CustomAssetManager::getSound);
        Texture shadow = CustomAssetManager.getTexture("dice_shadow.png");
        diceAreaCalculator = new DiceAreaCalculator();
        diceAreaCalculator.prepareRollArea(diceRolls.size());
        diceAreaCalculator.prepareReturnArea(diceRolls.size());
        for (int i = 0; i < diceRolls.size(); i++) {
            DiceImage diceImage = new DiceImage(texture);
            diceImage.setRollAudio(audio);
            diceImage.setShadowTexture(shadow);
            diceImage.setArea(diceAreaCalculator.getRollArea(i));
            diceImage.setOnRollEndAction(this::onRollEnd);
            diceImage.setOnAddOneEndAction(this::onAddOneEnd);
            diceImage.setDiceValue(diceRolls.get(i).getDiceValue());
            diceImage.setDiceNumber(diceRolls.get(i).getDiceNr());
            diceImage.setScore(diceRolls.get(i).getScore());
            diceLayer.addActor(diceImage);
            dices.add(diceImage);
        }
    }

    public Completable rollDice(int... diceNumbers) {
        return Completable.create(onSub -> {
            this.onSub = onSub;
            dicesFinished = 0;
            dicesRolled = 0;
            int audibleCount = 0;
            for (DiceImage dice : dices) {
                if (diceNumbers == null || diceNumbers.length == 0) {
                    audibleCount++;
                } else {
                    for (int number : diceNumbers) {
                        if (dice.getDiceNumber() == number) {
                            audibleCount++;
                            break;
                        }
                    }
                }
            }
            for (DiceImage dice : dices) dice.setAudibleDiceCount(audibleCount);
            // Count all scheduled dice before any can finish (including fast-forward).
            dicesRolled = audibleCount;
            if (diceNumbers == null || diceNumbers.length == 0) {
                onRoll();
            } else {
                onReroll(diceNumbers);
            }
        });
    }

    public Single<Integer> addAddOneClickListeners(int minSuccess) {
        return Single.create(onSub -> {
            this.onSub2 = onSub;
            this.minSuccess = minSuccess;
            int i=0;
            for (DiceImage dice : dices) {
                if (dice.getDiceValue() == 6) {
                    continue;
                }
                dice.addAction(Actions.sequence(
                        Actions.delay(0.1f * i),
                        Actions.forever(Actions.sequence(
                                Actions.scaleTo(0.85f,0.85f,0.5f, Interpolation.sine),
                                Actions.scaleTo(1.0f,1.0f,0.5f, Interpolation.sine)
                        ))
                ));
                ButtonUtils.addClickListener(dice, () -> addOneToResult(dice));
                i++;
            }
        });
    }

    private void addOneToResult(DiceImage diceImage) {
        if (diceImage.getDiceValue() == 6) {
            return;
        }
        if (dicesRolled > 0) {
            return;
        }
        for (DiceImage dice : dices) {
            dice.clearActions();
        }
        dicesFinished = 0;
        dicesRolled = 1;
        diceImage.setScore(diceImage.getDiceValue()+1 < minSuccess ? DiceRoll.Score.BAD : DiceRoll.Score.GOOD);
        diceImage.addOneToResult();
    }

    public void setDiceLayer(Group diceLayer) {
        this.diceLayer = diceLayer;
    }

    private void onRollEnd(DiceImage diceImage) {
        dicesFinished++;
        diceImage.highlight();
        if (dicesFinished == dicesRolled) {
            onRollEnd();
        }
    }

    private void onAddOneEnd(DiceImage diceImage) {
        dicesFinished++;
        diceImage.highlight();
        if (dicesFinished == dicesRolled) {
            onAddOneEnd(diceImage.getDiceNumber());
        }
    }

    private void onRollEnd() {
        Collections.sort(dices, (o1, o2) -> {
            if (o1.getDiceValue() == o2.getDiceValue()) {
                return o1.getDiceNumber() - o2.getDiceNumber();
            }
            return o1.getDiceValue() - o2.getDiceValue();
        });
        for (int i = 0; i < dices.size(); i++) {
            Vector2 returnArea = diceAreaCalculator.getReturnArea(i);
            float returnDuration = 0.75f;
            dices.get(i).addAction(new FastForwardAction<>(
                    Actions.sequence(
                            Actions.delay(0.25f),
                            Actions.parallel(
                                    Actions.rotateTo((int) (dices.get(i).getRotation() / 90) * 90, returnDuration, Interpolation.sine),
                                    Actions.moveTo(returnArea.x, returnArea.y, returnDuration, Interpolation.sine)),
                            Actions.run(() -> {
                                diceFinished(() -> {
                                    dicesRolled = 0;
                                    onSub.onCompleted();
                                });
                            })
                    )
            ));
        }
    }

    private void onAddOneEnd(int diceNumber) {
        Collections.sort(dices, (o1, o2) -> {
            if (o1.getDiceValue() == o2.getDiceValue()) {
                return o1.getDiceNumber() - o2.getDiceNumber();
            }
            return o1.getDiceValue() - o2.getDiceValue();
        });


        for (int i = 0; i < dices.size(); i++) {
            Vector2 returnArea = diceAreaCalculator.getReturnArea(i);
            float returnDuration = 0.5f;
            dices.get(i).addAction(new FastForwardAction<>(
                    Actions.sequence(
                            Actions.parallel(
                                    Actions.rotateTo((int) (dices.get(i).getRotation() / 90) * 90, returnDuration, Interpolation.sine),
                                    Actions.moveTo(returnArea.x, returnArea.y, returnDuration, Interpolation.sine),
                                    Actions.scaleTo(1f, 1f, returnDuration, Interpolation.sine)
                            ),
                            Actions.run(() -> {
                                diceFinished(() -> {
                                    dicesRolled = 0;
                                    onSub2.onSuccess(diceNumber);
                                });
                            })
                    )
            ));
        }

        dices.get(0).addAction(new FastForwardAction<>(
                Actions.sequence(
                        Actions.delay(0.5f),
                        Actions.run(() -> {

                        })
                )
        ));
    }

    public void diceFinished(Action0 actionToCall) {
        dicesFinished--;
        if (dicesFinished == 0) {
            actionToCall.call();
        }
    }

    private void onRoll() {
        int index = 0;
        for (DiceImage dice : dices) {
            scheduleRoll(dice, DiceThrowMotion.releaseDelay(index++, dices.size()));
        }
    }

    private void scheduleRoll(DiceImage dice, float delay) {
        dice.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.delay(delay), Actions.run(dice::roll))));
    }

    private void onReroll(int[] diceNumbers) {
        diceAreaCalculator.prepareRollArea(diceNumbers.length);
        int nr = 0;
        for (int i = 0; i < dices.size(); i++) {
            for (int diceNumber : diceNumbers) {
                DiceImage diceImage = dices.get(i);
                if (diceNumber != diceImage.getDiceNumber()) {
                    continue;
                }
                diceImage.setArea(diceAreaCalculator.getRollArea(nr++));
                moveDiceDownAndReroll(diceImage, DiceThrowMotion.releaseDelay(nr - 1, dicesRolled));
                break;
            }
        }
    }

    private void moveDiceDownAndReroll(DiceImage dice, float delay) {
        dice.remove();
        diceLayer.addActor(dice);
        dice.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.moveTo(ViewProperties.VIEWPORT_WIDTH / 2 - dice.getWidth() / 2,
                        -dice.getHeight() * dice.getScaleY(), 0.25f),
                Actions.delay(delay), Actions.run(dice::roll))));

    }

    public Completable moveUp(float bottomY) {
        return Completable.create(onSub -> {
            float sourceX = dices.get(0).getX();
            float targetX = ViewProperties.VIEWPORT_WIDTH/2 - (ACTUAL_DICE_SIZE * dices.size()) / 2f - (DICE_SIZE - ACTUAL_DICE_SIZE)/2f;
            forEach(dices, diceImageOld -> {
                MoveToAction moveToAction = new MoveToAction();
                moveToAction.setActor(diceImageOld);
                moveToAction.setPosition(diceImageOld.getX() + (targetX - sourceX), bottomY - (DICE_SIZE - ACTUAL_DICE_SIZE)/2f);
                moveToAction.setDuration(0.5f);
                moveToAction.setInterpolation(Interpolation.sine);
                diceImageOld.addAction(new FastForwardAction<>(Actions.sequence(moveToAction, Actions.run(onSub::onCompleted))));
            });
        });
    }

    public void updateDiceRoll(DiceRoll diceRoll) {
        for (DiceImage diceImage : dices) {
            if (diceImage.getDiceNumber() != diceRoll.getDiceNr()) {
                continue;
            }
            diceImage.setDiceValue(diceRoll.getDiceValue());
            diceImage.setScore(diceRoll.getScore());
        }
    }

    public void hideDices() {
        for (DiceImage dice : dices) {
            dice.addAction(
                    new FastForwardAction<>(
                            Actions.sequence(
                                    Actions.moveTo(ViewProperties.VIEWPORT_WIDTH / 2 - dice.getWidth() / 2,
                                            -dice.getHeight() * dice.getScaleY(), 0.25f),
                                    Actions.run(dice::remove)
                            )
                    )
            );
        }
    }

    /** Completes once all dice have stopped moving (position, rotation and scale unchanged for a short while). */
    public Completable waitUntilSettled() {
        return Completable.create(onSub -> diceLayer.addAction(new Action() {
            private static final float SETTLE_TIME = 0.1f;
            private static final float EPSILON = 0.01f;
            private final Map<DiceImage, float[]> last = new HashMap<>();
            private float stillTime;

            @Override
            public boolean act(float delta) {
                boolean moved = false;
                for (DiceImage dice : dices) {
                    float[] now = {dice.getX(), dice.getY(), dice.getRotation(), dice.getScaleX(), dice.getScaleY()};
                    float[] before = last.put(dice, now);
                    if (before == null) {
                        moved = true;
                        continue;
                    }
                    for (int i = 0; i < now.length; i++) {
                        if (Math.abs(now[i] - before[i]) > EPSILON) {
                            moved = true;
                            break;
                        }
                    }
                }
                stillTime = moved ? 0 : stillTime + delta;
                if (stillTime >= SETTLE_TIME || FastForwardAction.isOn()) {
                    onSub.onCompleted();
                    return true;
                }
                return false;
            }
        }));
    }

    /** Live on-screen centres keyed by dice number; they keep following the dice while fireballs are queued. */
    public Map<Integer, Vector2> getLiveDiceCenters() {
        Map<Integer, Vector2> centers = new HashMap<>();
        for (DiceImage dice : dices) {
            centers.put(dice.getDiceNumber(), new ActorCenterPosition(dice));
        }
        return centers;
    }

    public void hideDiceDontRemove() {
        dicePositionsBeforeHide.clear();
        for (DiceImage dice : dices) {
            dicePositionsBeforeHide.add(new Vector2(dice.getX(), dice.getY()));
            dice.addAction(
                    new FastForwardAction<>(
                            Actions.sequence(
                                    Actions.moveTo(ViewProperties.VIEWPORT_WIDTH / 2 - dice.getWidth() / 2, -dice.getHeight() * dice.getScaleY(), 0.25f)
                            )
                    )
            );
        }
    }

    public Completable showHiddenDice() {
        return Completable.create(onSub -> {
            for (int i = 0; i < dices.size(); i++) {
                dices.get(i).addAction(
                        new FastForwardAction<>(
                                Actions.sequence(
                                        Actions.moveTo(dicePositionsBeforeHide.get(i).x, dicePositionsBeforeHide.get(i).y, 0.25f, Interpolation.sine),
                                        Actions.delay(0.25f),
                                        Actions.run(onSub::onCompleted)
                                )
                        )
                );
            }
        });
    }
}
