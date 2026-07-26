package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.GainClueFromPoolAction;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.GainClueFromSpaceAction;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.GainFocusAction;
import sk.sivak.eldritchhorror.core.action.impl.basic.GainHealthAction;
import sk.sivak.eldritchhorror.core.action.impl.basic.GainSanityAction;
import sk.sivak.eldritchhorror.core.action.impl.clue.SpawnCluesAction;
import sk.sivak.eldritchhorror.core.action.impl.game.*;
import sk.sivak.eldritchhorror.core.action.impl.initgame.AddTokenToOmenTrackAction;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainHealthData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainSanityData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainFocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.MoveTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.List;

/**
 * @author msivak
 */
public class TokenActionProviderImpl extends AbstractActionProvider implements TokenActionProvider {


    @Override
    public HookableAction<Integer, List<ClueInfo>> spawnCluesAction(boolean quick) {
        return new SpawnCluesAction(quick);
    }

    @Override
    public HookableAction<GainHealthData, GainHealthData> gainHealth() {
        return new GainHealthAction();
    }

    @Override
    public HookableAction<GainSanityData, GainSanityData> gainSanity() {
        return new GainSanityAction();
    }

    @Override
    public HookableAction<GainClueData, GainClueData> gainClueFromPool() {
        return new GainClueFromPoolAction();
    }

    @Override
    public HookableAction<GainClueData, GainClueData> gainClueFromSpace() {
        return new GainClueFromSpaceAction();
    }

    @Override
    public HookableAction<GainFocusData, GainFocusData> gainFocus() {
        return new GainFocusAction();
    }

    @Override
    public HookableAction<LoseTokenData, LoseTokenData> loseFocus() {
        return new LoseFocusAction();
    }

    @Override
    public HookableAction<LoseTokenData, LoseTokenData> loseSanity() {
        return new LoseSanityAction();
    }

    @Override
    public HookableAction<LoseTokenData, LoseTokenData> spendSanity() {
        return new SpendSanityAction();
    }

    @Override
    public HookableAction<LoseTokenData, LoseTokenData> loseHealth() {
        return new LoseHealthAction();
    }

    @Override
    public HookableAction<LoseTokenData, LoseTokenData> spendHealth() {
        return new SpendHealthAction();
    }

    @Override
    public HookableAction<LoseTokenData, LoseTokenData> loseClue() {
        return new LoseClueAction();
    }

    @Override
    public HookableAction<SpendData, SpendData> spend() {
        return new SpendAction();
    }

    @Override
    public HookableAction<LocationId, LocationId> discardClue() {
        return new DiscardClueAction();
    }

    @Override
    public Action<Object, Void> spawnClueAt(LocationId locationId) {
        return new SpawnClueAtAction(locationId);
    }

    @Override
    public HookableAction<MoveTokenData, MoveTokenData> moveClue() {
        return new MoveClueAction();
    }
}
