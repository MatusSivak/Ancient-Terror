package sk.sivak.eldritchhorror.core.view.components.tutorial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class SafeTypingLabel extends TypingLabel {
    public SafeTypingLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    public SafeTypingLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }

    public SafeTypingLabel(CharSequence text, Skin skin, String fontName, String colorName) {
        super(text, skin, fontName, colorName);
    }

    public SafeTypingLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public SafeTypingLabel(CharSequence text, Skin skin) {
        super(text, skin);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        try {
            super.draw(batch, parentAlpha);
        } catch (Exception e) {
            // Fuck that
        }
    }
}
