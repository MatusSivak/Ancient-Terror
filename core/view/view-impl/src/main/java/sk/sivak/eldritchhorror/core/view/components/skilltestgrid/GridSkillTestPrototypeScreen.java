package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
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
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.ArrayList;
import java.util.Comparator;
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
    private static final String SOUND_VARIANTS_DIR = "sounds";
    private static final String SOUND_VARIANT_SUFFIX = ".wav";
    private static final String TOKEN_EXPLOSION_SOUND_PREFIX = "token_explosion_";
    private static final String GOOD_TOKEN_IMPLOSION_SOUND_PREFIX = "good_token_implosion_pickup_";
    private static final String CHESS_PIECE_MOVE_SOUND_PREFIX = "chess_piece_move_";

    private final Stage stage;
    private final RandomSymbolProvider randomProvider;
    private final GridTestController controller;
    private final GridBoardActor boardActor;
    private final GridTestSoundHooks soundHooks;
    private final Random random;
    private final GridTestAssets assets;
    private final NextTokenPreview nextTokenPreview;
    private final Label movesLabel;
    private final Label successesLabel;
    private final Label gainLabel;
    private final Label endLabel;
    private final Label focusLabel;
    private final TextButton restartButton;
    private final TextButton focusButton;
    private final Label superRerollLabel;
    private final TextButton superRerollButton;
    private final Label swapLabel;
    private final TextButton swapButton;
    private final SelectBox<TestMode> modeSelectBox;
    private final CheckBox momentumCheckBox;
    private final Table controlPanel;
    private final GridTestModePreferences modePreferences;
    private final GridTestMomentumPreferences momentumPreferences;
    private final FocusReroller focusReroller;
    private final SymbolReroller superReroller;
    private List<Sound> chessPieceMoveSounds;
    private List<Sound> tokenExplosionSounds;
    private List<Sound> goodTokenImplosionSounds;
    private int configuredMoves;
    private GridTestResult result;

    public GridSkillTestPrototypeScreen(int moves) {
        this(moves, new Random(), GridTestSoundHooks.NO_OP);
    }

    public GridSkillTestPrototypeScreen(int moves, Random random, GridTestSoundHooks soundHooks) {
        this.configuredMoves = moves;
        this.soundHooks = soundHooks == null ? GridTestSoundHooks.NO_OP : soundHooks;
        this.random = random == null ? new Random() : random;
        stage = new Stage(new FitViewport(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT));
        randomProvider = new RandomSymbolProvider(this.random);
        controller = new GridTestController(new GridBoard(randomProvider));
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        modePreferences = new GridTestModePreferences(preferences);
        momentumPreferences = new GridTestMomentumPreferences(preferences);
        controller.setSelectedMode(modePreferences.load());
        controller.setConfiguredMomentum(momentumPreferences.load());
        focusReroller = new FocusReroller(this.random);
        superReroller = new SymbolReroller(this.random);
        controller.startTest(moves);
        assets = new GridTestAssets();
        boardActor = new GridBoardActor(controller, assets);
        boardActor.setLayoutScale(PLAY_AREA_SCALE);
        boardActor.setMoveSelectedListener(this::onMoveSelected);
        boardActor.setInteractionEnabled(false);

        Label.LabelStyle titleStyle = new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_BLACK_CHANCERY), Color.WHITE);
        Label.LabelStyle gainStyle = new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_ADLER), new Color(0x6fff6fff));

        movesLabel = new Label("moves: 0", titleStyle);
        successesLabel = new Label("successes: 0", titleStyle);
        nextTokenPreview = new NextTokenPreview(assets);
        gainLabel = new Label("", gainStyle);
        endLabel = new Label("", titleStyle);
        focusLabel = new Label("", titleStyle);
        restartButton = buildButton("RESTART");
        focusButton = buildButton("FOCUS");
        superRerollLabel = new Label("", titleStyle);
        superRerollButton = buildButton("SUPER REROLL");
        swapLabel = new Label("", titleStyle);
        swapButton = buildButton("SWAP");
        modeSelectBox = new SelectBox<>(CustomAssetManager.getSkin());
        modeSelectBox.setItems(TestMode.BLESSED, TestMode.NORMAL, TestMode.CURSED);
        modeSelectBox.setSelected(controller.getSelectedMode());
        modeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TestMode selectedMode = modeSelectBox.getSelected();
                controller.setSelectedMode(selectedMode);
                modePreferences.save(selectedMode);
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
            }
        });

        configureLabel(movesLabel, Align.center);
        configureLabel(successesLabel, Align.center);
        configureLabel(gainLabel, Align.center);
        configureLabel(endLabel, Align.center);
        configureLabel(focusLabel, Align.center);
        configureLabel(superRerollLabel, Align.center);
        configureLabel(swapLabel, Align.center);
        movesLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        successesLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        focusLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        superRerollLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        swapLabel.setFontScale(UI_LABEL_SCALE * PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        restartButton.setTransform(true);
        restartButton.setOrigin(0f, 0f);
        restartButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        focusButton.setTransform(true);
        focusButton.setOrigin(0f, 0f);
        focusButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        superRerollButton.setTransform(true);
        superRerollButton.setOrigin(0f, 0f);
        superRerollButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);
        swapButton.setTransform(true);
        swapButton.setOrigin(0f, 0f);
        swapButton.setScale(PLAY_AREA_SCALE * LEFT_HUD_SCALE);

        stage.addActor(boardActor);
        stage.addActor(nextTokenPreview);
        stage.addActor(gainLabel);
        stage.addActor(endLabel);
        
        // Build control panel as a responsive table
        controlPanel = buildControlPanel();
        stage.addActor(controlPanel);

        addClickListener(restartButton, () -> startTest(configuredMoves));
        addClickListener(focusButton, this::onFocusPressed);
        addClickListener(superRerollButton, this::onSuperRerollPressed);
        addClickListener(swapButton, this::onSwapPressed);

        updateCounters();
        setNextTokenPreviewVisible(false);
        layoutUi(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT);
        setInputEnabled(true);
        startResolutionLoop(false);
    }

    public void startTest(int moves) {
        configuredMoves = moves;
        result = null;
        endLabel.setText("");
        controller.startTest(moves);
        boardActor.resetAnimations();
        boardActor.syncBoardToActors();
        nextTokenPreview.clearNextToken();
        setNextTokenPreviewVisible(false);
        boardActor.setInteractionEnabled(false);
        restartButton.setDisabled(false);
        updateCounters();
        startResolutionLoop(false);
    }

    public void setRandomSeed(long seed) {
        randomProvider.setSeed(seed);
    }

    public void setBoard(SymbolType... symbols) {
        controller.setDebugBoard(symbols);
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
            return;
        }
        boardActor.setInteractionEnabled(false);
        GridShiftOutcome shiftOutcome = controller.applyMove(move);
        nextTokenPreview.clearNextToken();
        setNextTokenPreviewVisible(false);
        updateCounters();
        soundHooks.onShift();
        playTokenMoveSound();
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
        MatchResolution resolution = controller.resolveMatches(matches);
        setNextTokenPreviewVisible(false);
        controller.setState(GridTestState.MATCH_ANIMATION);
        boardActor.setInteractionEnabled(false);
        int successesGained = resolution.getSuccessesGained();
        if (successesGained > 0) {
            pulseSuccessCounter(successesGained);
        }
        soundHooks.onMatch(successesGained, cascade);
        playMatchSoundsIfNeeded(matches);
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
        movesLabel.setText("moves: " + controller.getMovesRemaining());
        successesLabel.setText("successes: " + controller.getSuccesses());
        focusLabel.setText("focus: " + controller.getFocusRemaining());
        superRerollLabel.setText("super reroll x" + controller.getSuperRerollsRemaining());
        swapLabel.setText("swap: " + controller.getSwapRemaining());
        updateFocusButtonState();
        updateSuperRerollButtonState();
        updateSwapButtonState();
    }

    private void onFocusPressed() {
        if (!canUseFocus()) {
            return;
        }
        SymbolType currentNext = randomProvider.peekNext();
        if (controller.useFocus()) {
            SymbolType rerolled = focusReroller.reroll(currentNext);
            randomProvider.overrideNext(rerolled);
            refreshNextTokenPreview();
            updateCounters();
            updateFocusButtonState();
        }
    }

    private boolean canUseFocus() {
        if (controller.getFocusRemaining() <= 0) {
            return false;
        }
        if (controller.getState() != GridTestState.WAITING_FOR_INPUT) {
            return false;
        }
        SymbolType currentNext = randomProvider.peekNext();
        return currentNext != null;
    }

    private void updateFocusButtonState() {
        boolean canUse = canUseFocus();
        focusButton.setDisabled(!canUse);
        // Keep button text simple - count is shown in the "focus: N" label
        focusButton.setText("FOCUS");
    }

    private void onSuperRerollPressed() {
        if (!controller.canUseSuperReroll()) {
            return;
        }

        boardActor.setInteractionEnabled(false);
        Map<GridPosition, SymbolType> rerolledCells = controller.performSuperReroll(superReroller);
        if (rerolledCells.isEmpty()) {
            onBoardStable();
            return;
        }

        controller.setState(GridTestState.MATCH_ANIMATION);
        updateCounters();
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

    private void onSwapPressed() {
        if (!canUseSwap()) {
            return;
        }
        Gdx.app.log("SWAP", "Swap button clicked");
        controller.setState(GridTestState.SWAP_SELECTING);
        boardActor.enterSwapSelectionMode(this::onSwapComplete);
        updateCounters();
    }

    private boolean canUseSwap() {
        if (controller.getSwapRemaining() <= 0) {
            return false;
        }
        if (controller.getState() != GridTestState.WAITING_FOR_INPUT) {
            return false;
        }
        return true;
    }

    private void updateSwapButtonState() {
        boolean canUse = canUseSwap();
        swapButton.setDisabled(!canUse);
        // Keep button text simple - count is shown in the "swap: N" label
        swapButton.setText("SWAP");
    }

    public void onSwapComplete(GridPosition pos1, GridPosition pos2) {
        if (pos1 != null && pos2 != null) {
            Gdx.app.log("SWAP", "onSwapComplete called with pos1=(" + pos1.getRow() + "," + pos1.getColumn() + ") pos2=(" + pos2.getRow() + "," + pos2.getColumn() + ")");
            boardActor.setInteractionEnabled(false);
            controller.useSwap();
            MatchResolution resolution = controller.performSwap(pos1, pos2);
            Gdx.app.log("SWAP", "performSwap completed, matches found: " + resolution.getMatches().size());
            
            boardActor.animateSwap(pos1, pos2, () -> {
                Gdx.app.log("SWAP", "animateSwap animation complete");
                updateCounters();
                
                if (resolution.getSuccessesGained() > 0) {
                    pulseSuccessCounter(resolution.getSuccessesGained());
                }
                
                soundHooks.onMatch(resolution.getSuccessesGained(), false);
                playMatchSoundsIfNeeded(resolution.getMatches());
                
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
            updateCounters();
        }
    }

    private void refreshNextTokenPreview() {
        nextTokenPreview.setNextToken(randomProvider.peekNext());
    }

    private void setNextTokenPreviewVisible(boolean visible) {
        nextTokenPreview.setVisible(visible);
    }

    private void playMatchSoundsIfNeeded(List<GridMatch> matches) {
        boolean playedTokenExplosion = false;
        boolean playedGoodTokenImplosion = false;
        for (GridMatch match : matches) {
            SymbolType symbol = match.getSymbol();
            if (!playedTokenExplosion && (symbol == SymbolType.ONE || symbol == SymbolType.TWO || symbol == SymbolType.THREE || symbol == SymbolType.FOUR)) {
                Sound sound = getRandomTokenExplosionSound();
                if (sound != null) {
                    sound.play();
                }
                playedTokenExplosion = true;
            }
            if (!playedGoodTokenImplosion && (symbol == SymbolType.FIVE || symbol == SymbolType.SIX)) {
                Sound sound = getRandomGoodTokenImplosionSound();
                if (sound != null) {
                    sound.play();
                }
                playedGoodTokenImplosion = true;
            }
            if (playedTokenExplosion && playedGoodTokenImplosion) {
                return;
            }
        }
    }

    private Sound getRandomTokenExplosionSound() {
        List<Sound> sounds = getOrLoadSounds(TOKEN_EXPLOSION_SOUND_PREFIX, 0);
        if (sounds.isEmpty()) {
            return null;
        }
        return sounds.get(random.nextInt(sounds.size()));
    }

    private Sound getRandomGoodTokenImplosionSound() {
        List<Sound> sounds = getOrLoadSounds(GOOD_TOKEN_IMPLOSION_SOUND_PREFIX, 1);
        if (sounds.isEmpty()) {
            return null;
        }
        return sounds.get(random.nextInt(sounds.size()));
    }

    private void playTokenMoveSound() {
        Sound sound = getRandomChessPieceMoveSound();
        if (sound != null) {
            sound.play();
        }
    }

    private Sound getRandomChessPieceMoveSound() {
        List<Sound> sounds = getOrLoadSounds(CHESS_PIECE_MOVE_SOUND_PREFIX, 2);
        if (sounds.isEmpty()) {
            return null;
        }
        return sounds.get(random.nextInt(sounds.size()));
    }

    private List<Sound> getOrLoadSounds(String prefix, int soundType) {
        List<Sound> sounds = null;
        if (soundType == 0) {
            sounds = tokenExplosionSounds;
        } else if (soundType == 1) {
            sounds = goodTokenImplosionSounds;
        } else if (soundType == 2) {
            sounds = chessPieceMoveSounds;
        }
         
        if (sounds != null) {
            return sounds;
        }

        sounds = new ArrayList<>();
        if (Gdx.audio != null && Gdx.files != null) {
            com.badlogic.gdx.files.FileHandle soundsDir = Gdx.files.internal(SOUND_VARIANTS_DIR);
            if (soundsDir.exists() && soundsDir.isDirectory()) {
                com.badlogic.gdx.files.FileHandle[] files = soundsDir.list((dir, name) ->
                        name.startsWith(prefix) && name.endsWith(SOUND_VARIANT_SUFFIX));
                java.util.Arrays.sort(files, Comparator.comparing(com.badlogic.gdx.files.FileHandle::name));
                for (com.badlogic.gdx.files.FileHandle file : files) {
                    sounds.add(Gdx.audio.newSound(file));
                }
            }
        }

        if (soundType == 0) {
            tokenExplosionSounds = sounds;
        } else if (soundType == 1) {
            goodTokenImplosionSounds = sounds;
        } else if (soundType == 2) {
            chessPieceMoveSounds = sounds;
        }
        return sounds;
    }

    private Table buildControlPanel() {
        Table panel = new Table();
        panel.pad(8f);
        
        float controlScale = PLAY_AREA_SCALE * LEFT_HUD_SCALE;
        float panelWidth = ViewProperties.VIEWPORT_WIDTH * 0.25f * controlScale;
        float buttonHeight = ViewProperties.VIEWPORT_HEIGHT * 0.06f * controlScale;
        float statsFontScale = UI_LABEL_SCALE * controlScale;
        
        // Stats block (moves, successes, focus, swap)
        Table statsTable = new Table();
        statsTable.align(Align.left);
        
        movesLabel.setFontScale(statsFontScale);
        successesLabel.setFontScale(statsFontScale);
        focusLabel.setFontScale(statsFontScale);
        superRerollLabel.setFontScale(statsFontScale);
        swapLabel.setFontScale(statsFontScale);
        
        statsTable.add(movesLabel).left().padBottom(2f).row();
        statsTable.add(successesLabel).left().padBottom(2f).row();
        statsTable.add(focusLabel).left().padBottom(2f).row();
        statsTable.add(superRerollLabel).left().padBottom(2f).row();
        statsTable.add(swapLabel).left().row();
        
        panel.add(statsTable).left().padBottom(12f).row();
        
        // Restart button
        restartButton.setTransform(true);
        restartButton.setOrigin(0f, 0f);
        restartButton.setScale(controlScale);
        panel.add(restartButton).width(panelWidth).height(buttonHeight * 1.2f).padBottom(8f).center().row();
        
        // Mode select box
        modeSelectBox.setSize(panelWidth / controlScale, buttonHeight / controlScale);
        panel.add(modeSelectBox).width(panelWidth).height(buttonHeight).padBottom(8f).center().row();

        // Momentum applies to the next started or restarted test.
        momentumCheckBox.getLabel().setFontScale(statsFontScale);
        panel.add(momentumCheckBox).left().padBottom(8f).row();
        
        // Focus button
        focusButton.setTransform(true);
        focusButton.setOrigin(0f, 0f);
        focusButton.setScale(controlScale);
        panel.add(focusButton).width(panelWidth).height(buttonHeight).padBottom(8f).center().row();

        superRerollButton.setTransform(true);
        superRerollButton.setOrigin(0f, 0f);
        superRerollButton.setScale(controlScale);
        panel.add(superRerollButton).width(panelWidth).height(buttonHeight).padBottom(8f).center().row();
        
        // Swap button
        swapButton.setTransform(true);
        swapButton.setOrigin(0f, 0f);
        swapButton.setScale(controlScale);
        panel.add(swapButton).width(panelWidth).height(buttonHeight).padBottom(20f).center().row();
        
        // Position the panel
        panel.setSize(panelWidth + 16f, ViewProperties.VIEWPORT_HEIGHT * 0.58f);
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
        setInputEnabled(false);
    }

    @Override
    public void dispose() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
        if (tokenExplosionSounds != null) {
            for (Sound tokenExplosionSound : tokenExplosionSounds) {
                tokenExplosionSound.dispose();
            }
            tokenExplosionSounds = null;
        }
        if (goodTokenImplosionSounds != null) {
            for (Sound goodTokenImplosionSound : goodTokenImplosionSounds) {
                goodTokenImplosionSound.dispose();
            }
            goodTokenImplosionSounds = null;
        }
        if (chessPieceMoveSounds != null) {
            for (Sound chessPieceMoveSound : chessPieceMoveSounds) {
                chessPieceMoveSound.dispose();
            }
            chessPieceMoveSounds = null;
        }
        stage.dispose();
    }
}
