package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import java8.features.function.Consumer;
import java8.features.function.Function;
import java8.features.stream.Stream;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.combat.CombatOverviewTableData;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.view.TestView;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.combat.CombatOverviewTable;
import sk.sivak.eldritchhorror.core.view.components.combat.MonsterCombatTable;
import sk.sivak.eldritchhorror.core.view.components.diceroller.DiceRoller;
import sk.sivak.eldritchhorror.core.view.components.diceroller.DiceRollerStack;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.DragAndDropBinder;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.SourceTargetGroup;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.addAction;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static java8.features.stream.Stream.collectToList;
import static java8.features.stream.Stream.map;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.components.combat.MonsterCombatTable.X_POSITION;
import static sk.sivak.eldritchhorror.core.view.test.RollResultTable.createNotSuccessfulTable;
import static sk.sivak.eldritchhorror.core.view.test.RollResultTable.createSuccessfulTable;
import static sk.sivak.eldritchhorror.core.view.test.TestResultTable.*;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder.buildButton;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class TestViewImpl implements TestView {

    private TestController controller;
    private TestInfoTable testInfoTable;
    private Actor selectAssetsToUseActor;
    private TextButton testButton;
    private TestTableStack testTableStack = new TestTableStack();
    private LabelTable labelTable;
    private Button[] buttons;
    private DiceRollerStack diceRollerStack = new DiceRollerStack();
    private Stack<MonsterCombatTable> monsterCombatTableStack = new Stack<>();
    private List<Subscription> monsterCombatTableSubscriptions = new LinkedList<>();
    private boolean hideDicesAfterConfirm;

    public void setController(TestController controller) {
        this.controller = controller;
    }

    @Override
    public Single<List<UsableAsset>> confirmTest(Stat stat, int modifier, int baseStatValue, int bonusStatValue,
                                                 List<UsableAsset> usableAssets, int additionalDicesCount, boolean isCombat) {

        return Single.create(onSub -> {
            MapStage.darkenWorld();
            if (isCombat) {
                hideDicesAfterConfirm = false;
            } else {
                hideDicesAfterConfirm = true;
                InfoStage.displayText(get("test.testPrefix", stat.prettyString()));
                if (!monsterCombatTableStack.isEmpty()) {
                    monsterCombatTableStack.peek().moveRight().subscribe();
                    monsterCombatTableStack.peek().setLocked(true);
                }
            }
            if (modifier == 0 && usableAssets.isEmpty() && additionalDicesCount == 0) {
                onSub.onSuccess(null);
                return;
            }
            testInfoTable = new TestInfoTable(stat, modifier, baseStatValue, bonusStatValue, usableAssets, additionalDicesCount);

            String buttonTitle = get("test.button");

            testButton = buildButton(buttonTitle);
            testButton.addListener(new ConfirmTestListener(onSub));
            InfoStage.showButton(testButton);
            testInfoTable.setX(VIEWPORT_WIDTH / 2 - testInfoTable.getWidth() / 2);
            testInfoTable.setY(InfoStage.getInvestigatorHud().getPrefHeight() + VIEWPORT_HEIGHT * 0.01f);
            InfoStage.addSmallActorToInfoStage(testInfoTable);

            createDragAndDrop(usableAssets, testInfoTable);
        });
    }

    private void createDragAndDrop(List<UsableAsset> usableAssets, TestInfoTable testInfoTable) {

        CardTemplate[] cardTemplates = collectToList(map(usableAssets, it -> {
            CardTemplate cardTemplate = CardTemplate.buildCard(it.getCardInfo());
            cardTemplate.setScale(0.16f);
            return cardTemplate;
        })).toArray(new CardTemplate[usableAssets.size()]);
        DragAndDropBinder dragAndDropBinder = new DragAndDropBinder(InfoStage.getStageSafe(), testInfoTable);
        dragAndDropBinder.init(cardTemplates);
        SourceTargetGroup sourceTargetGroup = dragAndDropBinder.getSourceTargetGroup();
        ScrollPane scrollPane = new ScrollPane(sourceTargetGroup) {
            @Override
            public void act(float delta) {
                super.act(delta);
            }
        };
        Container<ScrollPane> scrollPaneContainer = new Container<>(scrollPane);
        float maxWidth = 320;
        if (sourceTargetGroup.getWidth() < maxWidth) {
            scrollPaneContainer.setPosition(460 - sourceTargetGroup.getWidth(), 260);
            scrollPane.setScrollingDisabled(true, true);
            scrollPaneContainer
                    .width(sourceTargetGroup.getWidth())
                    .height(sourceTargetGroup.getHeight())
                    .maxWidth(maxWidth);
        } else {
            scrollPaneContainer.setPosition(80 + maxWidth/2f,260);
            scrollPane.setScrollingDisabled(false, true);
            scrollPaneContainer
                    .width(sourceTargetGroup.getWidth())
                    .height(sourceTargetGroup.getHeight())
                    .maxWidth(maxWidth);
            scrollPane.layout();
            scrollPane.setScrollPercentX(100);
        }


        InfoStage.showActor(scrollPaneContainer);
        selectAssetsToUseActor = scrollPaneContainer;
    }

    @Override
    public Completable showRolledTestDices(List<DiceRoll> diceRolls) {
        DiceRoller diceRoller = diceRollerStack.push();
        diceRoller.setDiceLayer(InfoStage.getDiceLayer());
        diceRoller.init(diceRolls);
        InfoStage.setBottomHeight(InfoStage.getInvestigatorHud().getPrefHeight() + 5);
        return diceRoller.rollDice();
    }

    @Override
    public Completable showRolledDices(List<DiceRoll> diceRolls) {
        MapStage.darkenWorld();
        return showRolledTestDices(diceRolls);
    }

    @Override
    public Completable confirmTestResult(boolean scoreImportant, boolean successful, int score) {
        return Completable.create(onSub -> {
            if (!monsterCombatTableStack.isEmpty() && monsterCombatTableStack.peek().isLocked()) {
                monsterCombatTableStack.peek().setLocked(false);
            }
            diceRollerStack.peek().moveUp().subscribe();

            if (scoreImportant) {
                confirmScoreTestResult(score, onSub);
            } else {
                confirmBinaryTestResult(successful, onSub);
            }
        });
    }

    @Override
    public Completable confirmRollResult(int score) {
        return Completable.create(onSub -> {
            TextButton button = buildButton(get("dialog.ok"));
            hideDicesAfterConfirm = true;
            showButton(onSub, button);

            if (score == 0) {
                testTableStack.push(createFailedTable());
            } else if (score == 1) {
                testTableStack.push(createNotSuccessfulTable());
            } else {
                testTableStack.push(createSuccessfulTable());
            }

            testTableStack.peek().setPosition(VIEWPORT_WIDTH / 2 - testTableStack.peek().getWidth() / 2,
                    InfoStage.getInvestigatorHud().getPrefHeight() + VIEWPORT_HEIGHT * 0.01f);
            InfoStage.showActor(testTableStack.peek());
        });
    }

    @Override
    public Completable rerollDice(List<DiceRoll> diceRollList) {
        int[] diceNumbers = new int[diceRollList.size()];
        for (int i = 0; i < diceRollList.size(); i++) {
            diceRollerStack.peek().updateDiceRoll(diceRollList.get(i));
            diceNumbers[i] = diceRollList.get(i).getDiceNr();
        }
        return diceRollerStack.peek().rollDice(diceNumbers);
    }

    private void confirmBinaryTestResult(boolean successful, CompletableSubscriber onSub) {
        TextButton button = buildButton(get("dialog.ok"));
        showButton(onSub, button);

        if (successful) {
            testTableStack.push(createPassedTable());
        } else {
            testTableStack.push(createFailedTable());
        }

        testTableStack.peek().setPosition(VIEWPORT_WIDTH / 2 - testTableStack.peek().getWidth() / 2,
                InfoStage.getInvestigatorHud().getPrefHeight() + VIEWPORT_HEIGHT * 0.01f);
        InfoStage.showActor(testTableStack.peek());
    }

    private void confirmScoreTestResult(int score, CompletableSubscriber onSub) {
        TextButton button = buildButton(get("dialog.ok"));
        showButton(onSub, button);

        testTableStack.push(createScoreTable(score));
        testTableStack.peek().setPosition(VIEWPORT_WIDTH / 2 - testTableStack.peek().getWidth() / 2,
                InfoStage.getInvestigatorHud().getPrefHeight() + VIEWPORT_HEIGHT * 0.01f);
        InfoStage.showActor(testTableStack.peek());

    }

    private void showButton(CompletableSubscriber onSub, TextButton button) {
        addClickListener(button, () -> {
            if (hideDicesAfterConfirm) {
                diceRollerStack.pop().hideDices();
                InfoStage.hideActor(testTableStack.pop());
                InfoStage.setBottomHeight(5);
                MapStage.brightenWorld();
            }
            InfoStage.setBottomHeight(5);
            onSub.onCompleted();
        });
        InfoStage.showButton(button);
    }

    @Override
    public Single<Boolean> askRerollUsingFocus(Question question) {
        return Single.create(onSub -> {
            InfoStage.getInvestigatorHud().getFocusBar().highlightFullContainer();
            float originalBottomHeight = InfoStage.getBottomHeight();
            Runnable noAction = () -> {
                InfoStage.getInvestigatorHud().getFocusBar().stopHighlight();
                rerollButtonAction(onSub, false, originalBottomHeight);
            };
            Runnable yesAction = () -> {
                InfoStage.getInvestigatorHud().getFocusBar().stopHighlight();
                rerollButtonAction(onSub, true, originalBottomHeight);
            };
            buttons = ButtonUtils.buildNoYesButtons(0, noAction, yesAction);
            if (question == null) {
                labelTable = LabelTable.createAndShowTable(0, get("test.rerollFocus"),
                        CustomAssetManager.getTexture(CustomAssetManager.FOCUS_TOKEN));
            } else {
                List<Texture> textures = new LinkedList<>();
                textures.add(CustomAssetManager.getTexture(CustomAssetManager.FOCUS_TOKEN));
                for (Object textureName : question.getTextureNameList()) {
                    textures.add(CustomAssetManager.getTexture((String)textureName)); // W T F ?!?!?!?!
                }
                if (question.getPortraitTextureName() != null) {
                    labelTable = LabelTable.createAndShowTable(0, question.getTitle(),
                            question.getPortraitTextureName(), textures, 0);
                } else {
                    labelTable = LabelTable.createAndShowTable(0, question.getTitle(),
                            CustomAssetManager.getTexture(CustomAssetManager.FOCUS_TOKEN));
                }

            }
        });
    }

    @Override
    public Single<Boolean> askRerollUsingClue(Question question) {
        return Single.create(onSub -> {
            InfoStage.getInvestigatorHud().getClueBar().highlightFullContainer();
            float originalBottomHeight = InfoStage.getBottomHeight();
            Runnable noAction = () -> {
                InfoStage.getInvestigatorHud().getClueBar().stopHighlight();
                rerollButtonAction(onSub, false, originalBottomHeight);
            };
            Runnable yesAction = () -> {
                InfoStage.getInvestigatorHud().getClueBar().stopHighlight();
                rerollButtonAction(onSub, true, originalBottomHeight);
            };
            buttons = ButtonUtils.buildNoYesButtons(0, noAction, yesAction);
            if (question == null) {
                labelTable = LabelTable.createAndShowTable(0, get("test.rerollClue"),
                        CustomAssetManager.getTexture(CustomAssetManager.CLUE_TOKEN));
            } else {
                List<Texture> textures = new LinkedList<>();
                textures.add(CustomAssetManager.getTexture(CustomAssetManager.CLUE_TOKEN));
                for (Object textureName : question.getTextureNameList()) {
                    textures.add(CustomAssetManager.getTexture((String)textureName));
                }
                if (question.getPortraitTextureName() != null) {
                    labelTable = LabelTable.createAndShowTable(0, question.getTitle(),
                            question.getPortraitTextureName(), textures, 0);
                } else {
                    labelTable = LabelTable.createAndShowTable(0, question.getTitle(),
                            CustomAssetManager.getTexture(CustomAssetManager.CLUE_TOKEN));
                }

            }

        });
    }

    @Override
    public Single<Integer> addOneToDieResult(int minSuccess) {
        LabelTable addOneTable = LabelTable.createAndShowTable(0f, get("test.addOneToDie"));
        return diceRollerStack.peek().addAddOneClickListeners(minSuccess).map(input -> {
            InfoStage.hideActor(addOneTable);
            InfoStage.setBottomHeight(InfoStage.getBottomHeight() - addOneTable.getHeight() - 5);
            return input;
        });
    }

    private void rerollButtonAction(SingleSubscriber<? super Boolean> onSub, boolean value, float originalBottomHeight) {
        InfoStage.hideActor(labelTable);
        for (Button button : buttons) {
            InfoStage.hideActor(button);
        }
        InfoStage.setBottomHeight(originalBottomHeight);
        onSub.onSuccess(value);
    }

    private class ConfirmTestListener extends ClickListener {

        private SingleSubscriber<? super List<UsableAsset>> singleSubscriber;

        public ConfirmTestListener(SingleSubscriber<? super List<UsableAsset>> singleSubscriber) {
            this.singleSubscriber = singleSubscriber;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            InfoStage.hideActor(testButton);
            InfoStage.hideActor(selectAssetsToUseActor);
            InfoStage.hideActor(testInfoTable);
            singleSubscriber.onSuccess(testInfoTable.getSelectedUsableAssets());
        }
    }

    @Override
    public Completable showCombatOverview(CombatOverviewTableData data) {
        return Completable.create(onSub -> {
            MapStage.darkenWorld();
            InfoStage.displayTextDontHide(get("combat.title"));
            String buttonTitle = get("combat.fight");

            CombatOverviewTable combatOverviewTable = new CombatOverviewTable();
            combatOverviewTable.init(data);
            combatOverviewTable.setX(VIEWPORT_WIDTH - combatOverviewTable.getWidth() - 65);
            combatOverviewTable.setY(InfoStage.getInvestigatorHud().getPrefHeight() + VIEWPORT_HEIGHT * 0.01f);
            InfoStage.addSmallActorToInfoStage(combatOverviewTable);

            TextButton combatButton = buildButton(buttonTitle);
            ButtonUtils.addClickListener(combatButton, () -> {
                onSub.onCompleted();
                InfoStage.hideActor(combatButton);
                InfoStage.hideActor(combatOverviewTable);
                InfoStage.hideLabel(get("combat.title"));
            });
            InfoStage.showButton(combatButton);
            combatButton.setPosition(combatOverviewTable.getX() + combatOverviewTable.getWidth()/2 - combatButton.getWidth()/2f, 5);


        });
    }

    @Override
    public Completable showCombatTable(MonsterCombatTableData data) {
        return Completable.create(onSub -> {
            monsterCombatTableStack.push(new MonsterCombatTable());
            Subscription subscription1 = InfoStage.getAddSmallActorSubject()
                    .filter(actor -> actor instanceof CardTemplate)
                    .subscribe(addedActor -> {
                        monsterCombatTableStack.peek().moveRight().subscribe();
                    });
            Subscription subscription2 = InfoStage.getHideActorSubject()
                    .filter(actor -> actor instanceof Button)
                    .filter(actor -> !monsterCombatTableStack.peek().isLocked())
                    .subscribe(removedActor -> {
                        monsterCombatTableStack.peek().moveLeft().subscribe();
                    });
            monsterCombatTableSubscriptions.add(subscription1);
            monsterCombatTableSubscriptions.add(subscription2);
            InfoStage.addSmallActorToInfoStage(monsterCombatTableStack.peek());
            monsterCombatTableStack.peek().init(data);
            monsterCombatTableStack.peek().setPosition(
                    X_POSITION, VIEWPORT_HEIGHT);
            monsterCombatTableStack.peek().addAction(new FastForwardAction<>(Actions.sequence(
                    moveTo(X_POSITION,
                            VIEWPORT_HEIGHT - monsterCombatTableStack.peek().getHeight(), 1f, Interpolation.sine
                    ),
                    Actions.run(() -> {
                        monsterCombatTableStack.peek().setCentered();
                        onSub.onCompleted();
                    })
            )));

        });
    }

    @Override
    public Completable highlightHorrorCombat() {
        return highlightCombat(MonsterCombatTable::highlightHorror);
    }

    @Override
    public Completable highlightDamageCombat() {
        return highlightCombat(MonsterCombatTable::highlightDamageAndToughness);
    }

    private Completable highlightCombat(Consumer<MonsterCombatTable> highlightAction) {
        return Completable.create(onSub -> {
            if (monsterCombatTableStack.isEmpty()) {
                onSub.onCompleted();
                return;
            }
            monsterCombatTableStack.peek().addAction(new Action() {
                @Override
                public boolean act(float v) {
                    if (!monsterCombatTableStack.peek().isCentered()) {
                        return false;
                    }
                    onSub.onCompleted();
                    highlightAction.accept(monsterCombatTableStack.peek());
                    return true;
                }
            });
        });
    }


    @Override
    public Completable destroyHorror(List<DiceRoll> diceRolls) {
        return destroyHorrorOrDamage(diceRolls, dicePositions -> monsterCombatTableStack.peek().destroyHorror(dicePositions), () -> {
            diceRollerStack.pop().hideDices();
            InfoStage.hideActor(testTableStack.pop());
            InfoStage.setBottomHeight(5);
        });
    }

    @Override
    public Completable destroyDamage(List<DiceRoll> diceRolls) {
        return destroyHorrorOrDamage(diceRolls, dicePositions -> monsterCombatTableStack.peek().destroyDamage(dicePositions), () -> {
            if (monsterCombatTableStack.peek().getRemainingDamage() > 0) {
                diceRollerStack.peek().hideDiceDontRemove();
                testTableStack.peek().addAction(new FastForwardAction<>(Actions.alpha(0, 1f)));
            }
        });
    }


    private Completable destroyHorrorOrDamage(List<DiceRoll> diceRolls, Function<List<Vector2>, Completable> destroyFunction, Action0 onEndAction) {
        List<Vector2> dicesCenterPositions = diceRollerStack.peek().getDicesCenterPositions();
        Collections.sort(diceRolls, (o1, o2) -> o1.getDiceNr() - o2.getDiceNr());
        List<Vector2> result = new LinkedList<>();
        for (int i = 0; i < diceRolls.size(); i++) {
            if (diceRolls.get(i).getScore() == DiceRoll.Score.GOOD) {
                result.add(dicesCenterPositions.get(i));
            }
            if (diceRolls.get(i).getScore() == DiceRoll.Score.VERY_GOOD) {
                result.add(dicesCenterPositions.get(i));
                result.add(dicesCenterPositions.get(i));
            }
        }
        if (result.isEmpty()) {
            onEndAction.call();
            return Completable.complete();
        }

        return destroyFunction.apply(result).concatWith(Completable.create(onSub -> {
            onEndAction.call();
            onSub.onCompleted();
        }));
    }

    @Override
    public Completable destroySanity(int sanityLost) {
        return destroySanityOrHealth(sanityLost, InfoStage.getInvestigatorHud().getSanityBar(),
                positions -> monsterCombatTableStack.peek().destroySanity(positions));
    }

    @Override
    public Completable destroyHealth(int healthLost) {
        return destroySanityOrHealth(healthLost, InfoStage.getInvestigatorHud().getHealthBar(),
                positions -> monsterCombatTableStack.peek().destroyHealth(positions));
    }

    private Completable destroySanityOrHealth(int tokensCount, ContainerBar containerBar, Function<List<Vector2>, Completable> destroyFunction) {
        if (tokensCount == 0) {
            return Completable.complete();
        }
        List<Vector2> endPositions = new LinkedList<>();
        for (int i = 0; i < tokensCount; i++) {
            Vector2 position = containerBar.getFullContainerPosition(i);
            endPositions.add(new Vector2(position.x + 11, position.y + 13.5f));
        }
        Collections.reverse(endPositions);
        return destroyFunction.apply(endPositions);
    }

    @Override
    public Completable destroyMonsterHealth(List<DiceRoll> diceRolls) {
        boolean anyDiceWithGoodScore = Stream.anyMatch(diceRolls, diceRoll -> diceRoll.getScore() != DiceRoll.Score.BAD);
        if (!anyDiceWithGoodScore) {
            diceRollerStack.pop().hideDices();
            InfoStage.hideActor(testTableStack.pop());
            InfoStage.setBottomHeight(5);
            return Completable.complete();
        }

        return Completable.create(onSubMain -> {
            monsterCombatTableStack.peek().addAction(new Action() {
                @Override
                public boolean act(float v) {
                    if (!monsterCombatTableStack.peek().isCentered()) {
                        return false;
                    }
                    Completable completable;
                    if (monsterCombatTableStack.peek().getRemainingDamage() > 0) {
                        testTableStack.peek().addAction(new FastForwardAction<>(Actions.alpha(1, 1f)));
                        completable = diceRollerStack.peek().showHiddenDice();
                    } else {
                        completable = Completable.complete();
                    }
                    completable.subscribe(() -> {

                        List<Vector2> dicesCenterPositions = diceRollerStack.peek().getDicesCenterPositions();
                        Collections.sort(diceRolls, (o1, o2) -> o1.getDiceNr() - o2.getDiceNr());
                        List<Vector2> result = new LinkedList<>();
                        for (int i = 0; i < diceRolls.size(); i++) {
                            if (diceRolls.get(i).getScore() == DiceRoll.Score.GOOD) {
                                result.add(dicesCenterPositions.get(i));
                            }
                            if (diceRolls.get(i).getScore() == DiceRoll.Score.VERY_GOOD) {
                                result.add(dicesCenterPositions.get(i));
                                result.add(dicesCenterPositions.get(i));
                            }
                        }

                        monsterCombatTableStack.peek().destroyMonsterHealth(result).concatWith(Completable.create(onSub -> {
                            diceRollerStack.pop().hideDices();
                            InfoStage.hideActor(testTableStack.pop());
                            InfoStage.setBottomHeight(5);
                            onSub.onCompleted();
                        })).subscribe(onSubMain::onCompleted);
                    });
                    return true;
                }
            });
        });
    }

    @Override
    public Completable hideCombatTable() {
        return Completable.create(onSub -> {
            monsterCombatTableStack.peek().addAction(new AfterCenteredAction(() -> {
                MapStage.brightenWorld();
                for (Subscription monsterCombatTableSubscription : monsterCombatTableSubscriptions) {
                    monsterCombatTableSubscription.unsubscribe();
                }
                monsterCombatTableSubscriptions.clear();
                monsterCombatTableStack.peek().addAction(new FastForwardAction<>(
                        Actions.after(Actions.sequence(
                                moveTo(X_POSITION, VIEWPORT_HEIGHT, 1f, Interpolation.sine),
                                Actions.run(() -> {
                                    monsterCombatTableStack.peek().remove();
                                    monsterCombatTableStack.pop();
                                    onSub.onCompleted();
                                })))
                ));
            }));
        });
    }


    @Override
    public Completable updateMonsterHorror(Integer actualHorror) {
        return monsterCombatTableStack.peek().updateHorror(actualHorror);
    }

    @Override
    public Completable updateMonsterDamage(Integer actualDamage) {
        return monsterCombatTableStack.peek().updateDamage(actualDamage);
    }

    @Override
    public Completable waitForCombatTableCentered() {
        return Completable.create(onSub -> {
            monsterCombatTableStack.peek().addAction(new AfterCenteredAction(onSub::onCompleted));
        });
    }


    private class AfterCenteredAction extends Action {

        private Runnable action;

        AfterCenteredAction(Runnable action) {
            this.action = action;
        }

        @Override
        public boolean act(float v) {
            if (!monsterCombatTableStack.peek().isCentered()) {
                return false;
            }
            action.run();
            return true;
        }
    }
}
