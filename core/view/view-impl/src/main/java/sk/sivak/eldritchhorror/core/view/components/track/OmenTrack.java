package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.button.HideOkButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.HashMap;
import java.util.Map;

import static java8.features.util.MapUtils.forEach;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class OmenTrack extends Group {

    public static final float MIN_SCALE = 0.5f;
    private Map<OmenId, OmenIdComponent> omenIdComponentMap = new HashMap<>();
    private OmenId omenId;
    private ClickListener showHideClickListener;

    public OmenTrack() {
        omenIdComponentMap.put(OmenId.NORTH, new OmenIdComponent(
                new Image(getTextureRegionDrawable(OMEN_GREEN)),
                new Image(getTextureRegionDrawable(OMEN_GREEN))
        ));

        omenIdComponentMap.put(OmenId.EAST, new OmenIdComponent(
                new Image(getTextureRegionDrawable(OMEN_BLUE)),
                new Image(getTextureRegionDrawable(OMEN_BLUE))
        ));

        omenIdComponentMap.put(OmenId.WEST, new OmenIdComponent(
                new Image(getTextureRegionDrawable(OMEN_BLUE)),
                new Image(getTextureRegionDrawable(OMEN_BLUE))
        ));

        omenIdComponentMap.put(OmenId.SOUTH, new OmenIdComponent(
                new Image(getTextureRegionDrawable(OMEN_RED)),
                new Image(getTextureRegionDrawable(OMEN_RED))
        ));

        OmenIdComponent northComponent = omenIdComponentMap.get(OmenId.NORTH);
        OmenIdComponent westComponent = omenIdComponentMap.get(OmenId.WEST);
        OmenIdComponent eastComponent = omenIdComponentMap.get(OmenId.EAST);
        OmenIdComponent southComponent = omenIdComponentMap.get(OmenId.SOUTH);

        int distance = 200;
        northComponent.setPosition(distance, distance * 2);
        westComponent.setPosition(0, distance);
        eastComponent.setPosition(distance * 2, distance);
        southComponent.setPosition(distance, 0);

        Image background = new Image(getTextureRegionDrawable(OMEN_BACKGROUND));
        addActor(background);
        addActor(northComponent);
        addActor(westComponent);
        addActor(eastComponent);
        addActor(southComponent);

        setScale(67 / 700f);
    }

    public int getTokens(OmenId omenId) {
        return omenIdComponentMap.get(omenId).omenTrackTokens.getTokensCount();
    }

    public void updateOmen(OmenId omenId) {
        this.omenId = omenId;
        forEach(omenIdComponentMap, (oi, omenIdComponent) -> omenIdComponent.deActivate());
        OmenIdComponent component = omenIdComponentMap.get(omenId);
        component.setOrigin(component.getWidth() / 2, component.getHeight() / 2);
        component.activate();
    }

    public void initSelectNewOmenListeners(SingleSubscriber<? super OmenId> onSub, ClickZoomListener clickZoomListener) {
        HideOkButtons hideOkButtons = new HideOkButtons() {
            @Override
            protected void onConfirm() {
                InfoStage.hideActor(getHideButton());
                InfoStage.hideActor(getOkButton());
                InfoStage.setBottomHeight(5);
            }
        };

        for (Map.Entry<OmenId, OmenIdComponent> entry : omenIdComponentMap.entrySet()) {
            entry.getValue().deActivate();
            ButtonUtils.addClickListener(entry.getValue(), () -> {
                updateOmen(entry.getKey());
                hideOkButtons.enableOkButton();
            });
        }
        removeListener(showHideClickListener);
        BigActorsManager.lock(BigActorsManager.BigActorKey.OMEN_TRACK);

        hideOkButtons.init("Display Omen?", () -> {
            for (Map.Entry<OmenId, OmenIdComponent> entry : omenIdComponentMap.entrySet()) {
                entry.getValue().clearListeners();
            }
            addListener(showHideClickListener);
            BigActorsManager.unlock(BigActorsManager.BigActorKey.OMEN_TRACK);
            clickZoomListener.minimize();
            clickZoomListener.setOnCompletedListener(() -> {});
            onSub.onSuccess(omenId);
        }, this);
        hideOkButtons.showButtons();
        hideOkButtons.disableOkButton();
    }

    public void addTokenToOmen(OmenId omenId) {
        omenIdComponentMap.get(omenId).addToken();
    }

    public void justAddTokensToOmen(OmenId omenId, int amount) {
        omenIdComponentMap.get(omenId).justAddTokens(amount);
    }


    public void removeTokenFromOmen(OmenId omenId) {
        omenIdComponentMap.get(omenId).removeToken();
    }

    public OmenId getOmenId() {
        return omenId;
    }

    @Override
    public float getWidth() {
        return (200 * 2 + 300) * getScaleY();
    }

    @Override
    public float getHeight() {
        return getWidth();
    }

    public void setShowHideClickListener(ClickListener showHideClickListener) {
        this.showHideClickListener = showHideClickListener;
    }

    private class OmenIdComponent extends Group {

        private final Image circleDisabled;
        private final Image circle;
        private final Image colorImage;
        private final Image colorImageDisabled;
        private final OmenTrackTokens omenTrackTokens;

        public OmenIdComponent(Image colorImage, Image colorImageDisabled) {
            this.colorImage = colorImage;
            this.colorImage.setSize(200,200);
            this.colorImageDisabled = colorImageDisabled;
            this.colorImageDisabled.setSize(200,200);
            this.circle = new Image(getTextureRegionDrawable(OMEN_CIRCLE));
            this.circleDisabled = new Image(getTextureRegionDrawable(OMEN_CIRCLE_DISABLED));

            this.colorImage.setOrigin(colorImage.getWidth() / 2, colorImage.getHeight() / 2);
            this.colorImageDisabled.setOrigin(colorImageDisabled.getWidth() / 2, colorImageDisabled.getHeight() / 2);
            this.circle.setOrigin(circle.getWidth() / 2, circle.getHeight() / 2);
            this.circleDisabled.setOrigin(circleDisabled.getWidth() / 2, circleDisabled.getHeight() / 2);

            addActor(circleDisabled);
            addActor(colorImageDisabled);

            omenTrackTokens = new OmenTrackTokens();
            omenTrackTokens.setPosition(
                    getWidth() / 2 - OmenTrackTokens.TOKEN_SIZE / 2,
                    getHeight() / 2 - OmenTrackTokens.TOKEN_SIZE / 2);
            addActor(omenTrackTokens);

            colorImageDisabled.setPosition(50, 50);

            setOrigin(getWidth() / 2, getHeight() / 2);
            setScale(MIN_SCALE);
        }

        public void addToken() {
            omenTrackTokens.init(omenTrackTokens.getTokensCount() + 1);
        }

        public void justAddTokens(int amount) {
            omenTrackTokens.init(amount);
        }

        public void removeToken() {
            omenTrackTokens.init(Math.max(0, omenTrackTokens.getTokensCount() - 1));
        }

        public void activate() {
            removeActor(circleDisabled);
            removeActor(colorImageDisabled);
            removeActor(omenTrackTokens);

            addActor(circle);
            addActor(colorImage);
            addActor(omenTrackTokens);

            colorImage.setPosition(50, 50);

            addAction(Actions.scaleTo(1.2f, 1.2f, 1f, Interpolation.sine));
        }

        public void deActivate() {
            removeActor(omenTrackTokens);
            removeActor(circle);
            removeActor(colorImage);

            addActor(circleDisabled);
            addActor(colorImageDisabled);
            addActor(omenTrackTokens);

            colorImageDisabled.setPosition(50, 50);

            addAction(Actions.scaleTo(MIN_SCALE, MIN_SCALE, 1f, Interpolation.sine));
        }

        @Override
        public float getWidth() {
            return circle.getWidth();
        }

        @Override
        public float getHeight() {
            return circle.getHeight();
        }
    }
}
