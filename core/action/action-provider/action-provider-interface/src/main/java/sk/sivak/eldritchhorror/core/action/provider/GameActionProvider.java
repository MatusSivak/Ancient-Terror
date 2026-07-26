package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;

/**
 * @author msivak
 */
public interface GameActionProvider {

    Action<Void, Void> initGame();

    Action<Object, Void> showExpeditionLocation();

    Action<Object, Void> showCurrentMysteryCard(boolean moveCamera,boolean quick);

    HookableAction<Object, Void> advanceActiveMystery();

    Action<Object, Void> advanceCurrentMysteryCard(int amount);

    Action<Object, Void> showAncientOneCard();

    Action<Object, Void> spawnRedPins();

    Action<Object, Void> justAddRedPins();

    HookableAction<Object, GateInfo> spawnGate(boolean quick);

    HookableAction<SpawnMonsterData, SpawnMonsterData> spawnMonster(boolean quick);

    HookableAction<CollectAvailableActionsData, CollectAvailableActionsData> collectAvailableActions();

    HookableAction<CollectAvailableActionsData, CollectAvailableActionsData> filterPerformedActions();

    DirectEventAction<CollectAvailableActionsData> disableOrRemoveActions();

    HookableAction<CollectAvailableActionsData, ActionPhaseAction> selectAction();

    HookableAction<ActionPhaseAction, Void> executeAction();

    HookableAction<Object, AfterActionPerformedData> afterActionPerformed();

    HookableAction<AfterActionPerformedData, AfterActionPerformedData> withAfterActionPerformedData();

    Action<Object, Void> initInvestigators();

    Action<Object, Object> displayText(String text);

    HookableAction<ShowCardRequest, ShowCardResponse> showCard();

    <RD, AD> HookableAction<Question<RD>, Answer<RD, AD>> ask();

    HookableAction<GainCardData, GainedCardData> selectCardToGain();

    HookableAction<GainedCardData, GainedCardData> registerGainedCard();

    Action<Object, PhaseType> changePhase();

    HookableAction<Object, Void> showPhase();

    HookableAction<Object, Void> initPhase();

    HookableAction<SelectLocationData, LocationId> selectLocation();

    Action<Object, Void> verifyNotDelayed();

    Action<Void, Void> initActionButton();

    DirectEventAction<Object> performActionStart();

    Action<Object, Void> hasAnyFreeAction();

    HookableAction<Object, Void> monsterSurge(int monstersCount);

    HookableAction<SelectSingleGateData, GateInfo> selectSingleGate();

    Action<Object, Object> checkDefeatedInActionPhase();

    Action<Object, Void> clearRedPins();

    Action<Object, Void> spawnVortex(LocationId locationId);

    Action<Object, Void> removeVortex(LocationId currentLocationId);

    Action<Object, Object> askToRateThisGame();

    Action<Object, Void> restartGame();

    Action<Object, Void> sewCard(CardInfo cardInfo);
}
