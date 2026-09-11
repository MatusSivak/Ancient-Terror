package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.*;

final class TypewriterTextLayout {
    private TypewriterTextLayout() {
    }

    static List<String> splitLines(BitmapFont font, String text, Color color) {
        BitmapFont.BitmapFontData data = font.getData();
        float oldScaleX = data.scaleX;
        float oldScaleY = data.scaleY;
        boolean oldMarkupEnabled = data.markupEnabled;
        try {
            // Measure in the same coordinates as the labels, independent of shared font state.
            data.setScale(FONT_SCALE);
            data.markupEnabled = true;
            GlyphLayout layout = new GlyphLayout(font, text, color,
                    TEXT_AREA_WIDTH * TABLE_SCALE, Align.left, true);
            Map<Float, StringBuilder> lines = new LinkedHashMap<>();
            for (int i = 0; i < layout.runs.size; i++) {
                GlyphLayout.GlyphRun run = layout.runs.get(i);
                StringBuilder line = lines.get(run.y);
                if (line == null) {
                    line = new StringBuilder();
                    lines.put(run.y, line);
                }
                line.append("[#").append(run.color).append("]");
                int glyphCount = run.glyphs.size;
                if (i + 1 == layout.runs.size || layout.runs.get(i + 1).y != run.y) {
                    // The old GlyphLayout retains wrap spaces. They can push a reconstructed
                    // line over its width when the closing color tag uses their full advance.
                    while (glyphCount > 0 && data.isWhitespace((char) run.glyphs.get(glyphCount - 1).id)) {
                        glyphCount--;
                    }
                }
                for (int j = 0; j < glyphCount; j++) {
                    BitmapFont.Glyph glyph = run.glyphs.get(j);
                    // GlyphLayout has already decoded escaped markup brackets.
                    if (glyph.id == '[') line.append('[');
                    line.append((char) glyph.id);
                }
                line.append("[]");
            }
            List<String> result = new LinkedList<>();
            for (StringBuilder line : lines.values()) result.add(line.toString());
            if (result.isEmpty()) result.add("");
            return result;
        } finally {
            data.setScale(oldScaleX, oldScaleY);
            data.markupEnabled = oldMarkupEnabled;
        }
    }

    static void configureLine(Label label) {
        // Each table row reserves exactly one line. Wrapping again can overflow that row.
        label.setWrap(false);
        label.setFontScale(FONT_SCALE);
    }
}
