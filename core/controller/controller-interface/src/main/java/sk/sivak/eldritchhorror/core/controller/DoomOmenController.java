package sk.sivak.eldritchhorror.core.controller;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;

/**
 * @author msivak
 */
public interface DoomOmenController {

    void setGateDoomPulse(LocationId location, boolean enabled);

    Completable retreatDoom(int doom, int positiveAmount);

    Completable advanceDoom(int doom, int negativeAmount);

    Completable addTokenToOmenTrack(OmenId input);

    Completable removeTokenFromOmenTrack(OmenId omenId);

    Completable advanceOmen(OmenInfo omenInfo);

    void updateNoiseColorAndGatesTransparency(OmenColor omenColor);

    Single<OmenId> selectNewOmen();

    void justAddTokensToOmenTrack(OmenId omenId, int tokensCount);
}
