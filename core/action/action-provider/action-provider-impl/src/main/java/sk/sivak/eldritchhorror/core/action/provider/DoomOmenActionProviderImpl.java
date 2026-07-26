package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.AdvanceDoomAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.AdvanceDoomByCurrentOmenAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.AdvanceOmenAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.AncientOneAwakensAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.CountdownRumorCardAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.DisplayVictoryPaperAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.DrawMythosCardAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.HighlightRumorFailureAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.HighlightRumorObjectiveAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.HighlightRumorReckoningAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.IncreaseAncientOnePowerAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.InitRumorCardAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.OmenChangedEventAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.OnTriggeredReckoningAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.PrepareNewRoundAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.ResolveCurrentMysteryAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.RetreatDoomAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.SelectNewOmenAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.ShowRumorCardAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.SpawnStormAction;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.UnlockRandomCardAction;
import sk.sivak.eldritchhorror.core.action.impl.initgame.AddTokenToOmenTrackAction;
import sk.sivak.eldritchhorror.core.action.impl.initgame.RemoveTokenFromOmenTrackAction;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;

/**
 * @author msivak
 */
public class DoomOmenActionProviderImpl extends AbstractActionProvider implements DoomOmenActionProvider {

    @Override
    public Action<OmenId, Void> addTokenToOmenTrack() {
        return new AddTokenToOmenTrackAction();
    }

    @Override
    public HookableAction<Object, Object> retreatDoom() {
        return new RetreatDoomAction();
    }

    @Override
    public HookableAction<Integer, Void> advanceDoom() {
        return new AdvanceDoomAction();
    }

    @Override
    public HookableAction<OmenInfo, Void> advanceDoomByCurrentOmen() {
        return new AdvanceDoomByCurrentOmenAction();
    }

    @Override
    public HookableAction<Object, OmenInfo> advanceOmen() {
        return new AdvanceOmenAction();
    }

    @Override
    public Action<Object, OmenInfo> selectNewOmen() {
        return new SelectNewOmenAction();
    }

    @Override
    public DirectEventAction<OmenInfo> omenChangedEvent() {
        return new OmenChangedEventAction();
    }

    @Override
    public Action<Object, Void> removeTokenFromOmenTrack(OmenId omenId) {
        return new RemoveTokenFromOmenTrackAction(omenId);
    }

    @Override
    public HookableAction<Object, Void> drawMythosCard() {
        return new DrawMythosCardAction();
    }

    @Override
    public Action<Object, Void> displayVictoryPaper() {
        return new DisplayVictoryPaperAction();
    }

    @Override
    public Action<Object, Void> onTriggeredReckoning() {
        return new OnTriggeredReckoningAction();
    }

    @Override
    public HookableAction<Object, Boolean> resolveCurrentMystery() {
        return new ResolveCurrentMysteryAction();
    }

    @Override
    public HookableAction<Object, Void> prepareNewRound() {
        return new PrepareNewRoundAction();
    }

    @Override
    public Action<Object, Void> unlockRandomCard() {
        return new UnlockRandomCardAction();
    }

    @Override
    public Action<Object, Void> initRumorCard(InitRumorCardData initRumorCardData) {
        return new InitRumorCardAction(initRumorCardData);
    }

    @Override
    public Action<Object, Void> showRumorCard(String rumorId) {
        return new ShowRumorCardAction(rumorId);
    }

    @Override
    public Action<Object, Void> countdownRumorCard(String rumorId, int amount) {
        return new CountdownRumorCardAction(rumorId, amount);
    }

    @Override
    public Action<Object, Void> highlightRumorFailure(String rumorId) {
        return new HighlightRumorFailureAction(rumorId);
    }

    @Override
    public Action<Object, Void> highlightRumorObjective(String rumorId) {
        return new HighlightRumorObjectiveAction(rumorId);
    }

    @Override
    public Action<Object, Void> highlightRumorReckoning(String rumorId) {
        return new HighlightRumorReckoningAction(rumorId);
    }

    @Override
    public Action<Object, Void> spawnStorm(String stormId, LocationId location) {
        return new SpawnStormAction(stormId, location);
    }

    @Override
    public Action<Object, Void> ancientOneAwakens() {
        return new AncientOneAwakensAction();
    }

    @Override
    public Action<Object, Void> increaseAncientOnePower(int power) {
        return new IncreaseAncientOnePowerAction(power);
    }
}
