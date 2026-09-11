package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
        getColor().a = 0f;
        menuButtonClickable = false;
        MapStage.darkenWorld(0.85f);

        Dialog dialog = createMenuDialog();

        ImageTextButton restartGameButton = createNiceButton("icon/restart.png", get("menu.restartGame"));
        ImageTextButton musicOnButton = createNiceButton("icon/volume_on.png", get("menu.musicOn"));
        ImageTextButton musicOffButton = createNiceButton("icon/volume_off.png", get("menu.musicOff"));
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
        Cell<ImageTextButton> musicToggleButtonCell;
        if (NewMusicBox.getInstance().isEnabled()) {
            musicToggleButtonCell = dialog.getContentTable().add(musicOnButton);
        } else {
            musicToggleButtonCell = dialog.getContentTable().add(musicOffButton);
        }
        musicToggleButtonCell.size(280, 50);
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


        ButtonUtils.addClickListener(musicOnButton, () -> {
            musicToggleButtonCell.getActor().remove();
            musicToggleButtonCell.setActor(musicOffButton).size(280,50);
            NewMusicBox.getInstance().setEnabled(false);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("music_disabled", true);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(musicOffButton, () -> {
            musicToggleButtonCell.getActor().remove();
            musicToggleButtonCell.setActor(musicOnButton).size(280,50);
            NewMusicBox.getInstance().setEnabled(true);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("music_disabled", false);
                preferences.flush();
            });
        });

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
                new ReportBugDialog(MenuButton.this, skin).show(InfoStage.getStageSafe());
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
        niceButton.getLabel().setFontScale(0.4f);
        niceButton.setSize(280, 50);
        niceButton.getImage().setAlign(Align.left);
        niceButton.getLabel().setAlignment(Align.left);
        niceButton.getLabelCell().growX();
        niceButton.getImageCell().size(36).padLeft(14).padRight(8);
        return niceButton;
    }

    private TextButton createNiceButton(String text) {
        TextButton niceButton = new TextButton(text, createMenuButtonStyle());
        niceButton.getLabel().setFontScale(0.4f);
        niceButton.setSize(280, 50);
        return niceButton;
    }

    private TextButton.TextButtonStyle createMenuButtonStyle() {
        NinePatch normal = CustomAssetManager.createMenuButtonPatch(false);
        NinePatch pressed = CustomAssetManager.createMenuButtonPatch(true);
        normal.scale(0.5f, 0.5f);
        pressed.scale(0.5f, 0.5f);
        NinePatchDrawable up = new NinePatchDrawable(normal);
        NinePatchDrawable down = new NinePatchDrawable(pressed);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                up, down, down, CustomAssetManager.getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4));
        style.over = up;
        style.fontColor = new Color(0.91f, 0.85f, 0.69f, 1f);
        style.overFontColor = new Color(1f, 0.94f, 0.79f, 1f);
        style.downFontColor = new Color(0.82f, 0.75f, 0.59f, 1f);
        style.checkedFontColor = style.downFontColor;
        style.pressedOffsetY = -1f;
        return style;
    }
}
