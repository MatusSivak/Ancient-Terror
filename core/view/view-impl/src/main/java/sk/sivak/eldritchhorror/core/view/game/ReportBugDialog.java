package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import sk.sivak.eldritchhorror.core.view.firebase.BugReportPayload;
import sk.sivak.eldritchhorror.core.view.firebase.FirebaseBugReports;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class ReportBugDialog extends Dialog {
    private final TextArea description;
    private final TextButton send;
    private final TextButton close;
    private final Label status;
    private final CheckBox includeScreenshot;
    private final byte[] screenshot;
    private byte[] save;
    private String frozenDescription;
    private byte[] frozenScreenshot;
    private final String reportId = UUID.randomUUID().toString();
    private long capturedAt;
    private final Map<String, String> metadata = new LinkedHashMap<>();
    private boolean sending;
    private boolean sent;

    public ReportBugDialog(MenuButton menuButton, Skin skin, byte[] screenshot) {
        super("", windowStyle(skin));
        this.screenshot = screenshot;
        setMovable(false);
        setResizable(false);
        pad(26, 30, 26, 30);
        getTitleTable().setVisible(false);
        float width = Math.min(560, InfoStage.getStageSafe().getWidth() - 80);
        getContentTable().defaults().width(width).left();
        // Keep the heading in the layout, safely below the decorative top frame.
        Label heading = new Label(get("reportBug.title").trim(), new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.valueOf("E8D6AD")));
        heading.setFontScale(0.68f);
        heading.setAlignment(Align.center);
        heading.setWrap(true);
        getContentTable().add(heading).padTop(4).padBottom(14).row();
        Image rule = new Image(getTexture(PURE_WHITE_BACKGROUND));
        rule.setColor(Color.valueOf("8E7953"));
        getContentTable().add(rule).height(1).padBottom(14).row();
        Label intro = label(get("reportBug.intro"), "EEE6D5");
        intro.setFontScale(0.94f);
        getContentTable().add(intro).padBottom(12).row();

        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        fieldStyle.font = getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 18);
        fieldStyle.fontColor = Color.valueOf("EEE6D5");
        fieldStyle.disabledFontColor = fieldStyle.fontColor;
        fieldStyle.messageFontColor = Color.valueOf("AAB7AF");
        fieldStyle.background = inputBackground("0D191B", "8E7953");
        fieldStyle.focusedBackground = inputBackground("152625", "DCC99F");
        fieldStyle.disabledBackground = fieldStyle.background;
        description = new TextArea("", fieldStyle);
        description.setMessageText(get("reportBug.placeholder"));
        description.setMaxLength(BugReportPayload.MAX_DESCRIPTION);
        getContentTable().add(description).height(140).padBottom(10).row();

        Label attachments = label(get("reportBug.attachments"), "B8C4BE");
        attachments.setFontScale(0.84f);
        getContentTable().add(attachments).padBottom(6).row();
        CheckBox.CheckBoxStyle checkStyle = new CheckBox.CheckBoxStyle(skin.get(CheckBox.CheckBoxStyle.class));
        checkStyle.font = getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 18);
        checkStyle.fontColor = Color.valueOf("EEE6D5");
        includeScreenshot = new CheckBox(get(screenshot == null ? "reportBug.noScreenshot" : "reportBug.screenshot"), checkStyle);
        includeScreenshot.setChecked(screenshot != null);
        includeScreenshot.setDisabled(screenshot == null);
        includeScreenshot.left();
        includeScreenshot.getLabel().setFontScale(0.9f);
        includeScreenshot.getImageCell().padRight(6);
        getContentTable().add(includeScreenshot).left().padBottom(4).row();
        status = label("", "DCC99F");
        status.setFontScale(0.84f);
        getContentTable().add(status).height(38).row();
        send = createReportButton(get("reportBug.send"));
        close = createReportButton(get("reportBug.close"));
        getButtonTable().add(close).size(170, 44).padRight(12);
        getButtonTable().add(send).size(230, 44);
        ButtonUtils.addClickListener(close, () -> {
            if (sending) return;
            Gdx.input.setOnscreenKeyboardVisible(false);
            hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButton.menuButtonClickable = true;
                menuButton.getColor().a = 1f;
            }));
        });
        ButtonUtils.addClickListener(send, this::submit);
        pack();
    }

    private void submit() {
        if (sending || sent) return;
        if (description.getText().trim().isEmpty()) { status.setText(get("reportBug.required")); return; }
        try {
            if (frozenDescription == null) {
                FileHandle file = Gdx.files.local("save.json");
                if (file.exists()) {
                    if (file.length() > BugReportPayload.MAX_SAVE_BYTES) {
                        status.setText(get("reportBug.saveTooLarge")); return;
                    }
                    save = file.readBytes();
                    metadata.put("saveModifiedAt", Long.toString(file.lastModified()));
                }
                metadata.put("platform", Gdx.app.getType().name());
                metadata.put("platformVersion", Integer.toString(Gdx.app.getVersion()));
                metadata.put("language", UiText.getLanguage());
                metadata.put("screen", Gdx.graphics.getBackBufferWidth() + "x" + Gdx.graphics.getBackBufferHeight());
                metadata.put("density", Float.toString(Gdx.graphics.getDensity()));
                metadata.put("saveStatus", save == null ? "missing" : "attached");
                metadata.put("appVersion", new JsonReader().parse(Gdx.files.internal("bug-report-firebase.json")).getString("appVersion", "unknown"));
                capturedAt = System.currentTimeMillis();
                frozenScreenshot = includeScreenshot.isChecked() ? screenshot : null;
                metadata.put("screenshotStatus", frozenScreenshot != null ? "attached" : screenshot == null ? "unavailable" : "excluded");
                frozenDescription = description.getText().trim();
            }
        } catch (Exception e) { status.setText(get("reportBug.attachError")); return; }
        sending = true;
        send.setDisabled(true);
        close.setDisabled(true);
        description.setDisabled(true);
        includeScreenshot.setDisabled(true);
        Gdx.input.setOnscreenKeyboardVisible(false);
        status.setColor(Color.valueOf("DCC99F"));
        status.setText(get("reportBug.sending"));
        new FirebaseBugReports().send(reportId, frozenDescription, save, frozenScreenshot, metadata, capturedAt,
                new FirebaseBugReports.Callback() {
                    @Override public void success() {
                        sending = false;
                        sent = true;
                        close.setDisabled(false);
                        send.setText(get("reportBug.sent"));
                        status.setColor(Color.valueOf("A8D6A0"));
                        status.setText(get("reportBug.success") + " " + reportId);
                    }
                    @Override public void failed(String reason) {
                        sending = false;
                        close.setDisabled(false);
                        send.setDisabled(false);
                        send.setText(get("reportBug.retry"));
                        status.setColor(Color.valueOf("E4AB97"));
                        status.setText(get("configuration".equals(reason) ? "reportBug.configuration" : "reportBug.failure"));
                    }
                });
    }

    private static Label label(String text, String color) {
        Label label = new Label(text, new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 18), Color.valueOf(color)));
        label.setWrap(true);
        label.setAlignment(Align.left);
        return label;
    }
    private static BaseDrawable inputBackground(String fill, String edge) {
        BaseDrawable background = (BaseDrawable) SelectionPanelStyle.panel(fill, edge);
        background.setLeftWidth(12);
        background.setRightWidth(12);
        background.setTopHeight(10);
        background.setBottomHeight(10);
        return background;
    }
    private static TextButton createReportButton(String text) {
        TextButton button = new TextButton(text, AncientTerrorMenuStyles.button());
        button.getLabel().setFontScale(0.45f);
        AncientTerrorMenuStyles.makeMomentary(button);
        return button;
    }
    private static WindowStyle windowStyle(Skin skin) {
        WindowStyle style = new WindowStyle(skin.get(WindowStyle.class));
        TextureRegionDrawable background = new TextureRegionDrawable(getTextureRegion(RESERVE_BACKGROUND));
        background.setMinWidth(0);
        background.setMinHeight(0);
        style.background = background;
        style.titleFont = getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 42);
        style.titleFontColor = Color.valueOf("E8D6AD");
        return style;
    }
}
