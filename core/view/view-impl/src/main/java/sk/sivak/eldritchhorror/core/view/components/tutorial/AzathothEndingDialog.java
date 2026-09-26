package sk.sivak.eldritchhorror.core.view.components.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import rx.Completable;
import rx.subjects.AsyncSubject;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** The final Azathoth sheet, presented separately from tutorial chalkboards. */
public final class AzathothEndingDialog extends Group {
    public static final String TEXT_KEY = "ancientOne.azathoth.endGame";
    private static final Color INK = Color.valueOf("30251F");
    private static final Color RED_INK = Color.valueOf("792C2D");
    private static final Color BRONZE = Color.valueOf("88704D");
    private static final float CARD_WIDTH = 780;
    private static final float CARD_HEIGHT = 480;
    private Sound openingSound;
    private long openingSoundId = -1;
    private final AsyncSubject<Void> dismissed = AsyncSubject.create();

    public AzathothEndingDialog() {
        setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        setTouchable(Touchable.enabled);

        Image dimmer = solid(Color.valueOf("09080DDD"));
        dimmer.setSize(getWidth(), getHeight());
        addActor(dimmer);

        Group card = new Group();
        card.setBounds((getWidth() - CARD_WIDTH) / 2, (getHeight() - CARD_HEIGHT) / 2,
                CARD_WIDTH, CARD_HEIGHT);
        addActor(card);
        Image background = new Image(CustomAssetManager.getTexture(ANCIENT_ONE_DIALOG_BACKGROUND));
        background.setSize(CARD_WIDTH, CARD_HEIGHT);
        card.addActor(background);

        label(card, "AZATHOTH", NEW_FONT_SPECIAL_ELITE, 32, INK, 40, 408, 700, 52, Align.center);
        label(card, get("ancientOne.azathoth.ending.subtitle"), NEW_FONT_SPECIAL_ELITE, 14, RED_INK,
                40, 393, 700, 22, Align.center);
        line(card, 40, 382, 700);

        Image frame = solid(BRONZE);
        frame.setBounds(39, 125, 344, 232);
        card.addActor(frame);
        Image art = new Image(CustomAssetManager.getTexture("ancient_one/azathoth-ending-art.png"));
        art.setScaling(Scaling.fit);
        art.setBounds(41, 127, 340, 228);
        card.addActor(art);

        label(card, get("ancientOne.azathoth.ending.title"), NEW_FONT_SPECIAL_ELITE, 23, RED_INK,
                408, 298, 332, 74, Align.topLeft);
        Label story = label(card, get(TEXT_KEY), NEW_FONT_SPECIAL_ELITE, 18, INK,
                408, 91, 332, 224, Align.topLeft);
        // Keep the localized story within its panel even if font metrics change.
        while (story.getPrefHeight() > story.getHeight() && story.getFontScaleY() > 0.8f) {
            story.setFontScale(story.getFontScaleY() - 0.025f);
        }

        line(card, 40, 73, 700);
        label(card, get("ancientOne.azathoth.ending.defeat"), NEW_FONT_SPECIAL_ELITE, 18, RED_INK,
                80, 24, 395, 40, Align.left);

        TextureRegionDrawable white = getTextureRegionDrawable(PURE_WHITE_BACKGROUND);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 18);
        style.fontColor = Color.valueOf("F4E5CA");
        style.up = white.tint(RED_INK);
        style.over = white.tint(Color.valueOf("953B3C"));
        style.down = white.tint(Color.valueOf("542022"));
        TextButton continueButton = new TextButton(get("ancientOne.azathoth.ending.continue"), style);
        continueButton.setBounds(500, 25, 170, 40);
        card.addActor(continueButton);
        ButtonUtils.addClickListener(continueButton, () -> {
            continueButton.setTouchable(Touchable.disabled);
            remove();
            dismissed.onCompleted();
        });

        getColor().a = 0;
        addAction(Actions.parallel(Actions.fadeIn(0.6f), Actions.run(this::playOpeningSound)));
    }

    private void playOpeningSound() {
        if (Gdx.audio == null) return;
        try {
            openingSound = CustomAssetManager.getSound("sounds/azathoth_ending.wav");
            openingSoundId = openingSound.play(1f);
        } catch (RuntimeException failure) {
            // A missing audio device must not block the end-of-game controls.
            Gdx.app.error("AzathothEndingDialog", "Could not play ending sound", failure);
        }
    }

    @Override
    public boolean remove() {
        clearActions();
        if (openingSound != null && openingSoundId != -1) {
            openingSound.stop(openingSoundId);
            openingSoundId = -1;
        }
        return super.remove();
    }

    public Completable awaitDismissal() {
        return dismissed.toCompletable();
    }

    private static Label label(Group parent, String text, String font, int size, Color color,
                               float x, float y, float width, float height, int alignment) {
        Label label = new Label(text, new Label.LabelStyle(getBitmapFontNew(font, size), color));
        label.setWrap(true);
        label.setAlignment(alignment);
        label.setBounds(x, y, width, height);
        parent.addActor(label);
        return label;
    }

    private static Image solid(Color color) {
        Image image = new Image(getTextureRegionDrawable(PURE_WHITE_BACKGROUND));
        image.setColor(color);
        return image;
    }

    private static void line(Group card, float x, float y, float width) {
        Image line = solid(BRONZE);
        line.setBounds(x, y, width, 1);
        card.addActor(line);
    }
}
