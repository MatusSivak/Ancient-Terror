package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;

public class QuoteLabel extends Label{

    public QuoteLabel() {
        super("", new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4), new Color(0.86f, 0.84f, 0.77f, 1f)));
        getStyle().font.getData().markupEnabled = true;
        setAlignment(Align.center);
        setWrap(true);
        setFontScale(0.3f);
        getStyle().font.getData().markupEnabled = true;
    }

    @Override
    public void setText(CharSequence newText) {
        if (getText().toString().equals(newText.toString())) {
            return;
        }
        clearActions();
        setColor(new Color(1f,1f,1f,0f));
        addAction(Actions.alpha(1f, 0.5f));
        super.setText(newText);
    }

    public void hide() {
        clearActions();
        addAction(Actions.sequence(
                Actions.alpha(0f, 0.5f),
                Actions.run(() -> setText(""))
        ));
    }
}
