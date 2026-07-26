package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
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
public interface TokenActionProvider {

    HookableAction<Integer, List<ClueInfo>> spawnCluesAction(boolean quick);

    HookableAction<GainHealthData, GainHealthData> gainHealth();

    HookableAction<GainSanityData, GainSanityData> gainSanity();

    HookableAction<GainClueData, GainClueData> gainClueFromPool();

    HookableAction<GainClueData, GainClueData> gainClueFromSpace();

    HookableAction<GainFocusData, GainFocusData> gainFocus();

    HookableAction<LoseTokenData, LoseTokenData> loseFocus();

    HookableAction<LoseTokenData, LoseTokenData> loseHealth();

    HookableAction<LoseTokenData, LoseTokenData> spendHealth();

    HookableAction<LoseTokenData, LoseTokenData> loseSanity();

    HookableAction<LoseTokenData, LoseTokenData> spendSanity();

    HookableAction<LoseTokenData, LoseTokenData> loseClue();

    HookableAction<SpendData, SpendData> spend();

    HookableAction<LocationId, LocationId> discardClue();

    Action<Object, Void> spawnClueAt(LocationId locationId);

    HookableAction<MoveTokenData, MoveTokenData> moveClue();
}
