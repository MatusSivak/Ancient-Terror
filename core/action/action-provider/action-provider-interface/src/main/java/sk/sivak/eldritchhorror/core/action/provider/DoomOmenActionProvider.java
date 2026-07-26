package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;

/**
 * @author msivak
 */
public interface DoomOmenActionProvider {

    Action<OmenId, Void> addTokenToOmenTrack();

    HookableAction<Object, Object> retreatDoom();

    HookableAction<Integer, Void> advanceDoom();

    HookableAction<Object, OmenInfo> advanceOmen();

    Action<Object, OmenInfo> selectNewOmen();

    DirectEventAction<OmenInfo> omenChangedEvent();

    HookableAction<OmenInfo, Void> advanceDoomByCurrentOmen();

    Action<Object, Void> removeTokenFromOmenTrack(OmenId omenId);

    HookableAction<Object, Void> drawMythosCard();

    Action<Object, Void> displayVictoryPaper();

    Action<Object, Void> onTriggeredReckoning();

    HookableAction<Object, Boolean> resolveCurrentMystery();

    HookableAction<Object, Void> prepareNewRound();

    Action<Object, Void> unlockRandomCard();

    Action<Object, Void> initRumorCard(InitRumorCardData initRumorCardData);

    Action<Object, Void> showRumorCard(String rumorId);

    Action<Object, Void> countdownRumorCard(String rumorId, int amount);

    Action<Object, Void> highlightRumorFailure(String rumorId);

    Action<Object, Void> highlightRumorObjective(String rumorId);

    Action<Object, Void> highlightRumorReckoning(String rumorId);

    Action<Object, Void> spawnStorm(String stormId, LocationId location);

    Action<Object, Void> ancientOneAwakens();

    Action<Object, Void> increaseAncientOnePower(int power);
}
