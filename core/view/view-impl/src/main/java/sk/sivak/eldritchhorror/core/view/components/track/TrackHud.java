package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class TrackHud extends Group {

    private final OmenTrack omenTrack;
    private final DoomTrackWidget doomTrackWidget;
    private final float minimizedWidth;
    private final float minimizedHeight;
    private Texture background;

    public static final float PADDING = VIEWPORT_WIDTH / 80f;
    private boolean displayed = false;
    private boolean moving = false;

    public TrackHud() {
        omenTrack = new OmenTrack();
        omenTrack.setPosition(5, 5);

        doomTrackWidget = new DoomTrackWidget();
        doomTrackWidget.setPosition(omenTrack.getX() + omenTrack.getWidth() + 5, 5);

        ClickListener omenTrackClickListener = addClickListener(omenTrack, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.HUD_BUTTON, "Omen");
            BigActorsManager.displayOrHideOmenTrack();
        });

        omenTrack.setShowHideClickListener(omenTrackClickListener);


        addClickListener(doomTrackWidget, () -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.HUD_BUTTON, "Doom");
            BigActorsManager.displayOrHideDoomTrack();
        });


        addActor(doomTrackWidget);
        addActor(omenTrack);

        minimizedWidth = getWidth();
        minimizedHeight = getHeight();
    }

    @Override
    public float getWidth() {
        return doomTrackWidget.getWidth() + omenTrack.getWidth() + 15;
    }

    @Override
    public float getHeight() {
        return 77;
    }

    public OmenTrack getOmenTrack() {
        return omenTrack;
    }

    public DoomTrackWidget getDoomTrackWidget() {
        return doomTrackWidget;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(new Color(0, 0, 0, 0.5f));
        background = getTexture(PURE_WHITE_BACKGROUND);
        batch.draw(background, getX(), getY(),
                minimizedWidth,
                minimizedHeight);
        super.draw(batch, parentAlpha);
    }

    public Completable show() {
        return hideOrShow(true);
    }

    public Completable hide() {
        return hideOrShow(false);
    }

    private Completable hideOrShow(boolean show) {
        if (moving) {
            moving = false;
            displayed = show;
            clearActions();
            return Completable.complete();
        }
        if (displayed == show) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            moving = true;
            MoveToAction action = new MoveToAction();
            if (show) {
                action.setPosition(getX(), ViewProperties.VIEWPORT_HEIGHT-getHeight());
            } else {
                action.setPosition(getX(), getY() + getHeight() + PADDING);
            }
            action.setDuration(FAST_ACTION_DURATION);
            action.setTarget(this);
            addAction(sequence(new FastForwardAction(action), run(() -> {
                displayed = show;
                moving = false;
                onSub.onCompleted();
            })));
        });
    }
}
