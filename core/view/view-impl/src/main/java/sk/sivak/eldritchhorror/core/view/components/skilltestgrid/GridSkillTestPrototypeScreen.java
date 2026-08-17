package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder.buildButton;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class GridSkillTestPrototypeScreen extends ScreenAdapter {
    private static final int DEFAULT_MOVES = 4;
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
    private final GridSymbolActor nextSymbolActor;
    private final Label movesLabel;
    private final Label successesLabel;
    private final Label gainLabel;
    private final Label endLabel;
    private final TextButton restartButton;
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
        controller.startTest(moves);
        assets = new GridTestAssets();
        boardActor = new GridBoardActor(controller, assets);
        boardActor.setMoveSelectedListener(this::onMoveSelected);
        boardActor.setInteractionEnabled(false);

        Label.LabelStyle titleStyle = new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_BLACK_CHANCERY), Color.WHITE);
        Label.LabelStyle gainStyle = new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_ADLER), new Color(0x6fff6fff));

        movesLabel = new Label("moves: 0", titleStyle);
        successesLabel = new Label("successes: 0", titleStyle);
        nextSymbolActor = new GridSymbolActor(assets, SymbolType.ONE);
        gainLabel = new Label("", gainStyle);
        endLabel = new Label("", titleStyle);
        restartButton = buildButton("RESTART");

        configureLabel(movesLabel, Align.center);
        configureLabel(successesLabel, Align.center);
        configureLabel(gainLabel, Align.center);
        configureLabel(endLabel, Align.center);
        movesLabel.setFontScale(UI_LABEL_SCALE);
        successesLabel.setFontScale(UI_LABEL_SCALE);

        stage.addActor(boardActor);
        stage.addActor(nextSymbolActor);
        stage.addActor(movesLabel);
        stage.addActor(successesLabel);
        stage.addActor(gainLabel);
        stage.addActor(endLabel);
        stage.addActor(restartButton);

        addClickListener(restartButton, () -> startTest(configuredMoves));

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
        GridTestController.MatchResolution resolution = controller.resolveMatches(matches);
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
    }

    private void refreshNextTokenPreview() {
        nextSymbolActor.setSymbolType(assets, randomProvider.peekNext());
    }

    private void setNextTokenPreviewVisible(boolean visible) {
        nextSymbolActor.setVisible(visible);
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

    private void layoutUi(float width, float height) {
        float boardSize = Math.min(width * BOARD_WIDTH_RATIO, height * BOARD_HEIGHT_RATIO);
        boardActor.layout(width * 0.5f, height * 0.50f, boardSize);

        movesLabel.pack();
        successesLabel.pack();
        float nextPreviewSize = height * 0.12f;
        nextSymbolActor.setSize(nextPreviewSize, nextPreviewSize);

        float leftColumnX = width * 0.08f;
        float topY = height * 0.34f;
        float lineGap = height * 0.085f;

        movesLabel.setPosition(leftColumnX, topY);
        successesLabel.setPosition(leftColumnX, topY - lineGap);

        float restartY = topY - lineGap - height * 0.12f;
        restartButton.setPosition(leftColumnX, restartY);

        nextSymbolActor.setPosition(width * 0.5f - nextSymbolActor.getWidth() / 2f, height * 0.07f);
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
