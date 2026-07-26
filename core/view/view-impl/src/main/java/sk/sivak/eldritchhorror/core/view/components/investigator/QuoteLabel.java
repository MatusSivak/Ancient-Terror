package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class QuoteLabel extends Label{

    public QuoteLabel() {
        super("", new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), Color.WHITE));
        getStyle().font.getData().markupEnabled = true;
        setAlignment(Align.center);
        setWrap(true);
        setFontScale(0.45f);
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
