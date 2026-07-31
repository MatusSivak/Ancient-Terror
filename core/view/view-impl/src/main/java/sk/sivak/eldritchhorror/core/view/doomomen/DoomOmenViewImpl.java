package sk.sivak.eldritchhorror.core.view.doomomen;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.controller.DoomOmenController;
import sk.sivak.eldritchhorror.core.view.DoomOmenView;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.effect.NoiseEffect;
import sk.sivak.eldritchhorror.core.view.components.track.ClickZoomListener;
import sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget;
import sk.sivak.eldritchhorror.core.view.components.track.OmenTrack;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.DOOM_CHANGED;
import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.HUD_BUTTON;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class DoomOmenViewImpl implements DoomOmenView {

    private DoomOmenController controller;

    public void setController(DoomOmenController controller) {
        this.controller = controller;
    }

    @Override
    public Completable retreatDoom(int positiveAmount) {
        return advanceOrRetreatDoom(get("doom.retreating"), positiveAmount);
    }

    @Override
    public Completable advanceDoom(int negativeAmount) {
        return advanceOrRetreatDoom(get("doom.advancing"), negativeAmount);
    }

    private Completable advanceOrRetreatDoom(String text, int amount) {
        return Completable.create(onSub -> {
            InfoStage.displayTextDontHide(text);
            DoomTrackWidget doomTrackWidget = HudButtons.getDoomTrack();
            ClickZoomListener clickZoomListener = new ClickZoomListener(doomTrackWidget);
            clickZoomListener.setOnCompletedListener(() -> {
                doomTrackWidget.addOnCompletedListener(() -> {
                    clickZoomListener.setOnCompletedListener(() -> {
                        onSub.onCompleted();
                        InfoStage.hideLabel(text);
                    });
                    clickZoomListener.minimize();
                });
                GoogleServicesHolder.getAnalyticsTracker().trackNonInteraction(DOOM_CHANGED, "" + (doomTrackWidget.getCurrentDoom() + amount));
                doomTrackWidget.updateDoom(doomTrackWidget.getCurrentDoom() + amount);
            });
            clickZoomListener.maximize();
        });
    }

    @Override
    public Completable addTokenToOmenTrack(OmenId omenId) {
        return Completable.create(onSub -> {
            String label = get("omen.addingToken");
            InfoStage.displayTextDontHide(label);
            OmenTrack omenTrack = HudButtons.getOmenTrack();
            ClickZoomListener clickZoomListener = new ClickZoomListener(omenTrack);
            clickZoomListener.setOnCompletedListener(() -> {
                boolean hasTokens = omenTrack.getTokens(omenId) > 0;
                omenTrack.addAction(Actions.sequence(
                        new FastForwardAction(Actions.delay(0.25f)),
                        Actions.run(() -> {
                            omenTrack.addTokenToOmen(omenId);
                        }),
                        new FastForwardAction(Actions.delay(hasTokens ? 2.0f : 1.5f)),
                        Actions.run(() -> {
                            clickZoomListener.setOnCompletedListener(() -> {
                                InfoStage.hideLabel(label);
                                onSub.onCompleted();
                            });
                            clickZoomListener.minimize();
                        })
                ));

            });
            clickZoomListener.maximize();
        });
    }

    @Override
    public Completable removeTokenFromOmenTrack(OmenId omenId) {
        return Completable.create(onSub -> {
            String label = get("omen.removingToken");
            InfoStage.displayTextDontHide(label);
            OmenTrack omenTrack = HudButtons.getOmenTrack();
            ClickZoomListener clickZoomListener = new ClickZoomListener(omenTrack);
            clickZoomListener.setOnCompletedListener(() -> {
                omenTrack.addAction(Actions.sequence(
                        new FastForwardAction<>(Actions.delay(0.25f)),
                        Actions.run(() -> {
                            omenTrack.removeTokenFromOmen(omenId);
                        }),
                        new FastForwardAction<>(Actions.delay(1.5f)),
                        Actions.run(() -> {
                            clickZoomListener.setOnCompletedListener(() -> {
                                InfoStage.hideLabel(label);
                                onSub.onCompleted();
                            });
                            clickZoomListener.minimize();
                        })
                ));

            });
            clickZoomListener.maximize();
        });
    }

    @Override
    public Completable advanceOmen(OmenId omenId) {
        return Completable.create(onSub -> {
            String label = get("omen.advancing");
            InfoStage.displayTextDontHide(label);
            OmenTrack omenTrack = HudButtons.getOmenTrack();
            ClickZoomListener clickZoomListener = new ClickZoomListener(omenTrack);
            clickZoomListener.setOnCompletedListener(() -> {
                omenTrack.addAction(Actions.sequence(
                        new FastForwardAction<>(Actions.delay(0.25f)),
                        Actions.run(() -> {
                            omenTrack.updateOmen(omenId);
                        }),
                        new FastForwardAction<>(Actions.delay(1)),
                        Actions.run(() -> {
                            clickZoomListener.setOnCompletedListener(() -> {
                                InfoStage.hideLabel(label);
                                onSub.onCompleted();
                            });
                            clickZoomListener.minimize();
                        })
                ));

            });
            clickZoomListener.maximize();
        });
    }

    @Override
    public Single<OmenId> selectNewOmen() {
        return Single.create(onSub -> {
            InfoStage.displayTextDontHide(get("omen.selectNew"));
            OmenTrack omenTrack = HudButtons.getOmenTrack();
            ClickZoomListener clickZoomListener = new ClickZoomListener(omenTrack);
            clickZoomListener.setOnCompletedListener(() -> selectNewOmen(onSub, omenTrack, clickZoomListener));
            clickZoomListener.maximize();
        });
    }

    private void selectNewOmen(SingleSubscriber<? super OmenId> onSub, OmenTrack omenTrack, ClickZoomListener clickZoomListener) {
        omenTrack.initSelectNewOmenListeners(onSub, clickZoomListener);
    }

    @Override
    public void justAddTokensToOmenTrack(OmenId omenId, int tokensCount) {
        for (int i = 0; i < tokensCount; i++) {
            HudButtons.getOmenTrack().justAddTokensToOmen(omenId, tokensCount);
        }

    }
}
