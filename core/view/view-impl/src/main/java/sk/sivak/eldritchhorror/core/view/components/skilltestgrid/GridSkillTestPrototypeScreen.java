package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.skilltestgrid.GridTestAudio.Cue;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder.buildButton;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class GridSkillTestPrototypeScreen extends ScreenAdapter {
    private static final int DEFAULT_MOVES = 60;
    private static final float PLAY_AREA_SCALE = 0.80f * 1.2f;
    private static final float LEFT_HUD_SCALE = 0.85f;
    private static final float BOARD_WIDTH_RATIO = 0.50f;
    private static final float BOARD_HEIGHT_RATIO = 0.58f;
    private static final float UI_LABEL_SCALE = 0.70f;
    private final Stage stage;
    private final RandomSymbolProvider randomProvider;
    private final GridTestController controller;
    private final GridBoardActor boardActor;
    private final GridTestSoundHooks soundHooks;
    private final Random random;
    private final GridTestAssets assets;
    private final NextTokenPreview nextTokenPreview;
    private final Label shiftsLabel;
    private final Label swapsLabel;
    private final Label successesLabel;
    private final Label resourceWarningLabel;
    private final Label gainLabel;
    private final Label endLabel;
    private final TextButton restartButton;
    private final TextButton rerollButton;
    private final TextButton superRerollButton;
    private final TextButton pickupButton;
    private final SelectBox<TestMode> modeSelectBox;
    private final SelectBox<String> gapSelectBox;
    private final CheckBox momentumCheckBox;
    private final CheckBox blindCheckBox;
    private final Table controlPanel;
    private final GridTestModePreferences modePreferences;
    private final GridTestMomentumPreferences momentumPreferences;
    private final GridTestBlindPreferences blindPreferences;
    private final SymbolReroller reroller;
    private final SymbolReroller superReroller;
    private final GridTestAudio audio = new GridTestAudio();
    private int configuredMoves;
    private GridTestResult result;
    private boolean tacticalEffectPreservingNextToken;

    public GridSkillTestPrototypeScreen(int moves) {
        this(moves, new Random(), GridTestSoundHooks.NO_OP);
    }

    public GridSkillTestPrototypeScreen(int moves, Random random, GridTestSoundHooks soundHooks) {
        this.configuredMoves = moves;
        this.soundHooks = soundHooks == null ? GridTestSoundHooks.NO_OP : soundHooks;
        this.random = random == null ? new Random() : random;
        stage = new Stage(new FitViewport(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT));
        randomProvider = new RandomSymbolProvider(this.random);
        controller = new GridTestController(new GridBoard(randomProvider, this.random));
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        modePreferences = new GridTestModePreferences(preferences);
        momentumPreferences = new GridTestMomentumPreferences(preferences);
        blindPreferences = new GridTestBlindPreferences(preferences);
        controller.setSelectedMode(modePreferences.load());
        controller.setConfiguredMomentum(momentumPreferences.load());
        controller.setConfiguredBlindEnabled(blindPreferences.load());
        reroller = new SymbolReroller(this.random);
        superReroller = new SymbolReroller(this.random);
        controller.startTest(moves);
        assets = new GridTestAssets();
        boardActor = new GridBoardActor(controller, assets);
        boardActor.setLayoutScale(PLAY_AREA_SCALE);
        boardActor.setMoveSelectedListener(this::onMoveSelected);
        boardActor.setTokenTappedListener(this::onTokenTapped);
        boardActor.setSwapTokenSelectedListener(position -> audio.play(Cue.SELECT));
        boardActor.setRefillStartedListener(() -> audio.play(Cue.SPAWN));
        boardActor.setInvalidTargetListener(() -> audio.play(Cue.UNAVAILABLE));
        boardActor.setInteractionEnabled(false);

        Label.LabelStyle titleStyle = new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_BLACK_CHANCERY), Color.WHITE);
        Label.LabelStyle gainStyle = new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_ADLER), new Color(0x6fff6fff));

        shiftsLabel = new Label("Shifts: 0", titleStyle);
        swapsLabel = new Label("Swaps: 0", titleStyle);
        successesLabel = new Label("successes: 0", titleStyle);
        resourceWarningLabel = new Label("No Shifts remaining", titleStyle);
        resourceWarningLabel.setColor(0.95f, 0.4f, 0.45f, 0f);
        resourceWarningLabel.setWrap(true);
        nextTokenPreview = new NextTokenPreview(assets);
        gainLabel = new Label("", gainStyle);
        endLabel = new Label("", titleStyle);
        restartButton = buildButton("RESTART");
        rerollButton = buildButton("REROLL");
        superRerollButton = buildButton("SUPER REROLL");
        pickupButton = buildButton("PICKUP");
        modeSelectBox = new SelectBox<>(CustomAssetManager.getSkin());
        modeSelectBox.setItems(TestMode.BLESSED, TestMode.NORMAL, TestMode.CURSED);
        modeSelectBox.setSelected(controller.getSelectedMode());
        modeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TestMode selectedMode = modeSelectBox.getSelected();
                controller.setSelectedMode(selectedMode);
                modePreferences.save(selectedMode);
                audio.play(Cue.SETTING);
            }
        });
        gapSelectBox = new SelectBox<>(CustomAssetManager.getSkin());
        gapSelectBox.setItems("0 GAPs", "1 GAP", "2 GAPs", "3 GAPs");
        gapSelectBox.setSelectedIndex(controller.getConfiguredGapCount());
        gapSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setConfiguredGapCount(gapSelectBox.getSelectedIndex());
                audio.play(Cue.SETTING);
            }
        });
        blindCheckBox = new CheckBox("Blind", CustomAssetManager.getSkin());
        blindCheckBox.setChecked(controller.isConfiguredBlindEnabled());
        blindCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean configuredBlind = blindCheckBox.isChecked();
                controller.setConfiguredBlindEnabled(configuredBlind);
                blindPreferences.save(configuredBlind);
                audio.play(Cue.SETTING);
            }
        });
        momentumCheckBox = new CheckBox("Momentum", CustomAssetManager.getSkin());
        momentumCheckBox.setChecked(controller.isConfiguredMomentum());
        momentumCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean configuredMomentum = momentumCheckBox.isChecked();
                controller.setConfiguredMomentum(configuredMomentum);
                momentumPreferences.save(configuredMomentum);
                audio.play(Cue.SETTING);
            }
        });

        configureLabel(shiftsLabel, Align.left);
        configureLabel(swapsLabel, Align.left);
        configureLabel(successesLabel, Align.center);
        configureLabel(gainLabel, Align.center);
        configureLabel(endLabel, Align.center);
        shiftsLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        swapsLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        successesLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        restartButton.setTransform(true);
        restartButton.setOrigin(0f, 0f);
        restartButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        rerollButton.setTransform(true);
        rerollButton.setOrigin(0f, 0f);
        rerollButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        superRerollButton.setTransform(true);
        superRerollButton.setOrigin(0f, 0f);
        superRerollButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        pickupButton.setTransform(true);
        pickupButton.setOrigin(0f, 0f);
        pickupButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);

        stage.addActor(boardActor);
        stage.addActor(nextTokenPreview);
        stage.addActor(gainLabel);
        stage.addActor(endLabel);
        
        // Build control panel as a responsive table
        controlPanel = buildControlPanel();
        stage.addActor(controlPanel);

        addClickListener(restartButton, () -> startTest(configuredMoves));
        addClickListener(rerollButton, this::onRerollPressed);
        addClickListener(superRerollButton, this::onSuperRerollPressed);
        addClickListener(pickupButton, this::onPickupPressed);

        updateCounters();
        setNextTokenPreviewVisible(false);
        layoutUi(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT);
        setInputEnabled(true);
        audio.play(Cue.TEST_START);
        startResolutionLoop(false);
    }

    public void startTest(int moves) {
        audio.stopAll();
        configuredMoves = moves;
        result = null;
        tacticalEffectPreservingNextToken = false;
        randomProvider.clearNextTokenReservation();
        endLabel.setText("");
        clearResourceWarning();
        controller.startTest(moves);
        boardActor.resetAnimations();
        boardActor.syncBoardToActors();
        nextTokenPreview.clearActions();
        nextTokenPreview.clearNextToken();
        setNextTokenPreviewVisible(false);
        boardActor.setInteractionEnabled(false);
        restartButton.setDisabled(false);
        updateCounters();
        audio.play(Cue.TEST_START);
        startResolutionLoop(false);
    }

    public void setRandomSeed(long seed) {
        randomProvider.setSeed(seed);
    }

    public void setBoard(SymbolType... symbols) {
        audio.stopAll();
        clearResourceWarning();
        controller.setDebugBoard(symbols);
        boardActor.resetAnimations();
        boardActor.syncBoardToActors();
        nextTokenPreview.clearNextToken();
        setNextTokenPreviewVisible(false);
        startResolutionLoop(false);
    }

    private void configureLabel(Label label, int align) {
        label.setAlignment(align);
    }

    private void onMoveSelected(GridMove move) {
        if (!controller.canAcceptInput()) {
            if (controller.getMovesRemaining() == 0
                    && (controller.getState() == GridTestState.WAITING_FOR_INPUT
                    || controller.getState() == GridTestState.FINISHED)) {
                boardActor.showExhaustedShift(move);
                showResourceWarning("No Shifts remaining");
            }
            return;
        }
        boardActor.setInteractionEnabled(false);
        if (controller.isBlindEnabled()) {
            controller.commitBlindMove(move);
            executeCommittedBlindMove();
            return;
        }
        beginShift(controller.applyMove(move));
    }

    private void executeCommittedBlindMove() {
        if (controller.getState() != GridTestState.REVEALING_NEXT_TOKEN) {
            return;
        }
        beginShift(controller.applyCommittedBlindMove());
    }

    private void beginShift(GridShiftOutcome shiftOutcome) {
        nextTokenPreview.clearNextToken();
        setNextTokenPreviewVisible(false);
        updateCounters();
        soundHooks.onShift();
        audio.play(Cue.SHIFT);
        boardActor.animateShift(shiftOutcome, () -> {
            soundHooks.onSymbolEnter();
            startResolutionLoop(false);
        });
    }

    private void startResolutionLoop(boolean cascade) {
        controller.setState(GridTestState.CHECKING_MATCHES);
        List<GridMatch> matches = controller.findMatches();
        if (matches.isEmpty()) {
            onBoardStable();
            return;
        }

        if (cascade) {
            soundHooks.onCascade();
        }
        int shiftsBefore = controller.getMovesRemaining();
        MatchResolution resolution = controller.resolveMatches(matches);
        int bonusShifts = controller.getMovesRemaining() - shiftsBefore;
        if (!tacticalEffectPreservingNextToken) {
            setNextTokenPreviewVisible(false);
        }
        controller.setState(GridTestState.MATCH_ANIMATION);
        boardActor.setInteractionEnabled(false);
        int successesGained = resolution.getSuccessesGained();
        if (successesGained > 0) {
            pulseSuccessCounter(successesGained);
        }
        soundHooks.onMatch(successesGained, cascade);
        audio.playMatchWave(matches, bonusShifts, cascade);
        boardActor.animateMatchWave(matches, resolution.getReplacements(), () -> {
            updateCounters();
            controller.setState(GridTestState.CASCADE_CHECK);
            startResolutionLoop(true);
        });
    }

    private void onBoardStable() {
        if (controller.shouldFinishWhenStable()) {
            finishTest();
            return;
        }
        if (tacticalEffectPreservingNextToken) {
            releaseNextTokenFromTacticalEffect();
        }
        refreshNextTokenPreview();
        setNextTokenPreviewVisible(true);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        updateCounters();
        boardActor.setInteractionEnabled(true);
    }

    private void finishTest() {
        result = controller.finish();
        boardActor.setInteractionEnabled(false);
        setNextTokenPreviewVisible(false);
        restartButton.setDisabled(false);
        endLabel.setText("TEST COMPLETE");
        endLabel.pack();
        endLabel.setPosition(ViewProperties.VIEWPORT_WIDTH / 2f - endLabel.getWidth() / 2f, ViewProperties.VIEWPORT_HEIGHT * 0.08f);
        endLabel.getColor().a = 0f;
        endLabel.addAction(new FastForwardAction<>(Actions.alpha(1f, 0.3f, Interpolation.sineOut)));
        updateCounters();
        soundHooks.onTestComplete(result);
        audio.play(Cue.TEST_COMPLETE);
    }

    private void pulseSuccessCounter(int gained) {
        successesLabel.clearActions();
        successesLabel.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.scaleTo(1.3f, 1.3f, 0.12f, Interpolation.sineOut),
                Actions.scaleTo(1f, 1f, 0.12f, Interpolation.sineIn)
        )));
        gainLabel.clearActions();
        gainLabel.setText("+" + gained);
        gainLabel.pack();
        gainLabel.setPosition(successesLabel.getX() + successesLabel.getWidth() + 8f, successesLabel.getY());
        gainLabel.getColor().a = 1f;
        gainLabel.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(
                        Actions.moveBy(0, 24f, 0.35f, Interpolation.sineOut),
                        Actions.alpha(0f, 0.35f, Interpolation.sineOut)
                ),
                Actions.run(() -> gainLabel.setText(""))
        )));
    }

    private void updateCounters() {
        clearResourceWarning();
        shiftsLabel.setText("Shifts: " + controller.getMovesRemaining());
        swapsLabel.setText("Swaps: " + controller.getSwapRemaining());
        successesLabel.setText("successes: " + controller.getSuccesses());
        updateRerollButtonState();
        updateSuperRerollButtonState();
        updatePickupButtonState();
    }

    private void showResourceWarning(String message) {
        audio.play(Cue.UNAVAILABLE);
        resourceWarningLabel.clearActions();
        resourceWarningLabel.setText(message);
        resourceWarningLabel.getColor().a = 1f;
        resourceWarningLabel.addAction(Actions.sequence(
                Actions.delay(1.1f),
                Actions.alpha(0f, 0.4f, Interpolation.fade)
        ));
    }

    private void clearResourceWarning() {
        resourceWarningLabel.clearActions();
        resourceWarningLabel.getColor().a = 0f;
    }

    private void onRerollPressed() {
        if (controller.getState() == GridTestState.REROLL_SELECTING) {
            controller.cancelRerollTargeting();
            boardActor.exitRerollTargetingMode();
            audio.play(Cue.CANCEL);
            updateCounters();
            return;
        }
        if (!controller.beginRerollTargeting()) {
            return;
        }
        boardActor.enterRerollTargetingMode(this::onRerollTargetSelected);
        audio.play(Cue.SELECT);
        updateCounters();
    }

    private void onRerollTargetSelected(GridPosition position) {
        if (controller.getState() != GridTestState.REROLL_SELECTING) {
            return;
        }
        boardActor.setInteractionEnabled(false);
        reserveNextTokenForTacticalEffect();
        SymbolType rerolledSymbol = controller.performReroll(position, reroller);
        controller.setState(GridTestState.MATCH_ANIMATION);
        updateCounters();
        audio.play(Cue.REROLL);
        boardActor.animateReroll(position, rerolledSymbol, () -> startResolutionLoop(false));
    }

    private void updateRerollButtonState() {
        boolean targeting = controller.getState() == GridTestState.REROLL_SELECTING;
        rerollButton.setDisabled(!targeting && !controller.canActivateReroll());
        rerollButton.setText(targeting
                ? "CANCEL REROLL"
                : "REROLL x" + controller.getRemainingRerolls());
    }

    private void onSuperRerollPressed() {
        if (!controller.canUseSuperReroll()) {
            return;
        }

        boardActor.setInteractionEnabled(false);
        reserveNextTokenForTacticalEffect();
        Map<GridPosition, SymbolType> rerolledCells = controller.performSuperReroll(superReroller);
        if (rerolledCells.isEmpty()) {
            releaseNextTokenFromTacticalEffect();
            onBoardStable();
            return;
        }

        controller.setState(GridTestState.MATCH_ANIMATION);
        updateCounters();
        audio.play(Cue.SUPER_REROLL);
        boardActor.animateSuperReroll(rerolledCells, () -> {
            controller.setState(GridTestState.CHECKING_MATCHES);
            startResolutionLoop(false);
        });
    }

    private void updateSuperRerollButtonState() {
        boolean canUse = controller.canUseSuperReroll();
        superRerollButton.setDisabled(!canUse);
        superRerollButton.setText("SUPER REROLL");
    }

    private void onTokenTapped(GridPosition position) {
        if (!controller.beginSwapSelection(position)) {
            if (controller.getSwapRemaining() == 0
                    && (controller.getState() == GridTestState.WAITING_FOR_INPUT
                    || controller.getState() == GridTestState.FINISHED)) {
                boardActor.showExhaustedSwap(position);
                showResourceWarning("No Swaps remaining");
            }
            return;
        }
        boardActor.enterSwapSelectionMode(position, this::onSwapComplete);
        updateCounters();
    }

    private void onPickupPressed() {
        if (controller.getState() == GridTestState.PICKUP_SELECTING) {
            controller.cancelPickupMode();
            boardActor.exitPickupTargetingMode();
            audio.play(Cue.CANCEL);
            updateCounters();
            return;
        }
        if (!controller.startPickupMode()) {
            return;
        }
        boardActor.enterPickupTargetingMode(this::onPickupTargetSelected);
        audio.play(Cue.SELECT);
        updateCounters();
    }

    private void onPickupTargetSelected(GridPosition position) {
        if (controller.getState() != GridTestState.PICKUP_SELECTING) {
            return;
        }
        boardActor.setInteractionEnabled(false);
        controller.pickupToken(position.getRow(), position.getColumn());
        updateCounters();
        audio.play(Cue.PICKUP);
        boardActor.animatePickup(position, () -> {
            boardActor.syncBoardToActors();
            refreshNextTokenPreview();
            setNextTokenPreviewVisible(true);
            onBoardStable();
        });
    }

    private void updatePickupButtonState() {
        boolean selecting = controller.getState() == GridTestState.PICKUP_SELECTING;
        boolean available = controller.getPickupsAvailable() > 0;
        pickupButton.setVisible(available);
        pickupButton.setDisabled(!selecting && !controller.canUsePickup());
        pickupButton.setText(selecting
                ? "CANCEL PICKUP"
                : "PICKUP x" + controller.getPickupsAvailable());
    }

    public void onSwapComplete(GridPosition pos1, GridPosition pos2) {
        if (pos1 != null && pos2 != null) {
            Gdx.app.log("SWAP", "onSwapComplete called with pos1=(" + pos1.getRow() + "," + pos1.getColumn() + ") pos2=(" + pos2.getRow() + "," + pos2.getColumn() + ")");
            boardActor.setInteractionEnabled(false);
            reserveNextTokenForTacticalEffect();
            controller.useSwap();
            int shiftsBefore = controller.getMovesRemaining();
            MatchResolution resolution = controller.performSwap(pos1, pos2);
            int bonusShifts = controller.getMovesRemaining() - shiftsBefore;
            Gdx.app.log("SWAP", "performSwap completed, matches found: " + resolution.getMatches().size());
            
            audio.play(Cue.SWAP);
            boardActor.animateSwap(pos1, pos2, () -> {
                Gdx.app.log("SWAP", "animateSwap animation complete");
                updateCounters();
                
                if (resolution.getSuccessesGained() > 0) {
                    pulseSuccessCounter(resolution.getSuccessesGained());
                }
                
                soundHooks.onMatch(resolution.getSuccessesGained(), false);
                audio.playMatchWave(resolution.getMatches(), bonusShifts, false);
                
                if (!resolution.getReplacements().isEmpty()) {
                    Gdx.app.log("SWAP", "Matches detected, animating match wave");
                    controller.setState(GridTestState.MATCH_ANIMATION);
                    boardActor.animateMatchWave(resolution.getMatches(), resolution.getReplacements(), () -> {
                        updateCounters();
                        controller.setState(GridTestState.CASCADE_CHECK);
                        startResolutionLoop(true);
                    });
                } else {
                    Gdx.app.log("SWAP", "No matches, returning to stable board");
                    onBoardStable();
                }
            });
        } else {
            Gdx.app.log("SWAP", "onSwapComplete cancelled - pos1 or pos2 is null");
            // Cancelled swap - re-enable normal interaction
            controller.setState(GridTestState.WAITING_FOR_INPUT);
            boardActor.setInteractionEnabled(true);
            audio.play(Cue.CANCEL);
            updateCounters();
        }
    }

    private void reserveNextTokenForTacticalEffect() {
        controller.reserveNextToken();
        tacticalEffectPreservingNextToken = true;
    }

    private void releaseNextTokenFromTacticalEffect() {
        controller.releaseNextToken();
        tacticalEffectPreservingNextToken = false;
    }

    private void refreshNextTokenPreview() {
        nextTokenPreview.setNextToken(controller.getNextToken());
        nextTokenPreview.setHidden(
                controller.isBlindEnabled()
                        && controller.getState() != GridTestState.REVEALING_NEXT_TOKEN
        );
    }

    private void setNextTokenPreviewVisible(boolean visible) {
        nextTokenPreview.setVisible(visible && !controller.isBlindEnabled());
    }

    private Table buildControlPanel() {
        Table panel = new Table();
        panel.pad(8f);
        
        float controlScale = PLAY_AREA_SCALE * LEFT_HUD_SCALE;
        float panelWidth = ViewProperties.VIEWPORT_WIDTH * 0.25f * controlScale;
        float buttonHeight = ViewProperties.VIEWPORT_HEIGHT * 0.06f * controlScale;
        float statsFontScale = UI_LABEL_SCALE * controlScale;
        
        // Stats block
        Table statsTable = new Table();
        statsTable.align(Align.left);
        
        shiftsLabel.setFontScale(statsFontScale);
        swapsLabel.setFontScale(statsFontScale);
        successesLabel.setFontScale(statsFontScale);
        resourceWarningLabel.setFontScale(statsFontScale * 0.75f);
        statsTable.add(shiftsLabel).left().padBottom(2f).row();
        statsTable.add(swapsLabel).left().padBottom(2f).row();
        statsTable.add(successesLabel).left().padBottom(2f).row();
        statsTable.add(resourceWarningLabel).width(panelWidth).left().padTop(4f).row();
        
        panel.add(statsTable).left().padBottom(12f).row();
        
        // Restart button
        restartButton.setTransform(true);
        restartButton.setOrigin(0f, 0f);
        restartButton.setScale(controlScale);
        panel.add(restartButton).width(panelWidth).height(buttonHeight * 1.2f).padBottom(8f).center().row();
        
        // Mode select box
        modeSelectBox.setSize(panelWidth / controlScale, buttonHeight / controlScale);
        panel.add(modeSelectBox).width(panelWidth).height(buttonHeight).padBottom(8f).center().row();
        gapSelectBox.setSize(panelWidth / controlScale, buttonHeight / controlScale);
        panel.add(gapSelectBox).width(panelWidth).height(buttonHeight).padBottom(8f).center().row();

        // Momentum applies to the next started or restarted test.
        momentumCheckBox.getLabel().setFontScale(statsFontScale);
        panel.add(momentumCheckBox).left().padBottom(4f).row();
        blindCheckBox.getLabel().setFontScale(statsFontScale);
        panel.add(blindCheckBox).left().padBottom(8f).row();
        
        rerollButton.setTransform(true);
        rerollButton.setOrigin(0f, 0f);
        rerollButton.setScale(controlScale);
        panel.add(rerollButton).width(panelWidth).height(buttonHeight).padBottom(8f).center().row();

        superRerollButton.setTransform(true);
        superRerollButton.setOrigin(0f, 0f);
        superRerollButton.setScale(controlScale);
        panel.add(superRerollButton).width(panelWidth).height(buttonHeight).padBottom(8f).center().row();
        
        pickupButton.setTransform(true);
        pickupButton.setOrigin(0f, 0f);
        pickupButton.setScale(controlScale);
        panel.add(pickupButton).width(panelWidth).height(buttonHeight).padBottom(20f).center().row();
        
        // Position the panel
        panel.setSize(panelWidth + 16f, ViewProperties.VIEWPORT_HEIGHT * 0.64f);
        panel.setPosition(
            ViewProperties.VIEWPORT_WIDTH * 0.02f, 
            ViewProperties.VIEWPORT_HEIGHT * 0.5f - panel.getHeight() / 2f
        );
        
        return panel;
    }

    private void layoutUi(float width, float height) {
        float centerX = width * 0.5f;
        float centerY = height * 0.5f;
        float boardSize = Math.min(width * BOARD_WIDTH_RATIO, height * BOARD_HEIGHT_RATIO) * PLAY_AREA_SCALE;
        boardActor.layout(centerX, centerY, boardSize);
        float boardBottom = centerY - boardSize / 2f;

        float nextPreviewSize = height * 0.12f * PLAY_AREA_SCALE;
        nextTokenPreview.setSize(nextPreviewSize, nextPreviewSize);

        float nextTokenGap = height * 0.072f * PLAY_AREA_SCALE;
        float nextTokenY = boardBottom - nextTokenGap - nextPreviewSize;
        nextTokenPreview.setPosition(centerX - nextTokenPreview.getWidth() / 2f, nextTokenY);
    }

    private static float scaleAround(float anchor, float value, float scale) {
        return anchor + (value - anchor) * scale;
    }

    private void setInputEnabled(boolean enabled) {
        if (enabled) {
            Gdx.input.setInputProcessor(stage);
        } else if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    public GridTestResult getResult() {
        return result;
    }

    @Override
    public void show() {
        setInputEnabled(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.07f, 0.07f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        layoutUi(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    }

    @Override
    public void hide() {
        audio.stopAll();
        setInputEnabled(false);
    }

    @Override
    public void dispose() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
        audio.dispose();
        stage.dispose();
    }
}
