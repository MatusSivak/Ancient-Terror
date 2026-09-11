package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.*;

public class TypewriterTextLayoutTest {
    private static final String AMAZON = "You've heard rumors that a particular secret of "
            + "the ancients is visible from the top of a hill. You "
            + "find that climbing the hill side is not an easy task.";

    @Test
    public void amazonLinesFitAndRemainOnOneBaselineWhileTypingAndSkipping() {
        BitmapFont font = font();
        List<String> lines = TypewriterTextLayout.splitLines(font, AMAZON, Color.BLACK);
        assertTrue(lines.size() > 1);
        StringBuilder visible = new StringBuilder();
        for (String line : lines) {
            font.getData().setScale(FONT_SCALE);
            GlyphLayout measured = new GlyphLayout(font, line);
            assertTrue("Line exceeds paper width: " + line, measured.width <= TEXT_AREA_WIDTH * TABLE_SCALE + 0.01f);
            for (GlyphLayout.GlyphRun run : measured.runs) {
                for (BitmapFont.Glyph glyph : run.glyphs) visible.append((char) glyph.id);
            }
            visible.append(' ');
            font.getData().setScale(1f);
            for (boolean skip : new boolean[]{false, true}) {
                TypingLabel label = new TypingLabel(line, new Label.LabelStyle(font, Color.WHITE));
                TypewriterTextLayout.configureLine(label);
                label.setSize(TEXT_AREA_WIDTH * TABLE_SCALE, TEXT_LINE_HEIGHT);
                if (skip) label.skipToTheEnd();
                for (int frame = 0; frame < 1000 && !label.hasEnded(); frame++) {
                    label.act(0.05f);
                    label.validate();
                    assertOneBaseline(label.getGlyphLayout());
                }
                assertTrue(label.hasEnded());
            }
        }
        assertEquals(AMAZON, visible.toString().trim());
    }

    @Test
    public void wrappingDoesNotDependOnPreviousSharedFontScale() {
        BitmapFont font = font();
        List<String> expected = TypewriterTextLayout.splitLines(font, AMAZON, Color.BLACK);
        font.getData().setScale(0.3f, 0.6f);
        font.getData().markupEnabled = false;
        assertEquals(expected, TypewriterTextLayout.splitLines(font, AMAZON, Color.BLACK));
        assertEquals(0.3f, font.getScaleX(), 0.00001f);
        assertEquals(0.6f, font.getScaleY(), 0.00001f);
        assertFalse(font.getData().markupEnabled);
    }

    @Test
    public void prewrappedRowsNeverWrapAgainEvenAtNarrowWidths() {
        BitmapFont font = font();
        Label label = new Label("A line that has already been wrapped", new Label.LabelStyle(font, Color.WHITE));
        TypewriterTextLayout.configureLine(label);
        label.setSize(100f, TEXT_LINE_HEIGHT);
        label.validate();
        assertOneBaseline(label.getGlyphLayout());
        TypingLabel typer = new TypingLabel(label.getText(), label.getStyle());
        TypewriterTextLayout.configureLine(typer);
        typer.setSize(100f, TEXT_LINE_HEIGHT);
        typer.skipToTheEnd();
        typer.act(1f);
        typer.validate();
        assertOneBaseline(typer.getGlyphLayout());
    }

    @Test
    public void preservesMarkupLiteralBracketsAndEmptyInput() {
        BitmapFont font = font();
        List<String> lines = TypewriterTextLayout.splitLines(font, "Black [#ff0000]red[] [[note]", Color.BLACK);
        assertEquals(1, lines.size());
        GlyphLayout layout = new GlyphLayout(font, lines.get(0));
        StringBuilder text = new StringBuilder();
        boolean red = false;
        for (GlyphLayout.GlyphRun run : layout.runs) {
            red |= run.color.equals(Color.RED);
            for (BitmapFont.Glyph glyph : run.glyphs) text.append((char) glyph.id);
        }
        assertEquals("Black red [note]", text.toString());
        assertTrue(red);
        assertEquals(1, TypewriterTextLayout.splitLines(font, "", Color.BLACK).size());
    }

    private static void assertOneBaseline(GlyphLayout layout) {
        for (GlyphLayout.GlyphRun run : layout.runs) assertEquals(0f, run.y, 0.001f);
    }

    private static BitmapFont font() {
        File root = new File(System.getProperty("user.dir"));
        while (root != null && !new File(root, "assets/new_font/Special_Elite/hiero.fnt").isFile()) {
            root = root.getParentFile();
        }
        assertNotNull("Cannot locate the game's font asset", root);
        BitmapFont.BitmapFontData data = new BitmapFont.BitmapFontData(
                new FileHandle(new File(root, "assets/new_font/Special_Elite/hiero.fnt")), false);
        data.markupEnabled = true;
        return new BitmapFont(data, new TextureRegion(), false) {
            @Override
            protected void load(BitmapFontData fontData) {
                // Layout tests use real metrics without uploading the atlas to OpenGL.
            }
        };
    }
}
