package sk.sivak.eldritchhorror.core.view;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;

/**
 * @author msivak
 */
public interface DoomOmenView {

    Completable retreatDoom(int positiveAmount);

    Completable advanceDoom(int negativeAmount);

    Completable addTokenToOmenTrack(OmenId omenId);

    Completable removeTokenFromOmenTrack(OmenId omenId);

    Completable advanceOmen(OmenId omenId);

    Single<OmenId> selectNewOmen();

    void justAddTokensToOmenTrack(OmenId omenId, int tokensCount);
}
