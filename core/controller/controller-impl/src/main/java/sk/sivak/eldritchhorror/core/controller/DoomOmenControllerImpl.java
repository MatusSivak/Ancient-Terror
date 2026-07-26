package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.view.DoomOmenView;
import sk.sivak.eldritchhorror.core.view.GameView;

/**
 * @author msivak
 */
public class DoomOmenControllerImpl implements DoomOmenController {

    private DoomOmenView doomOmenView;
    private GameView gameView;

    public void setView(DoomOmenView doomOmenView) {
        this.doomOmenView = doomOmenView;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    public Completable retreatDoom(int doom, int positiveAmount) {
        gameView.updateNoiseIntensity(doom);
        return doomOmenView.retreatDoom(positiveAmount);
    }

    @Override
    public Completable advanceDoom(int doom, int negativeAmount) {
        gameView.updateNoiseIntensity(doom);
        return doomOmenView.advanceDoom(negativeAmount);
    }

    @Override
    public Completable addTokenToOmenTrack(OmenId omenId) {
        return doomOmenView.addTokenToOmenTrack(omenId);
    }

    @Override
    public Completable removeTokenFromOmenTrack(OmenId omenId) {
        return doomOmenView.removeTokenFromOmenTrack(omenId);
    }

    @Override
    public Completable advanceOmen(OmenInfo omenInfo) {
        updateNoiseColorAndGatesTransparency(omenInfo.getOmenColor());
        return doomOmenView.advanceOmen(omenInfo.getOmenId());
    }

    @Override
    public void updateNoiseColorAndGatesTransparency(OmenColor omenColor) {
        gameView.updateNoiseColor(omenColor);
        gameView.updateGatesTransparency(omenColor);
    }

    @Override
    public Single<OmenId> selectNewOmen() {
        return doomOmenView.selectNewOmen();
    }

    @Override
    public void justAddTokensToOmenTrack(OmenId omenId, int tokensCount) {
        doomOmenView.justAddTokensToOmenTrack(omenId, tokensCount);
    }
}
