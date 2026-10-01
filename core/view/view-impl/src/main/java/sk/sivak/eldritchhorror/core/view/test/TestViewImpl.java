package sk.sivak.eldritchhorror.core.view.test;

import sk.sivak.eldritchhorror.core.view.components.combat.CombatInterruption;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
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
import rx.functions.Func0;
import sk.sivak.eldritchhorror.core.constants.combat.CombatOverviewTableData;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
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
import sk.sivak.eldritchhorror.core.view.action.PrePlayedTokenLoss;
import sk.sivak.eldritchhorror.core.view.action.TokenSounds;
import sk.sivak.eldritchhorror.core.view.action.focus.TokenView;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.DragAndDropBinder;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.SourceTargetGroup;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.addAction;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static java8.features.stream.Stream.collectToList;
import static java8.features.stream.Stream.map;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.test.RollResultTable.createNotSuccessfulTable;
import static sk.sivak.eldritchhorror.core.view.test.RollResultTable.createSuccessfulTable;
import static sk.sivak.eldritchhorror.core.view.test.TestResultTable.*;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder.buildButton;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class TestViewImpl implements TestView {

    private static final float RESULT_DISPLAY_DURATION = 0.0f;
    /** Result table sits in the bottom strip formerly used by the OK button; dice line up right above it. */
    private static final float RESULT_TABLE_Y = 5f;

    /* Test-setup layout keeps clear of the HUD: side menu + clock on the left, menu/fast-forward on the right. */
    static final float SAFE_LEFT = 150f;
    static final float SAFE_RIGHT = MonsterCombatTable.SAFE_RIGHT;
    static final float SAFE_TOP = VIEWPORT_HEIGHT - 8f;
    static final float LAYOUT_GAP = 12f;
    private static final float MAX_CARD_SCALE = 0.155f;
    private static final float MIN_CARD_SCALE = 0.11f;
    private static final float CARD_PANEL_PAD = 6f;

    private TestController controller;
    private TestInfoTable testInfoTable;
    private Actor selectAssetsToUseActor;
    private TextButton testButton;
    private TestTableStack testTableStack = new TestTableStack();
    private LabelTable labelTable;
    private Button[] buttons;
    private DiceRollerStack diceRollerStack = new DiceRollerStack();
    private Stack<MonsterCombatTable> monsterCombatTableStack = new Stack<>();
    /** The fighting investigator, whose stand on the map the monster's fireballs aim at. */
    private InvestigatorId combatInvestigatorId;
    private final TokenView impactTokenView = new TokenView();
    private List<Subscription> monsterCombatTableSubscriptions = new LinkedList<>();
    private boolean hideDicesAfterConfirm;

    public void setController(TestController controller) {
        this.controller = controller;
    }

    @Override
    public Single<List<UsableAsset>> confirmTest(Stat stat, int modifier, int baseStatValue, int bonusStatValue,
                                                 List<UsableAsset> usableAssets, int additionalDicesCount, boolean isCombat) {

        return Single.create(onSub -> {
            if (isCombat) {
                CombatInterruption.resume();
            }
            MapStage.darkenWorld();
            if (isCombat) {
                hideDicesAfterConfirm = false;
            } else {
                hideDicesAfterConfirm = true;
                InfoStage.displayText(get("test.testPrefix", stat.prettyString()));
                if (!monsterCombatTableStack.isEmpty()) {
                    // A test inside the combat (e.g. casting Flesh Ward) - the monster steps away meanwhile.
                    CombatInterruption.suspend();
                    monsterCombatTableStack.peek().moveRight().subscribe();
                    monsterCombatTableStack.peek().setLocked(true);
                }
            }
            // Nothing can change the dice count, so there's nothing to confirm; roll right away as if Test was pressed.
            if (usableAssets == null || usableAssets.isEmpty()) {
                onSub.onSuccess(null);
                return;
            }
            testInfoTable = new TestInfoTable(stat, modifier, baseStatValue, bonusStatValue, usableAssets, additionalDicesCount);

            // Cards and summary sit side by side, centred between the side menu and the right-hand buttons.
            // (In combat the monster roams above everything, so there is no panel to make room for.)
            testButton = buildButton(get("test.button"));
            testButton.addListener(new ConfirmTestListener(onSub));

            float infoColumnWidth = Math.max(testInfoTable.getWidth(), testButton.getWidth());
            float sideColumnLeft = SAFE_RIGHT - infoColumnWidth - LAYOUT_GAP * 2;
            VisTable cardsPanel = createDragAndDrop(usableAssets, testInfoTable, sideColumnLeft - LAYOUT_GAP - SAFE_LEFT);
            float totalWidth = cardsPanel.getWidth() + LAYOUT_GAP * 3 + infoColumnWidth;
            float cardsX = SAFE_LEFT + (SAFE_RIGHT - SAFE_LEFT - totalWidth) / 2f;
            float sideColumnCenter = cardsX + cardsPanel.getWidth() + LAYOUT_GAP * 3 + infoColumnWidth / 2f;
            float zoneBottom = getHudTop();
            cardsPanel.setPosition(cardsX, zoneBottom + Math.max(0, (SAFE_TOP - zoneBottom - cardsPanel.getHeight()) / 2f));

            InfoStage.showButton(testButton, sideColumnCenter - testButton.getWidth() / 2f, 5);
            testInfoTable.setX(sideColumnCenter - testInfoTable.getWidth() / 2);
            testInfoTable.setY(Math.max(testButton.getTop() + LAYOUT_GAP, zoneBottom));
            InfoStage.addSmallActorToInfoStage(testInfoTable);

            InfoStage.showActor(cardsPanel);
            selectAssetsToUseActor = cardsPanel;
        });
    }
    private static float getHudTop() {
        return InfoStage.getInvestigatorHud().getPrefHeight() + 8f;
    }

    private VisTable createDragAndDrop(List<UsableAsset> usableAssets, TestInfoTable testInfoTable, float maxWidth) {

        // Largest card size that fits every card side by side and both rows (use / available) vertically.
        float maxInnerWidth = maxWidth - CARD_PANEL_PAD * 2;
        float hintHeight = 24f;
        float maxGroupHeight = SAFE_TOP - getHudTop() - CARD_PANEL_PAD * 3 - hintHeight;
        float scaleForHeight = (maxGroupHeight - VIEWPORT_HEIGHT * 0.1f) / 2f / CardTemplate.CARD_HEIGHT;
        float scaleForWidth = maxInnerWidth / (usableAssets.size() * (float) CardTemplate.CARD_WIDTH);
        float cardScale = Math.max(MIN_CARD_SCALE, Math.min(MAX_CARD_SCALE, Math.min(scaleForHeight, scaleForWidth)));
        CardTemplate[] cardTemplates = collectToList(map(usableAssets, it -> {
            CardTemplate cardTemplate = CardTemplate.buildCard(it.getCardInfo());
            cardTemplate.setScale(cardScale);
            return cardTemplate;
        })).toArray(new CardTemplate[usableAssets.size()]);
        DragAndDropBinder dragAndDropBinder = new DragAndDropBinder(InfoStage.getStageSafe(), testInfoTable);
        dragAndDropBinder.init(cardTemplates);
        SourceTargetGroup sourceTargetGroup = dragAndDropBinder.getSourceTargetGroup();
        ScrollPane scrollPane = new ScrollPane(sourceTargetGroup);
        scrollPane.setOverscroll(false, false);

        boolean scrolling = sourceTargetGroup.getWidth() > maxInnerWidth;
        float innerWidth = Math.min(sourceTargetGroup.getWidth(), maxInnerWidth);
        scrollPane.setScrollingDisabled(!scrolling, true);

        VisTable cardsPanel = new VisTable();
        cardsPanel.setBackground(SelectionPanelStyle.panel("121B1DEE", "87734E"));
        cardsPanel.pad(CARD_PANEL_PAD);
        Label hint = new Label(get("test.dragCards"), new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.valueOf("C8B995")));
        hint.setFontScale(0.32f);
        hint.setAlignment(Align.center);
        hint.setWrap(true);
        cardsPanel.add(hint).width(innerWidth).padBottom(CARD_PANEL_PAD).row();
        cardsPanel.add(scrollPane).width(innerWidth).height(sourceTargetGroup.getHeight());
        cardsPanel.pack();

        if (scrolling) {
            scrollPane.layout();
            scrollPane.setScrollPercentX(100);
            scrollPane.updateVisualScroll();
        }
        return cardsPanel;
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
        return restoreCombatAfterTest().andThen(Completable.create(onSub -> {
            if (scoreImportant) {
                confirmScoreTestResult(score);
            } else {
                confirmBinaryTestResult(successful);
            }
            // Zero-success combat skips the hit animation, so cleanup can start immediately.
            // Finish positioning first to prevent that movement fighting the hide animation.
            diceRollerStack.peek().moveUp(testTableStack.peek().getTop() + 5)
                    .subscribe(() -> autoConfirm(onSub), onSub::onError);
        }));
    }

    Completable restoreCombatAfterTest() {
        return Completable.defer(() -> {
            if (monsterCombatTableStack.isEmpty() || !monsterCombatTableStack.peek().isLocked()) {
                return Completable.complete();
            }
            MonsterCombatTable combatTable = monsterCombatTableStack.peek();
            combatTable.setLocked(false);
            // Nested spell tests move the panel aside. Auto-confirm has no OK button
            // to trigger the old button-hide listener, so restore it explicitly.
            return combatTable.moveLeft();
        });
    }

    @Override
    public Completable confirmRollResult(int score) {
        return Completable.create(onSub -> {
            hideDicesAfterConfirm = true;
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
            autoConfirm(onSub);
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

    private void confirmBinaryTestResult(boolean successful) {
        if (successful) {
            testTableStack.push(createPassedTable());
        } else {
            testTableStack.push(createFailedTable());
        }

        testTableStack.peek().setPosition(VIEWPORT_WIDTH / 2 - testTableStack.peek().getWidth() / 2, RESULT_TABLE_Y);
        InfoStage.showActor(testTableStack.peek());
    }

    private void confirmScoreTestResult(int score) {
        testTableStack.push(createScoreTable(score));
        testTableStack.peek().setPosition(VIEWPORT_WIDTH / 2 - testTableStack.peek().getWidth() / 2, RESULT_TABLE_Y);
        InfoStage.showActor(testTableStack.peek());

    }

    private void autoConfirm(CompletableSubscriber onSub) {
        // No OK button: keep the result readable for a moment, then continue as if it was pressed.
        InfoStage.addActionToInfoStage(Actions.sequence(
                new FastForwardAction<>(Actions.delay(RESULT_DISPLAY_DURATION)),
                Actions.run(() -> {
                    if (hideDicesAfterConfirm) {
                        diceRollerStack.pop().hideDices();
                        InfoStage.hideActor(testTableStack.pop());
                        InfoStage.setBottomHeight(5);
                        MapStage.brightenWorld();
                    }
                    InfoStage.setBottomHeight(5);
                    onSub.onCompleted();
                })));
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
        combatInvestigatorId = data.getInvestigatorId();
        return Completable.create(onSub -> {
            MapStage.darkenWorld();
            InfoStage.displayTextDontHide(get("combat.title"));
            String buttonTitle = get("combat.fight");

            CombatOverviewTable combatOverviewTable = new CombatOverviewTable();
            combatOverviewTable.init(data);
            // Centred horizontally, and vertically between the HUD and the "Combat" title.
            float overviewBottom = getHudTop();
            float overviewTop = VIEWPORT_HEIGHT - 48f;
            combatOverviewTable.setX(VIEWPORT_WIDTH / 2f - combatOverviewTable.getWidth() / 2f);
            combatOverviewTable.setY(overviewBottom + Math.max(0, (overviewTop - overviewBottom - combatOverviewTable.getHeight()) / 2f));
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
            CombatInterruption.start(monsterCombatTableStack.peek(), combatInvestigatorId);
            float centeredX = monsterCombatTableStack.peek().getCenteredX();
            float centeredY = monsterCombatTableStack.peek().getTopY();
            monsterCombatTableStack.peek().setPosition(centeredX, VIEWPORT_HEIGHT);
            monsterCombatTableStack.peek().addAction(new FastForwardAction<>(Actions.sequence(
                    moveTo(centeredX, centeredY, 1f, Interpolation.sine
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
            CombatInterruption.resume();
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


    private List<Vector2> successfulDiceSources(List<DiceRoll> diceRolls) {
        Map<Integer, Vector2> centers = diceRollerStack.peek().getLiveDiceCenters();
        Collections.sort(diceRolls, (o1, o2) -> o1.getDiceNr() - o2.getDiceNr());
        List<Vector2> result = new LinkedList<>();
        for (DiceRoll diceRoll : diceRolls) {
            Vector2 center = centers.get(diceRoll.getDiceNr());
            if (center == null) {
                continue;
            }
            if (diceRoll.getScore() == DiceRoll.Score.GOOD) {
                result.add(center);
            }
            if (diceRoll.getScore() == DiceRoll.Score.VERY_GOOD) {
                result.add(center);
                result.add(center);
            }
        }
        return result;
    }

    private Completable destroyHorrorOrDamage(List<DiceRoll> diceRolls, Function<List<Vector2>, Completable> destroyFunction, Action0 onEndAction) {
        CombatInterruption.resume();
        List<Vector2> result = successfulDiceSources(diceRolls);
        if (result.isEmpty()) {
            onEndAction.call();
            return Completable.complete();
        }

        return diceRollerStack.peek().waitUntilSettled()
                .andThen(Completable.defer(() -> destroyFunction.apply(successfulDiceSources(diceRolls))))
                .concatWith(Completable.create(onSub -> {
                    onEndAction.call();
                    onSub.onCompleted();
                }));
    }

    @Override
    public Completable destroySanity(int sanityLost) {
        ContainerBar bar = InfoStage.getInvestigatorHud().getSanityBar();
        return destroySanityOrHealth(sanityLost, bar,
                positions -> monsterCombatTableStack.peek().destroySanity(positions,
                        () -> loseTokenOnImpact(bar, PrePlayedTokenLoss::recordSanity, () -> impactTokenView.loseSanity(0))));
    }

    @Override
    public Completable destroyHealth(int healthLost) {
        ContainerBar bar = InfoStage.getInvestigatorHud().getHealthBar();
        return destroySanityOrHealth(healthLost, bar,
                positions -> monsterCombatTableStack.peek().destroyHealth(positions,
                        () -> loseTokenOnImpact(bar, PrePlayedTokenLoss::recordHealth, () -> impactTokenView.loseHealth(0))));
    }

    /** One token leaves the bar and tears per fireball impact; the later model-driven loss skips it. */
    private Completable loseTokenOnImpact(ContainerBar bar, Runnable record, Func0<Completable> loseToken) {
        if (bar.getCurrentValue() <= 0) {
            return Completable.complete();
        }
        record.run();
        TokenSounds.playLeave();
        return loseToken.call().doOnCompleted(() -> TokenSounds.playLoss(TokenSounds.Cue.LOSS, 1));
    }

    private Completable destroySanityOrHealth(int tokensCount, ContainerBar containerBar, Function<List<Vector2>, Completable> destroyFunction) {
        PrePlayedTokenLoss.reset();
        if (tokensCount == 0) {
            return Completable.complete();
        }
        CombatInterruption.resume();
        List<Vector2> endPositions = new LinkedList<>();
        Vector2 stand = investigatorStandPosition();
        if (stand != null) {
            // The monster aims at the investigator's stand on the map; points are spread so every fireball is distinct.
            for (int i = 0; i < tokensCount; i++) {
                endPositions.add(new Vector2(stand.x + (i - (tokensCount - 1) / 2f) * 12f, stand.y + MathUtils.random(-8f, 8f)));
            }
            return destroyFunction.apply(endPositions);
        }
        for (int i = 0; i < tokensCount; i++) {
            Vector2 position = containerBar.getFullContainerPosition(i);
            Vector2 end = new Vector2(position.x + 11, position.y + 13.5f);
            // Fireball callbacks are keyed by target, so every target must be distinct.
            while (endPositions.contains(end)) {
                end.x += 1f;
            }
            endPositions.add(end);
        }
        Collections.reverse(endPositions);
        return destroyFunction.apply(endPositions);
    }

    /**
     * Centre of the fighting investigator's stand, in info-stage coordinates; null when it is not on screen.
     * The map wraps horizontally, so each stand exists in several copies - the one on screen is used.
     */
    private Vector2 investigatorStandPosition() {
        Vector2 best = null;
        for (InvestigatorImage stand : findInvestigatorStands()) {
            Vector2 mapCoordinates = stand.localToStageCoordinates(new Vector2(stand.getWidth() / 2f, stand.getHeight() / 2f));
            Vector2 screen = MapStage.getStage().stageToScreenCoordinates(mapCoordinates);
            Vector2 info = InfoStage.getStageSafe().screenToStageCoordinates(screen);
            boolean onScreen = info.x >= 0 && info.x <= VIEWPORT_WIDTH && info.y >= 0 && info.y <= VIEWPORT_HEIGHT;
            if (onScreen && (best == null || Math.abs(info.x - VIEWPORT_WIDTH / 2f) < Math.abs(best.x - VIEWPORT_WIDTH / 2f))) {
                best = info;
            }
        }
        return best;
    }

    private List<InvestigatorImage> findInvestigatorStands() {
        List<InvestigatorImage> stands = new LinkedList<>();
        if (combatInvestigatorId != null) {
            List<InvestigatorImage> byId = MapStage.getActor(InvestigatorUtils.getIdLayerResolver(combatInvestigatorId, false));
            if (byId != null) {
                stands.addAll(byId);
            }
        }
        if (stands.isEmpty()) {
            // No known fighter: only the combat location's stands stay visible during combat.
            for (Actor actor : MapStage.getAllActors(MapStage.getInvestigatorLayer())) {
                if (actor instanceof InvestigatorImage && actor.isVisible() && actor.getColor().a > 0.5f) {
                    stands.add((InvestigatorImage) actor);
                }
            }
        }
        return stands;
    }

    @Override
    public Completable destroyMonsterHealth(List<DiceRoll> diceRolls) {
        CombatInterruption.resume();
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
                    completable.andThen(diceRollerStack.peek().waitUntilSettled()).subscribe(() -> {
                        List<Vector2> result = successfulDiceSources(diceRolls);
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
        CombatInterruption.finish();
        return Completable.create(onSub -> {
            monsterCombatTableStack.peek().addAction(new AfterCenteredAction(() -> {
                MapStage.brightenWorld();
                for (Subscription monsterCombatTableSubscription : monsterCombatTableSubscriptions) {
                    monsterCombatTableSubscription.unsubscribe();
                }
                monsterCombatTableSubscriptions.clear();
                monsterCombatTableStack.peek().addAction(new FastForwardAction<>(
                        Actions.after(Actions.sequence(
                                moveTo(monsterCombatTableStack.peek().getCenteredX(), VIEWPORT_HEIGHT, 1f, Interpolation.sine),
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
        CombatInterruption.resume();
        return monsterCombatTableStack.peek().updateHorror(actualHorror);
    }

    @Override
    public Completable updateMonsterDamage(Integer actualDamage) {
        CombatInterruption.resume();
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
