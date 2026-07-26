package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.action.SelectActionComponent;
import sk.sivak.eldritchhorror.core.view.components.hud.InvestigatorHud;
import sk.sivak.eldritchhorror.core.view.components.skill.ImproveSkillComponent;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.SourceTargetGroup;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

/**
 * @author msivak
 */
public class InfoStage {

    private static InfoStage instance;
    private static boolean supressTextOnLabel;
    private final InvestigatorHud investigatorHud;
    private final Stage stage;
    private final Label label;
    private final Image grayBackground;
    private final FastForwardButton fastForwardButton;
    private final MenuButton menuButton;
    private final Group smallActors;
    private final Group diceLayer;
    private final Group bigActors;
    private final Group buttonLayer;
    private final Group chalkboardLayer;
    private final Group touchBlockerLayer;
    private final Group noiseEffectLayer;
    private final Group hudButtonsLayer;
    private float bottomHeight = 5;
    private PublishSubject<Actor> addSmallActorSubject;
    private final PublishSubject<Actor> hideActorSubject;

    public InfoStage() {
        stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        addSmallActorSubject = PublishSubject.create();
        hideActorSubject = PublishSubject.create();
        grayBackground = createGrayBackground();
        label = createLabel();
        fastForwardButton = new FastForwardButton();
        menuButton = new MenuButton();
        investigatorHud = createInvestigatorHud();
        smallActors = new Group();
        buttonLayer = new Group();
        chalkboardLayer = new Group();
        touchBlockerLayer = new Group();
        hudButtonsLayer = new Group();
        diceLayer = new Group();
        noiseEffectLayer = new Group();
        bigActors = new Group() {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (getChildren().size == 1) {
                    if (getChildren().get(0) instanceof SelectActionComponent ||
                            getChildren().get(0) instanceof SourceTargetGroup ||
                            getChildren().get(0) instanceof ImproveSkillComponent) {
                        showSmallActors();
                    } else {
                        hideSmallActors();
                    }
                } else if (getChildren().size > 1) {
                    hideSmallActors();
                } else {
                    showSmallActors();
                }
            }
        };

        grayBackground.setVisible(false);
        stage.addActor(grayBackground);
        stage.addActor(label);
        stage.addActor(investigatorHud);
        stage.addActor(smallActors);
        stage.addActor(diceLayer);
        stage.addActor(bigActors);
        stage.addActor(buttonLayer);

        stage.addActor(hudButtonsLayer);
        stage.addActor(touchBlockerLayer);
        stage.addActor(chalkboardLayer);
        stage.addActor(noiseEffectLayer);

        if (!GoogleServicesHolder.isTutorialPassed()) {
            supressTextOnLabel = true;
        }
    }

    public static void displayFastForwardButton() {
        if (GoogleServicesHolder.isTutorialPassed()) {
            get().stage.addActor(get().fastForwardButton);
        }
    }

    public static void displayMenuButton(Skin skin) {
        if (GoogleServicesHolder.isTutorialPassed()) {
            get().stage.addActor(get().menuButton);
            get().menuButton.setSkinProperty(skin);
        }
    }

    private boolean hidingSmallActors = false;
    private boolean showingSmallActors = false;

    private void showSmallActors() {
        if (showingSmallActors) {
            return;
        }
        showingSmallActors = true;
        hidingSmallActors = false;
        Group[] groups = new Group[] {smallActors, buttonLayer};
        for (Group group : groups) {
            group.clearActions();
            group.addAction(Actions.sequence(
                    Actions.alpha(1,0.5f),
                    Actions.run(() -> showingSmallActors = false)
            ));
        }
    }

    private void hideSmallActors() {
        if (hidingSmallActors) {
            return;
        }
        hidingSmallActors = true;
        showingSmallActors = false;
        Group[] groups = new Group[] {smallActors, buttonLayer};
        for (Group group : groups) {
            group.clearActions();
            group.addAction(Actions.sequence(
                    Actions.alpha(0,0.5f),
                    Actions.run(() -> hidingSmallActors = false)
            ));
        }
    }

    private static InfoStage get() {
        if (instance == null) {
            instance = new InfoStage();
        }
        return instance;
    }

    public static void nullifyInstance() {
        instance = null;
    }

    public static FastForwardButton getFastForwardButton() {
        return get().fastForwardButton;
    }

    public static Stage getStageSafe() {
        return get().stage;
    }

    public static void addSmallActorToInfoStage(Actor actor) {
        get().smallActors.addActor(actor);
        get().addSmallActorSubject.onNext(actor);
    }

    public static PublishSubject<Actor> getAddSmallActorSubject() {
        return get().addSmallActorSubject;
    }

    public static PublishSubject<Actor> getHideActorSubject() {
        return get().hideActorSubject;
    }

    public static void addActorToButtonLayer(Actor actor) {
        get().buttonLayer.addActor(actor);
    }

    public static Group getDiceLayer() {
        return get().diceLayer;
    }

    public static Group getTouchBlockerLayer() {
        return get().touchBlockerLayer;
    }

    public static Group getHudButtonsLayer() {
        return get().hudButtonsLayer;
    }

    public static Group getNoiseEffectLayer() {
        return get().noiseEffectLayer;
    }

    public static Group getChalkboardLayer() {
        return get().chalkboardLayer;
    }

    public static void addBigActor(OnScreenActors.ActorKey actorKey, Actor actor, Action0 hideAction, Action0 showAction) {
        OnScreenActors.register(actorKey, actor, hideAction, showAction);
        get().bigActors.addActor(actor);
    }

    public static void addActionToInfoStage(Action action) {
        get().stage.addAction(action);
    }

    public static InvestigatorHud getInvestigatorHud() {
        return get().investigatorHud;
    }

    public static void hideLabel(String expectedText) {

        if (!expectedText.equals(get().label.getText().toString())) {
            return;
        }
        if (get().label.getActions().size > 0) {
            get().label.clearActions();
        }
        AlphaAction alphaActionOut = new AlphaAction();
        alphaActionOut.setActor(get().label);
        alphaActionOut.setAlpha(0);
        alphaActionOut.setDuration(NORMAL_ACTION_DURATION);
        get().label.addAction(alphaActionOut);

        AlphaAction alphaActionOut2 = new AlphaAction();
        alphaActionOut2.setActor(get().grayBackground);
        alphaActionOut2.setAlpha(0);
        alphaActionOut2.setDuration(NORMAL_ACTION_DURATION);
        get().grayBackground.addAction(alphaActionOut2);

    }



    private static void showBackground(boolean fadeAway) {
        Image grayBackground = get().grayBackground;
        grayBackground.setVisible(true);
        grayBackground.clearActions();
        grayBackground.getColor().a = 0;
        float labelWidth = get().label.getWidth();
        float labelHeight = get().label.getHeight();
        float imageScale = 1.05f;
        grayBackground.setWidth(labelWidth * imageScale);

        grayBackground.setHeight(labelHeight * imageScale);
        grayBackground.setX(get().label.getX() - (labelWidth * imageScale - labelWidth) / 2);
        grayBackground.setY(get().label.getY() - (labelHeight * imageScale - labelHeight) / 2);

        AlphaAction alphaActionIn = new AlphaAction();
        alphaActionIn.setActor(grayBackground);
        alphaActionIn.setAlpha(0.75f);
        alphaActionIn.setDuration(NORMAL_ACTION_DURATION);

        AlphaAction alphaActionOut = new AlphaAction();
        alphaActionOut.setActor(grayBackground);
        alphaActionOut.setAlpha(0f);
        alphaActionOut.setDuration(NORMAL_ACTION_DURATION);

        if (fadeAway) {
            grayBackground.addAction(sequence(alphaActionIn, Actions.delay(NORMAL_ACTION_DURATION * 2), alphaActionOut));
        } else {
            grayBackground.addAction(alphaActionIn);
        }
    }

    public static void displayText(String text) {
        if (get().label.getText().toString().equals(text)) {
            return;
        }
        displayTextForce(text);
    }

    public static void displayTextForce(String text) {
        displayTextOnLabel(text, true);
        showBackground(true);
    }

    public static void displayTextDontHide(String text) {
        displayTextOnLabel(text, false);
        showBackground(false);
    }

    private static void displayTextOnLabel(String text, boolean fadeAway) {
        if (supressTextOnLabel) {
            return;
        }
        Label label = get().label;
        label.clearActions();
        label.getColor().a = 0f;
        label.setText(text);
        label.pack();
        label.setPosition(VIEWPORT_WIDTH / 2 - label.getWidth() / 2, VIEWPORT_HEIGHT - label.getHeight() + 13);

        AlphaAction alphaActionIn = new AlphaAction();
        alphaActionIn.setActor(label);
        alphaActionIn.setAlpha(1);
        alphaActionIn.setDuration(NORMAL_ACTION_DURATION);

        AlphaAction alphaActionOut = new AlphaAction();
        alphaActionOut.setActor(label);
        alphaActionOut.setAlpha(0);
        alphaActionOut.setDuration(NORMAL_ACTION_DURATION);

        if (fadeAway) {
            label.addAction(sequence(alphaActionIn, Actions.delay(NORMAL_ACTION_DURATION * 2), alphaActionOut));
        } else {
            label.addAction(sequence(alphaActionIn));
        }
    }

    public static void showButton(Button button) {
        showButton(button,
                VIEWPORT_WIDTH / 2f - button.getWidth() / 2,
                5);
    }

    public static void showButton(Button button, float posX, float posY) {
        showButton(button, posX, posY, true);
    }

    public static void showButton(Button button, float posX, float posY, boolean hide) {
        get().buttonLayer.addActor(button);
        button.getColor().a = 0f;
        button.setPosition(posX,
                posY);
        AlphaAction alphaActionIn = new AlphaAction();
        alphaActionIn.setActor(button);
        alphaActionIn.setAlpha(1);
        alphaActionIn.setDuration(NORMAL_ACTION_DURATION);

        if (hide) {
            button.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (button.isDisabled()) {
                        return;
                    }
                    hideActor(button);
                }
            });
        }
        button.addAction(alphaActionIn);
    }

    public static void hideActor(Actor actor) {
        hideActor(actor, null);
    }

    public static void hideActor(Actor actor, Action0 onHideAction) {
        get().hideActorSubject.onNext(actor);
        actor.setTouchable(Touchable.disabled);
        actor.addAction(new FastForwardAction<>(sequence(createHideAction(actor, 1f),
                Actions.run(() -> {
                    if (onHideAction != null) {
                        onHideAction.call();
                    }
                    actor.remove();
                }))));
    }

    public static void hideActor(Actor actor, Action0 onHideAction, float durationMultiplier) {
        actor.setTouchable(Touchable.disabled);
        actor.addAction(new FastForwardAction<>(sequence(createHideAction(actor, durationMultiplier),
                Actions.run(() -> {
                    actor.remove();
                    if (onHideAction != null) {
                        onHideAction.call();
                    }
                }))));
    }

    public static void showActor(Actor actor) {
        addSmallActorToInfoStage(actor);
        actor.addAction(new FastForwardAction<>(createShowAction(actor)));
    }

    private static AlphaAction createHideAction(Actor actor, float durationMultiplier) {
        AlphaAction alphaActionOut = new AlphaAction();
        alphaActionOut.setActor(actor);
        alphaActionOut.setAlpha(0);
        alphaActionOut.setDuration(NORMAL_ACTION_DURATION* durationMultiplier);
        return alphaActionOut;
    }

    private static AlphaAction createShowAction(Actor actor) {
        actor.setColor(new Color(0xffffff00));
        AlphaAction alphaActionOut = new AlphaAction();
        alphaActionOut.setActor(actor);
        alphaActionOut.setAlpha(1f);
        alphaActionOut.setDuration(NORMAL_ACTION_DURATION);
        return alphaActionOut;
    }

    public static float getBottomHeight() {
        return get().bottomHeight;
    }

    public static void setBottomHeight(float bottomHeight) {
        get().bottomHeight = bottomHeight;
    }


    private InvestigatorHud createInvestigatorHud() {
        InvestigatorHud hud = new InvestigatorHud();
        hud.setHeight(VIEWPORT_HEIGHT * HUD_HEIGHT_RATIO);
        hud.setPosition(0, -hud.getHeight() - hud.getHudPadding());
        return hud;
    }

    private Image createGrayBackground() {
        Image created = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        created.setColor(Color.BLACK);
        created.setX(VIEWPORT_WIDTH / 2f - created.getWidth() / 2);
        created.setY(VIEWPORT_HEIGHT / 2f - created.getHeight() / 2);
        return created;
    }

    private Label createLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_BLACK_CHANCERY);
        Label createdLabel = new Label("", labelStyle);
        createdLabel.setFontScale(1f);
        return createdLabel;
    }

    public static MenuButton getMenuButton() {
        return get().menuButton;
    }

    public static void reset() {
        getStageSafe().clear();
        instance = new InfoStage();
    }
}
