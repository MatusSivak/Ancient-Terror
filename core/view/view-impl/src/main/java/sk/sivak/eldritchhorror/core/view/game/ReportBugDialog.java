package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class ReportBugDialog extends Dialog {

    private final MenuButton menuButton;
    private final TextArea textArea;
    private final TextButton sendButton;
    private final TextButton closeButton;

    public ReportBugDialog(MenuButton menuButton, Skin skin) {
        super(get("reportBug.title"), skin);
        this.menuButton = menuButton;

        textArea = new TextArea(get("reportBug.placeholder"), skin);
        sendButton = createNiceButton(get("reportBug.send"), skin);
        closeButton = createNiceButton(get("reportBug.close"), skin);


        getContentTable().add(textArea).pad(5).size(ViewProperties.VIEWPORT_WIDTH * 0.5f, ViewProperties.VIEWPORT_HEIGHT * 0.5f);
        getButtonTable().add(sendButton).pad(5);
        getButtonTable().add(closeButton).pad(5);
        pack();

        ButtonUtils.addClickListener(closeButton, () -> {
            hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButton.menuButtonClickable = true;
                menuButton.getColor().a = 1f;
            }));
        });

        ButtonUtils.addClickListener(sendButton, this::reportBug);
    }

    private TextButton createNiceButton(String text, Skin skin) {
        TextButton niceButton = new TextButton(text, skin);
        niceButton.getLabel().setFontScale(0.5f);
        niceButton.setSize(280, 50);
        niceButton.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        return niceButton;
    }

    private void reportBug() {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.EXCEPTION, "BugReport: " + textArea.getText());
        hide(Actions.run(() -> {
            MapStage.brightenWorld();
            menuButton.menuButtonClickable = true;
            menuButton.getColor().a = 1f;
        }));
    }
}
