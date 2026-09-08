package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private static final float PLAY_AREA_SCALE = 0.80f * 1.2f;
    private static final float BOARD_WIDTH_RATIO = 0.42f;
    private static final float BOARD_HEIGHT_RATIO = 0.58f;
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
    private final Label rulesLabel;
    private final Label setupHint;
    private final TextButton restartButton;
    private final TextButton rerollButton;
    private final TextButton superRerollButton;
    private final TextButton pickupButton;
    private final Table controlPanel;
    private final GridTestSetupPanel setupPanel;
    private final SymbolReroller reroller;
    private final SymbolReroller superReroller;
    private final GridTestAudio audio = new GridTestAudio();
    private boolean preparing = true;
    private GridTestResult result;
    private boolean tacticalEffectPreservingNextToken;

    public GridSkillTestPrototypeScreen() {
        this(new Random(), GridTestSoundHooks.NO_OP);
    }

    public GridSkillTestPrototypeScreen(Random random, GridTestSoundHooks soundHooks) {
        this.soundHooks = soundHooks == null ? GridTestSoundHooks.NO_OP : soundHooks;
        this.random = random == null ? new Random() : random;
        stage = new Stage(new FitViewport(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT));
        randomProvider = new RandomSymbolProvider(this.random);
        controller = new GridTestController(new GridBoard(randomProvider, this.random));
        reroller = new SymbolReroller(this.random);
        superReroller = new SymbolReroller(this.random);
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
        successesLabel = new Label("Successes: 0", titleStyle);
        resourceWarningLabel = new Label("No Shifts remaining", titleStyle);
        resourceWarningLabel.setColor(0.95f, 0.4f, 0.45f, 0f);
        resourceWarningLabel.setWrap(true);
        nextTokenPreview = new NextTokenPreview(assets);
        gainLabel = new Label("", gainStyle);
        endLabel = new Label("", titleStyle);
        endLabel.setWrap(true);
        endLabel.setVisible(false);
        rulesLabel = new Label("", titleStyle);
        rulesLabel.setWrap(true);
        setupHint = new Label("Prepare your test\n\nConfirm parameters on the left to start.", titleStyle);
        setupHint.setAlignment(Align.center);
        setupHint.setWrap(true);
        setupHint.setFontScale(0.55f);
        setupHint.setColor(0.7f, 0.7f, 0.75f, 1f);
        restartButton = buildButton("NEW TEST");
        rerollButton = buildButton("REROLL");
        superRerollButton = buildButton("SUPER REROLL");
        pickupButton = buildButton("LIFT");

        configureLabel(shiftsLabel, Align.left);
        configureLabel(swapsLabel, Align.left);
        configureLabel(successesLabel, Align.center);
        configureLabel(gainLabel, Align.center);
        configureLabel(endLabel, Align.left);

        stage.addActor(boardActor);
        stage.addActor(nextTokenPreview);
        stage.addActor(gainLabel);
        stage.addActor(setupHint);

        controlPanel = buildControlPanel();
        stage.addActor(controlPanel);
        TextButton confirm = buildButton("CONFIRM & START");
        configurePanelButton(confirm);
        setupPanel = new GridTestSetupPanel(CustomAssetManager.getSkin(), confirm,
                GridTestParameters.randomized(this.random), this::startTest, () -> audio.play(Cue.SETTING));
        setupPanel.setBackground(new TextureRegionDrawable(assets.getWhitePixel())
                .tint(new Color(0.11f, 0.12f, 0.15f, 1f)));
        stage.addActor(setupPanel);

        addClickListener(restartButton, this::showSetup);
        addClickListener(rerollButton, this::onRerollPressed);
        addClickListener(superRerollButton, this::onSuperRerollPressed);
        addClickListener(pickupButton, this::onPickupPressed);

        boardActor.setVisible(false);
        controlPanel.setVisible(false);
        setNextTokenPreviewVisible(false);
        layoutUi(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT);
        setInputEnabled(true);
    }

    public void startTest(GridTestParameters parameters) {
        resetPresentation();
        controller.startTest(parameters);
        showPreparedTest();
    }

    public void startTest(int moves) {
        resetPresentation();
        controller.startTest(moves);
        showPreparedTest();
    }

    private void resetPresentation() {
        audio.stopAll();
        stage.cancelTouchFocus();
        boardActor.setInteractionEnabled(false);
        boardActor.resetAnimations();
        result = null;
        tacticalEffectPreservingNextToken = false;
        randomProvider.clearNextTokenReservation();
        endLabel.clearActions();
        endLabel.setText("");
        endLabel.setVisible(false);
        rulesLabel.setVisible(true);
        gainLabel.clearActions();
        gainLabel.setText("");
        successesLabel.clearActions();
        successesLabel.setScale(1f);
        clearResourceWarning();
        nextTokenPreview.clearActions();
        nextTokenPreview.clearNextToken();
        setNextTokenPreviewVisible(false);
    }

    private void showPreparedTest() {
        preparing = false;
        setupPanel.setVisible(false);
        setupHint.setVisible(false);
        controlPanel.setVisible(true);
        boardActor.setVisible(true);
        boardActor.syncBoardToActors();
        restartButton.setDisabled(false);
        rulesLabel.setText(controller.getActiveMode() + "  |  Gaps: " + controller.getGapCount()
                + "\nMomentum: " + (controller.isActiveMomentum() ? "On" : "Off")
                + "  |  Blind: " + (controller.isBlindEnabled() ? "On" : "Off"));
        updateCounters();
        audio.play(Cue.TEST_START);
        startResolutionLoop(false);
    }

    private void showSetup() {
        resetPresentation();
        preparing = true;
        controller.setState(GridTestState.INITIALIZING);
        boardActor.setVisible(false);
        controlPanel.setVisible(false);
        setupHint.setVisible(true);
        setupPanel.setParameters(GridTestParameters.randomized(random));
        setupPanel.setVisible(true);
        stage.setKeyboardFocus(null);
        stage.setScrollFocus(null);
        audio.play(Cue.SETTING);
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
        if (!preparing) {
            startResolutionLoop(false);
        }
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
        if (controller.hasReachedSuccessTarget()) {
            onBoardStable();
            return;
        }
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
        if (tacticalEffectPreservingNextToken) {
            releaseNextTokenFromTacticalEffect();
        }
        if (controller.shouldFinishWhenStable()) {
            finishTest();
            return;
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
        switch (result.getOutcome()) {
            case SUCCESS:
                endLabel.setText("TEST SUCCESSFUL");
                endLabel.setColor(0.55f, 1f, 0.65f, 1f);
                break;
            case FAILURE:
                endLabel.setText("TEST FAILED");
                endLabel.setColor(1f, 0.5f, 0.5f, 1f);
                break;
            case SCORE_ONLY:
                endLabel.setText("TEST COMPLETE");
                endLabel.setColor(Color.WHITE);
                break;
        }
        endLabel.setText(endLabel.getText() + "\nShifts used: " + result.getMovesUsed());
        rulesLabel.setVisible(false);
        endLabel.setVisible(true);
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
        Vector2 gainPosition = successesLabel.localToStageCoordinates(new Vector2(successesLabel.getWidth() + 8f, 0f));
        gainLabel.setPosition(gainPosition.x, gainPosition.y);
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
        GridSuccessTarget target = controller.getSuccessTarget();
        successesLabel.setText("Successes: " + controller.getSuccesses()
                + (target.isUnlimited() ? "\nScore only" : " / " + target.getMinimumSuccesses()));
        successesLabel.setColor(!target.isUnlimited() && controller.getSuccesses() >= target.getMinimumSuccesses()
                ? new Color(0.55f, 1f, 0.65f, 1f) : Color.WHITE);
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
        superRerollButton.setText("SUPER REROLL x" + controller.getSuperRerollsRemaining());
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
        pickupButton.setDisabled(!selecting && !controller.canUsePickup());
        pickupButton.setText(selecting
                ? "CANCEL LIFT"
                : "LIFT x" + controller.getPickupsAvailable());
    }

    public void onSwapComplete(GridPosition pos1, GridPosition pos2) {
        if (pos1 != null && pos2 != null) {
            Gdx.app.log("SWAP", "onSwapComplete called with pos1=(" + pos1.getRow() + "," + pos1.getColumn() + ") pos2=(" + pos2.getRow() + "," + pos2.getColumn() + ")");
            boardActor.setInteractionEnabled(false);
            reserveNextTokenForTacticalEffect();
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
            controller.cancelSwapSelection();
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
        nextTokenPreview.setVisible(visible && !preparing && !controller.isBlindEnabled());
    }

    private Table buildControlPanel() {
        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(assets.getWhitePixel())
                .tint(new Color(0.11f, 0.12f, 0.15f, 1f)));
        panel.pad(16f).top().left();
        panel.defaults().minWidth(0f).growX().padBottom(10f);
        shiftsLabel.setFontScale(20f / shiftsLabel.getStyle().font.getCapHeight());
        swapsLabel.setFontScale(shiftsLabel.getFontScaleX());
        successesLabel.setFontScale(shiftsLabel.getFontScaleX());
        successesLabel.setAlignment(Align.left);
        rulesLabel.setFontScale(11f / rulesLabel.getStyle().font.getCapHeight());
        resourceWarningLabel.setFontScale(12f / resourceWarningLabel.getStyle().font.getCapHeight());
        endLabel.setFontScale(14f / endLabel.getStyle().font.getCapHeight());
        panel.add(new Stack(rulesLabel, endLabel)).height(48f).row();
        panel.add(shiftsLabel).left().row();
        panel.add(swapsLabel).left().row();
        panel.add(successesLabel).left().padBottom(4f).row();
        panel.add(resourceWarningLabel).height(30f).row();
        for (TextButton button : new TextButton[] {rerollButton, superRerollButton, pickupButton}) {
            configurePanelButton(button);
            panel.add(button).height(34f).row();
        }
        Label instructions = new Label("Tap a token to swap.\nSwipe a row or column to shift.", rulesLabel.getStyle());
        instructions.setFontScale(rulesLabel.getFontScaleX());
        instructions.setWrap(true);
        panel.add(instructions).height(46f).padTop(6f).row();
        configurePanelButton(restartButton);
        panel.add(restartButton).height(34f).padTop(10f).row();
        return panel;
    }

    private static void configurePanelButton(TextButton button) {
        button.clearActions();
        button.setTransform(false);
        button.setScale(1f);
        button.getLabel().setFontScale(14f / button.getStyle().font.getCapHeight());
    }

    private void layoutUi(float width, float height) {
        float centerX = width * 0.65f;
        float centerY = height * 0.5f;
        float boardSize = Math.min(width * BOARD_WIDTH_RATIO, height * BOARD_HEIGHT_RATIO) * PLAY_AREA_SCALE;
        boardActor.layout(centerX, centerY, boardSize);
        float panelWidth = width * 0.29f;
        setupPanel.setBounds(width * 0.02f, 12f, panelWidth, height - 24f);
        controlPanel.setBounds(width * 0.02f, 12f, panelWidth, height - 24f);
        setupHint.setBounds(width * 0.34f, height * 0.25f, width * 0.62f, height * 0.5f);
        float boardBottom = centerY - boardSize / 2f;

        float nextPreviewSize = height * 0.12f * PLAY_AREA_SCALE;
        nextTokenPreview.setSize(nextPreviewSize, nextPreviewSize);

        float nextTokenGap = height * 0.072f * PLAY_AREA_SCALE;
        float nextTokenY = boardBottom - nextTokenGap - nextPreviewSize;
        nextTokenPreview.setPosition(centerX - nextTokenPreview.getWidth() / 2f, nextTokenY);
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
