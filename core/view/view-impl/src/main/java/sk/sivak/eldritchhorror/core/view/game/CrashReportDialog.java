package sk.sivak.eldritchhorror.core.view.game;

import sk.sivak.eldritchhorror.core.view.components.SoundDialog;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** Acknowledges the failure before handing control to the existing crash handler. */
public final class CrashReportDialog extends SoundDialog {
    private final Runnable onClose;
    private boolean closed;

    public CrashReportDialog(Runnable onClose) {
        super("", style());
        this.onClose = onClose;
        setMovable(false);
        setResizable(false);
        setModal(true);
        getTitleTable().setVisible(false);
        pad(30, 32, 26, 32);
        getContentTable().defaults().width(420).padBottom(16);
        Label heading = label(get("crash.title"), 26, "E8D6AD");
        getContentTable().add(heading).padTop(8).row();
        Image rule = new Image(getTexture(PURE_WHITE_BACKGROUND));
        rule.setColor(Color.valueOf("8E7953"));
        getContentTable().add(rule).height(1).row();
        getContentTable().add(label(get("crash.message"), 19, "EEE6D5")).row();
        getContentTable().add(label(get("crash.recovery"), 17, "B8C4BE")).padBottom(22).row();
        TextButton close = new TextButton(get("crash.close"), AncientTerrorMenuStyles.button());
        close.getLabel().setFontScale(.36f);
        AncientTerrorMenuStyles.makeMomentary(close);
        AncientTerrorMenuStyles.addFocusHighlight(close);
        ButtonUtils.addClickListener(close, this::close);
        getButtonTable().add(close).width(190).height(48);
        key(Input.Keys.ESCAPE, true);
        key(Input.Keys.BACK, true);
    }

    @Override protected void result(Object result) { close(); }

    private void close() {
        if (closed) return;
        closed = true;
        hide();
        onClose.run();
    }

    private static Label label(String text, int size, String color) {
        Label label = new Label(text, new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, size), Color.valueOf(color)));
        label.setWrap(true);
        label.setAlignment(Align.center);
        return label;
    }

    private static WindowStyle style() {
        TextureRegionDrawable background = new TextureRegionDrawable(getTextureRegion(RESERVE_BACKGROUND));
        background.setMinWidth(0);
        background.setMinHeight(0);
        WindowStyle style = new WindowStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 26),
                Color.valueOf("E8D6AD"), background);
        style.stageBackground = SelectionPanelStyle.panel("00000099", "00000000");
        return style;
    }
}
