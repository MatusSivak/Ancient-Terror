package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.Observable;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.hud.HudButton;
import sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget;
import sk.sivak.eldritchhorror.core.view.components.track.OmenTrack;
import sk.sivak.eldritchhorror.core.view.components.track.TrackHud;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.HUD_BUTTON;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.HUD_ANCIENT_ONE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.HUD_DISCARD;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.HUD_INVESTIGATOR;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.HUD_MYSTERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.HUD_RESERVE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.HUD_RUMOR;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;

public class HudButtons {
    public static final float BOTTOM_BONUS_PADDING = 3;
    public static final float PADDING = 0;
    private static HudButtons instance;
    private GameController controller;
    private ButtonGroup<HudButton> hudButtons;
    private Image background;
    private float availableSpace;
    private Set<HudButton> enabledByDefault = new HashSet<>();
    private final TrackHud trackHud;
    private boolean displayed = false;

    public HudButtons(GameController controller) {
        trackHud = createTrackGroup();
        this.controller = controller;
        HudButtons.instance = this;

        this.availableSpace = VIEWPORT_HEIGHT
                - InfoStage.getInvestigatorHud().getHeight()
                - BOTTOM_BONUS_PADDING
                - PADDING * 2
                - getTrackHud().getHeight();
    }

    public static DoomTrackWidget getDoomTrack() {
        return getTrackHud().getDoomTrackWidget();
    }

    public static TrackHud getTrackHud() {
        return instance.trackHud;
    }

    public static OmenTrack getOmenTrack() {
        return getTrackHud().getOmenTrack();
    }

    private TrackHud createTrackGroup() {
        TrackHud result = new TrackHud();
        result.setPosition(0, VIEWPORT_HEIGHT + TrackHud.PADDING);
        return result;
    }

    public static HudButtons getInstance() {
        return instance;
    }

    public void init() {
        background = new Image(CustomAssetManager.getTexture(PURE_WHITE_BACKGROUND));
        background.setColor(new Color(0f, 0f, 0f, 0.5f));
        background.setBounds(0, InfoStage.getInvestigatorHud().getHeight() + BOTTOM_BONUS_PADDING,
                availableSpace / 6f + PADDING * 2,
                availableSpace + PADDING * 2);
        background.setX(background.getX() - background.getWidth());
        InfoStage.getHudButtonsLayer().addActor(background);

        hudButtons = new ButtonGroup<>();
        addHudButtonOnStage(HUD_INVESTIGATOR, 0, true, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(HUD_BUTTON, "Passport");
            controller.displayInvestigatorPassport();
        });
        addHudButtonOnStage(HUD_RESERVE, 1, true, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(HUD_BUTTON, "Reserve");
            controller.showReserve();
        });
        addHudButtonOnStage(HUD_DISCARD, 2, false, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(HUD_BUTTON, "Discard");
            controller.showDiscard();
        });
        addHudButtonOnStage(HUD_MYSTERY, 3, true, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(HUD_BUTTON, "Mystery");
            controller.showCurrentMysteryCard(true)
                    .subscribe();
        });
        addHudButtonOnStage(HUD_RUMOR, 4, false, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(HUD_BUTTON, "Rumor");
            controller.showRumorCard().subscribe();
        });
        addHudButtonOnStage(HUD_ANCIENT_ONE, 5, true, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(HUD_BUTTON, "Ancient One");
            controller.showAncientOneCard().subscribe();
        });
        uncheckAll();

        disableEnabledButtons();
        enableEnabledButtons();

        InfoStage.getHudButtonsLayer().addActor(trackHud);
    }

    public void uncheckAll() {
        hudButtons.uncheckAll();
    }

    public void disableEnabledButtons() {
        for (HudButton hudButton : enabledByDefault) {
            hudButton.disable();
            hudButton.setTouchable(Touchable.disabled);
        }
    }

    public void enableEnabledButtons() {
        for (HudButton hudButton : enabledByDefault) {
            hudButton.enable();
            hudButton.setTouchable(Touchable.enabled);
        }
    }

    public void enableDiscardButton() {
        HudButton discardButton = hudButtons.getButtons().get(2);
        discardButton.enable();
        enabledByDefault.add(discardButton);
    }

    public void enableRumorsButton() {
        HudButton rumorsButton = hudButtons.getButtons().get(4);
        rumorsButton.enable();
        enabledByDefault.add(rumorsButton);
    }

    public void disableRumorsButton() {
        HudButton rumorsButton = hudButtons.getButtons().get(4);
        rumorsButton.disable();
        enabledByDefault.remove(rumorsButton);
    }

    private void addHudButtonOnStage(String iconPath, int position, boolean enabled, Runnable onClickAction) {
        float size = availableSpace / 6f;
        HudButton button;
        if (enabled) {
            button = HudButton.buildEnabled(iconPath);
            enabledByDefault.add(button);
        } else {
            button = HudButton.buildDisabled(iconPath);
        }
        if (onClickAction != null) {
            ButtonUtils.addClickListener(button, () -> {
                if (button.looksDisabled()) {
                    return;
                }
                onClickAction.run();
            });
        }
        button.setBounds(PADDING, InfoStage.getInvestigatorHud().getHeight() + BOTTOM_BONUS_PADDING + PADDING + position * size,
                size, size);
//        button.setX(button.getX() - button.getWidth());
        hudButtons.add(button);
        InfoStage.getHudButtonsLayer().addActor(button);
    }

    public Completable show() {
        return hideOrShow(true);
    }

    public Completable hide() {
        return hideOrShow(false);
    }

    private Completable hideOrShow(boolean show) {
        if (displayed == show) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            if (background == null || hudButtons == null) {
                onSub.onCompleted();
                return;
            }
            MoveToAction moveBackgroundAction = new MoveToAction();
            displayed = show;
            AtomicInteger pendingAnimations = new AtomicInteger(hudButtons.getButtons().size + 1);
            Runnable completeWhenDone = () -> {
                if (pendingAnimations.decrementAndGet() == 0) {
                    onSub.onCompleted();
                }
            };
            if (show) {
                moveBackgroundAction.setPosition(0, background.getY()); // 0
            } else {
                moveBackgroundAction.setPosition(-74, background.getY()); // -74
            }
            moveBackgroundAction.setDuration(FAST_ACTION_DURATION);
            moveBackgroundAction.setTarget(background);
            background.clearActions();
            background.addAction(sequence(new FastForwardAction(moveBackgroundAction), run(completeWhenDone)));
            for (HudButton hudButton : hudButtons.getButtons()) {
                MoveToAction action = new MoveToAction();
                if (show) {
                    action.setPosition(0, hudButton.getY()); // 3
                } else {
                    action.setPosition(-64, hudButton.getY()); // -64
                }
                action.setDuration(FAST_ACTION_DURATION);
                action.setTarget(hudButton);
                hudButton.clearActions();
                hudButton.addAction(sequence(new FastForwardAction(action), run(completeWhenDone)));
            }
        });
    }


}
