package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Random;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class GridTestSetupPanelTest {
    private GridTestSetupPanel panel;
    private GridTestController controller;
    private TextButton confirm;
    private Files previousFiles;
    private Application previousApplication;
    private final AtomicInteger confirmations = new AtomicInteger();

    @Before
    public void setUp() {
        previousFiles = Gdx.files;
        previousApplication = Gdx.app;
        Gdx.app = (Application) Proxy.newProxyInstance(Application.class.getClassLoader(),
                new Class<?>[] {Application.class}, (proxy, method, args) -> {
                    if ("getType".equals(method.getName())) {
                        return Application.ApplicationType.Desktop;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(),
                new Class<?>[] {Files.class}, (proxy, method, args) -> {
                    throw new UnsupportedOperationException(method.getName());
                });
        BitmapFont.BitmapFontData data = new BitmapFont.BitmapFontData();
        data.capHeight = 14f;
        data.lineHeight = 20f;
        data.down = -20f;
        for (char c = 32; c < 127; c++) {
            BitmapFont.Glyph glyph = new BitmapFont.Glyph();
            glyph.id = c;
            glyph.xadvance = 8;
            glyph.width = 8;
            glyph.height = 14;
            data.setGlyph(c, glyph);
        }
        BitmapFont font = new BitmapFont(data, new TextureRegion(), false) {
            @Override
            protected void load(BitmapFontData fontData) {
                // Layout-only font: no texture upload is needed.
            }
        };
        Skin skin = new Skin();
        skin.add("small", font, BitmapFont.class);
        BaseDrawable background = new BaseDrawable();
        skin.add("default", new Label.LabelStyle(font, Color.WHITE));
        skin.add("default", new CheckBox.CheckBoxStyle(background, background, font, Color.WHITE));
        skin.add("default", new SelectBox.SelectBoxStyle(font, Color.WHITE, background,
                new ScrollPane.ScrollPaneStyle(), new List.ListStyle(font, Color.WHITE, Color.WHITE, background)));
        BaseDrawable buttonBackground = new BaseDrawable();
        buttonBackground.setMinWidth(450f);
        buttonBackground.setMinHeight(80f);
        confirm = new TextButton("CONFIRM & START",
                new TextButton.TextButtonStyle(buttonBackground, buttonBackground, null, font));
        confirm.getLabel().setFontScale(0.8f);
        controller = new GridTestController(new GridBoard(new RandomSymbolProvider(new Random(9))));
        panel = new GridTestSetupPanel(skin, confirm, GridTestParameters.randomized(new Random(1)),
                parameters -> {
                    confirmations.incrementAndGet();
                    controller.startTest(parameters);
                    controller.setState(GridTestState.WAITING_FOR_INPUT);
                    panel.setVisible(false);
                }, () -> {});
        panel.setSize(960f * 0.29f, 516f);
        panel.validate();
    }

    @After
    public void tearDown() {
        Gdx.files = previousFiles;
        Gdx.app = previousApplication;
    }

    @Test
    public void setupDoesNotPrepareBoardOrAllowPlayBeforeConfirmation() {
        assertEquals(GridTestState.INITIALIZING, controller.getState());
        assertEquals(0, confirmations.get());
        assertNull(controller.getBoard().getCell(0, 0));
        assertFalse(controller.canAcceptInput());
        GridTestParameters parameters = panel.getParameters();
        assertEquals(TestMode.NORMAL, parameters.getDifficulty());
        assertFalse(parameters.isMomentum());
        assertFalse(parameters.isBlind());
        assertEquals(GridSuccessTarget.ONE, parameters.getSuccessTarget());
        assertTrue(panel.isVisible());
    }

    @Test
    public void allEditedFieldsAreAppliedOnlyOnConfirmation() {
        select("shifts", 3);
        select("swaps", 4);
        select("rerolls", 2);
        select("lifts", 1);
        select("super-rerolls", 1);
        assertEquals(2, ((SelectBox<?>) panel.findActor("super-rerolls")).getItems().size);
        select("gaps", 3);
        select("difficulty", 2);
        select("success-target", 1);
        ((CheckBox) panel.findActor("momentum")).setChecked(true);
        ((CheckBox) panel.findActor("blind")).setChecked(true);
        assertEquals(0, confirmations.get());
        assertNull(controller.getBoard().getCell(0, 0));

        confirm.setChecked(true);

        assertEquals(1, confirmations.get());
        assertFalse(panel.isVisible());
        assertTrue(controller.canAcceptInput());
        assertEquals(4, controller.getMovesRemaining());
        assertEquals(4, controller.getSwapRemaining());
        assertEquals(2, controller.getRemainingRerolls());
        assertEquals(1, controller.getPickupsAvailable());
        assertEquals(1, controller.getSuperRerollsRemaining());
        assertEquals(3, controller.getGapCount());
        assertEquals(TestMode.CURSED, controller.getActiveMode());
        assertTrue(controller.isActiveMomentum());
        assertTrue(controller.isBlindEnabled());
        assertEquals(GridSuccessTarget.TWO, controller.getSuccessTarget());
        assertTrue(controller.findMatches().isEmpty());
    }

    @Test
    public void unlimitedConfirmationProducesScoreOnlyResult() {
        select("success-target", 2);
        confirm.setChecked(true);
        assertEquals(GridTestResult.Outcome.SCORE_ONLY, controller.finish().getOutcome());
    }

    @Test
    public void freshParametersRestoreDefaultsWithoutStartingAnotherTest() {
        select("difficulty", 2);
        ((CheckBox) panel.findActor("momentum")).setChecked(true);
        ((CheckBox) panel.findActor("blind")).setChecked(true);
        confirm.setChecked(true);
        panel.setParameters(GridTestParameters.randomized(new Random(3)));
        panel.setVisible(true);
        assertEquals(1, confirmations.get());
        assertEquals(TestMode.NORMAL, panel.getParameters().getDifficulty());
        assertFalse(panel.getParameters().isMomentum());
        assertFalse(panel.getParameters().isBlind());
        confirm.setChecked(false);
        assertEquals(2, confirmations.get());
        assertEquals(TestMode.NORMAL, controller.getActiveMode());
        assertFalse(controller.isActiveMomentum());
        assertFalse(controller.isBlindEnabled());
    }

    @Test
    public void fullFormAndConfirmFitInsideLeftPanel() {
        assertTrue(panel.getPrefHeight() <= panel.getHeight());
        assertTrue(panel.getMinWidth() <= panel.getWidth());
        for (Actor child : panel.getChildren()) {
            assertTrue(child.getX() >= 0);
            assertTrue(child.getY() >= 0);
            assertTrue(child.getRight() <= panel.getWidth());
            assertTrue(child.getTop() <= panel.getHeight());
        }
        assertTrue(confirm.getY() >= 12f);
        assertTrue(confirm.getWidth() >= 230f);
    }

    private void select(String name, int index) {
        SelectBox<?> box = panel.findActor(name);
        box.setSelectedIndex(index);
    }
}
