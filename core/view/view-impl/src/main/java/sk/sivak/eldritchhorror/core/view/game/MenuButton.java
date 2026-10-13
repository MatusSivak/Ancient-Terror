package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import sk.sivak.eldritchhorror.core.view.firebase.BugReportScreenshot;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.music.NewMusicBox;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_LIBRE_BASKERVILLE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class MenuButton extends ImageButton {

    private final Preferences preferences;
    private RestartGameAction restartGameAction;
    private boolean restartButtonDisplayed;
    boolean menuButtonClickable = true;
    private Skin skin;

    public MenuButton() {
        super(getTextureRegionDrawable("icon/menu.png"),
                getTextureRegionDrawable("icon/menu.png"),
                getTextureRegionDrawable("icon/menu.png"));
        setSize(VIEWPORT_HEIGHT * 0.1f, VIEWPORT_HEIGHT * 0.1f);
        setPosition(VIEWPORT_WIDTH - 5 - getWidth(), VIEWPORT_HEIGHT - 5 - getHeight());

        preferences = Gdx.app.getPreferences("AncientTerror.xml");
        ButtonUtils.addClickListener(this, () -> {
            openMenu();
        });
    }

    private void openMenu() {
        if (!menuButtonClickable) {
            return;
        }
        final byte[] reportScreenshot = BugReportScreenshot.capture();
        getColor().a = 0f;
        menuButtonClickable = false;
        MapStage.darkenWorld(0.85f);

        Dialog dialog = createMenuDialog();

        ImageTextButton restartGameButton = createNiceButton("icon/restart.png", get("menu.restartGame"));
        ImageTextButton vibrationOnButton = createNiceButton("icon/vibration_on.png", get("menu.vibrationOn"));
        ImageTextButton vibrationOffButton = createNiceButton("icon/vibration_off.png", get("menu.vibrationOff"));
        ImageTextButton strobeOnButton = createNiceButton("icon/migraine_on.png", get("menu.strobeOn"));
        ImageTextButton strobeOffButton = createNiceButton("icon/migraine_off.png", get("menu.strobeOff"));
        ImageTextButton saveAndExitButton = createNiceButton("icon/save.png", get("menu.saveAndExit"));
        ImageTextButton reportBugButton = createNiceButton("icon/bug.png", get("menu.reportBug"));

        TextButton closeButton = createNiceButton(get("menu.return"));

        if (restartButtonDisplayed) {
            dialog.getContentTable().add(restartGameButton).size(280, 50);
            dialog.getContentTable().row();
        }
        dialog.getContentTable().add(createMusicVolumeControl()).size(280, 66);
        dialog.getContentTable().row();

        Cell<ImageTextButton> vibrationToggleButtonCell;
        if (preferences.getBoolean("vibration_disabled", false)) {
            vibrationToggleButtonCell = dialog.getContentTable().add(vibrationOffButton);
        } else {
            vibrationToggleButtonCell = dialog.getContentTable().add(vibrationOnButton);
        }
        vibrationToggleButtonCell.size(280, 50);
        dialog.getContentTable().row();

        Cell<ImageTextButton> strobeToggleButtonCell;
        if (preferences.getBoolean("strobe_disabled", false)) {
            strobeToggleButtonCell = dialog.getContentTable().add(strobeOffButton);
        } else {
            strobeToggleButtonCell = dialog.getContentTable().add(strobeOnButton);
        }
        strobeToggleButtonCell.size(280, 50);
        dialog.getContentTable().row();

        dialog.getContentTable().add(reportBugButton).size(280,50);
        dialog.getContentTable().row();

        dialog.getContentTable().add(saveAndExitButton).size(280,50);
        dialog.getContentTable().row();
        dialog.getContentTable().add(closeButton).padTop(30).size(280,50);
        dialog.getContentTable().row();

        dialog.show(InfoStage.getStageSafe());
        // Scale the entire packed menu so its artwork, buttons and text fill the viewport height.
        float menuScale = InfoStage.getStageSafe().getHeight() / dialog.getHeight();
        dialog.setTransform(true);
        dialog.setOrigin(0f, 0f);
        dialog.setScale(menuScale);
        dialog.setKeepWithinStage(false);
        dialog.setPosition((InfoStage.getStageSafe().getWidth() - dialog.getWidth() * menuScale) / 2f, 0f);


        ButtonUtils.addClickListener(vibrationOnButton, () -> {
            vibrationToggleButtonCell.getActor().remove();
            vibrationToggleButtonCell.setActor(vibrationOffButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("vibration_disabled", true);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(vibrationOffButton, () -> {
            vibrationToggleButtonCell.getActor().remove();
            vibrationToggleButtonCell.setActor(vibrationOnButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("vibration_disabled", false);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(strobeOnButton, () -> {
            strobeToggleButtonCell.getActor().remove();
            strobeToggleButtonCell.setActor(strobeOffButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("strobe_disabled", true);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(strobeOffButton, () -> {
            strobeToggleButtonCell.getActor().remove();
            strobeToggleButtonCell.setActor(strobeOnButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("strobe_disabled", false);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(restartGameButton, () -> {
            if (restartGameAction == null) {
                return;
            }
            dialog.hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButtonClickable = true;
                getColor().a = 1f;
                restartGameAction.restartGame();
            }));
        });

        ButtonUtils.addClickListener(closeButton, () -> {
            dialog.hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButtonClickable = true;
                getColor().a = 1f;
            }));
        });

        ButtonUtils.addClickListener(reportBugButton, () -> {
            dialog.hide(Actions.run(() -> {
                new ReportBugDialog(MenuButton.this, skin, reportScreenshot).show(InfoStage.getStageSafe());
            }));
        });

        ButtonUtils.addClickListener(saveAndExitButton, () -> {
            dialog.hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButtonClickable = true;
                getColor().a = 1f;
                Gdx.app.exit();
            }));
        });
    }

    private Table createMusicVolumeControl() {
        NewMusicBox music = NewMusicBox.getInstance();
        TextButton.TextButtonStyle buttonStyle = createMenuButtonStyle();
        Label label = new Label(get("menu.musicVolume"),
                new Label.LabelStyle(buttonStyle.font, buttonStyle.fontColor));
        Label value = new Label(Math.round(music.getVolume() * 100f) + "%", label.getStyle());
        label.setFontScale(0.32f);
        value.setFontScale(0.30f);
        value.setAlignment(Align.right);
        Image icon = new Image(getTextureRegionDrawable(music.isEnabled()
                ? "icon/volume_on.png" : "icon/volume_off.png"));
        icon.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        Slider slider = new Slider(0f, 100f, 1f, false, AncientTerrorMenuStyles.volumeSlider());
        slider.setValue(Math.round(music.getVolume() * 100f));
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                music.setVolume(slider.getValue() / 100f);
                value.setText(Math.round(slider.getValue()) + "%");
                icon.setDrawable(getTextureRegionDrawable(slider.getValue() > 0f
                        ? "icon/volume_on.png" : "icon/volume_off.png"));
            }
        });
        Table control = new Table();
        control.setBackground(buttonStyle.up);
        control.pad(10f, 28f, 10f, 24f);
        control.add(icon).size(36f).padRight(8f);
        Table volume = new Table();
        volume.add(label).expandX().left();
        volume.add(value).width(40f).right().row();
        volume.add(slider).colspan(2).growX().height(24f).padTop(2f);
        control.add(volume).growX();
        return control;
    }

    private Dialog createMenuDialog() {
        NinePatch background = CustomAssetManager.createMenuDialogPatch();
        // Keep the fixed header and corners proportional to the 960 x 540 game UI.
        background.scale(0.5f, 0.5f);
        Window.WindowStyle style = new Window.WindowStyle(skin.get(Window.WindowStyle.class));
        style.background = new NinePatchDrawable(background);
        style.titleFont = CustomAssetManager.getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4);
        style.titleFontColor = new Color(0.91f, 0.85f, 0.69f, 1f);
        Dialog dialog = new Dialog(get("menu.title"), style);
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getTitleLabel().setFontScale(0.5f);
        return dialog;
    }

    public void setRestartGameAction(RestartGameAction restartGameAction) {
        this.restartGameAction = restartGameAction;
    }

    public void displayRestartButton() {
        this.restartButtonDisplayed = true;
    }

    public void setSkinProperty(Skin skin) {
        this.skin = skin;
    }

    private ImageTextButton createNiceButton(String icon, String text) {
        return createNiceButton(CustomAssetManager.getTextureRegionDrawable(icon), text);
    }

    private ImageTextButton createNiceButton(TextureRegionDrawable textureRegionDrawable, String text) {
        TextButton.TextButtonStyle textButtonStyle = createMenuButtonStyle();
        ImageTextButton.ImageTextButtonStyle imageTextButtonStyle = new ImageTextButton.ImageTextButtonStyle(textButtonStyle);
        imageTextButtonStyle.imageUp = textureRegionDrawable;
        ImageTextButton niceButton = new ImageTextButton(text, imageTextButtonStyle);
        AncientTerrorMenuStyles.makeMomentary(niceButton);
        niceButton.getLabel().setFontScale(0.4f);
        AncientTerrorMenuStyles.addFocusHighlight(niceButton);
        niceButton.setSize(280, 50);
        niceButton.getImage().setAlign(Align.left);
        niceButton.getLabel().setAlignment(Align.left);
        niceButton.getLabelCell().growX();
        niceButton.getImageCell().size(36).padLeft(14).padRight(8);
        return niceButton;
    }

    private TextButton createNiceButton(String text) {
        TextButton niceButton = new TextButton(text, createMenuButtonStyle());
        AncientTerrorMenuStyles.makeMomentary(niceButton);
        niceButton.getLabel().setFontScale(0.4f);
        AncientTerrorMenuStyles.addFocusHighlight(niceButton);
        niceButton.setSize(280, 50);
        return niceButton;
    }

    private TextButton.TextButtonStyle createMenuButtonStyle() {
        return AncientTerrorMenuStyles.button();
    }
}
