package sk.sivak.eldritchhorror.core.view.components.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import rx.Completable;
import rx.Single;
import rx.functions.Action;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;

public class Chalkboard extends Group {
    private static final float BACKGROUND_SCALE = 0.35f;
    private static final float FONT_SCALE = 0.6f;
    private static final float BORDER_SIZE = 70;
    private static final int TEXTURE_WIDTH = 1007;
    private static final int TEXTURE_HEIGHT = 650;
    private Image background;
    private final TypingLabel textArea;
    private final Skin skin;
    private boolean visible = false;
    private PublishSubject<Object> publishSubject;

    public Chalkboard(Skin skin) {
        TypingConfig.INTERVAL_MULTIPLIERS_BY_CHAR.put('\n',0);
        this.skin = skin;
        CustomAssetManager.getTextureAsync("tutorial/chalkboard.png").subscribe(chalkboardTexture -> {
            background = new Image(chalkboardTexture);
            background.setScaling(Scaling.fit);
            background.setColor(Color.WHITE);
            background.setSize(TEXTURE_WIDTH * BACKGROUND_SCALE, TEXTURE_HEIGHT * BACKGROUND_SCALE);
            addActorAt(0, background);
        });



        Label.LabelStyle labelStyle = new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_BLACK_CHANCERY), Color.WHITE);
        textArea = new SafeTypingLabel("", labelStyle);
        textArea.getStyle().font.getData().markupEnabled = true;
        textArea.setFontScale(FONT_SCALE);
        textArea.setAlignment(Align.left, Align.center);
        textArea.setWrap(true);
        textArea.setPosition(BORDER_SIZE * BACKGROUND_SCALE, BORDER_SIZE * BACKGROUND_SCALE);

        setSize(TEXTURE_WIDTH * BACKGROUND_SCALE, TEXTURE_HEIGHT * BACKGROUND_SCALE);

        addActor(textArea);

        setOrigin(Align.center);
        setScale(0);

        ButtonUtils.addClickListener(this, () -> {
            if (!textArea.hasEnded()) {
                return;
            }
            publishSubject.onNext("XYZ");
            publishSubject.onCompleted();
        });
    }

    public Completable display(String text) {
        publishSubject = PublishSubject.create();
        if (visible) {
            restartTextAreaSafe(text);
        } else {
            visible = true;
            restartTextAreaSafe(text);
            clearActions();
            addAction(Actions.scaleTo(1f,1f,1f, Interpolation.swingOut));
        }
        return publishSubject.toCompletable();
    }

    private void restartTextAreaSafe(String text) {
        try {
            textArea.restart(text);
        } catch (Exception e) {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.EXCEPTION,
                    "textArea.restart(text) - " + e.toString(), text);
            // lets ignore this
        }


    }

    public Single<Boolean> displayWithNoYesButtons(String text) {
        textArea.setPosition(BORDER_SIZE * BACKGROUND_SCALE, BORDER_SIZE * BACKGROUND_SCALE + 50);
        textArea.setSize(getWidth() - 2*BORDER_SIZE*BACKGROUND_SCALE, getHeight() - 2*BORDER_SIZE*BACKGROUND_SCALE - 50);
        display(text).subscribe();

        Table buttonsTable = new Table();
        buttonsTable.align(Align.bottom);
        buttonsTable.setX(getWidth()/2f);
        buttonsTable.pad(BORDER_SIZE * BACKGROUND_SCALE);
        addActor(buttonsTable);

        PublishSubject<Boolean> selectedValue = PublishSubject.create();
        Action0 showButtons = () -> {
            TextButton noButton = createNiceButton("No");
            noButton.getLabel().setColor(Color.RED);
            buttonsTable.add(noButton).padRight(10).size(140, 50);
            ButtonUtils.addClickListener(noButton, () -> {
                hide(() -> {
                    resetChalkboard();
                    buttonsTable.remove();
                    selectedValue.onNext(false);
                    selectedValue.onCompleted();
                });

            });


            TextButton yesButton = createNiceButton("Yes");
            buttonsTable.add(yesButton).size(140, 50);
            yesButton.getLabel().setColor(Color.GREEN);
            ButtonUtils.addClickListener(yesButton, () -> {
                hide(() -> {
                    resetChalkboard();
                    buttonsTable.remove();
                    selectedValue.onNext(true);
                    selectedValue.onCompleted();
                });
            });
        };

        textArea.setTypingListener(new TypingAdapter() {
            @Override
            public void end() {
                showButtons.call();
            }
        });
        display(text).subscribe();
        return selectedValue.toSingle();
    }

    private void resetChalkboard() {
        textArea.setPosition(BORDER_SIZE * BACKGROUND_SCALE, BORDER_SIZE * BACKGROUND_SCALE);
        textArea.setSize(getWidth() - 2*BORDER_SIZE*BACKGROUND_SCALE, getHeight() - 2*BORDER_SIZE*BACKGROUND_SCALE);
    }

    private TextButton createNiceButton(String text) {
        TextButton niceButton = new TextButton(text, skin);
        niceButton.getLabel().setFontScale(0.5f);
        niceButton.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        return niceButton;
    }

    public void hide() {
        hide(() -> {});
    }

    public void hide(Action0 onHideAction) {
        if (!visible) {
            return;
        }
        visible = false;
        clearActions();
        addAction(Actions.sequence(
                Actions.scaleTo(0f,0f,0.5f, Interpolation.swingIn),
                Actions.run(() -> {
                    textArea.setText("");
                    onHideAction.call();
                })
        ));
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (background != null) {
            background.setSize(getWidth(), getHeight());
        }
        textArea.setSize(getWidth() - 2*BORDER_SIZE*BACKGROUND_SCALE, getHeight() - 2*BORDER_SIZE*BACKGROUND_SCALE);
    }

}
