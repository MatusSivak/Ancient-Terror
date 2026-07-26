package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.action.*;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.CheckDefeatedInActionPhaseAction;
import sk.sivak.eldritchhorror.core.action.impl.card.RegisterGainedCardAction;
import sk.sivak.eldritchhorror.core.action.impl.card.SelectCardToGainAction;
import sk.sivak.eldritchhorror.core.action.impl.game.*;
import sk.sivak.eldritchhorror.core.action.impl.gate.SpawnGateAction;
import sk.sivak.eldritchhorror.core.action.impl.initgame.InitGameAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.MonsterSurgeAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.SpawnMonsterAction;
import sk.sivak.eldritchhorror.core.action.impl.phase.ChangePhaseAction;
import sk.sivak.eldritchhorror.core.action.impl.phase.InitPhaseAction;
import sk.sivak.eldritchhorror.core.action.impl.phase.ShowPhaseAction;
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
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;

/**
 * @author msivak
 */
public class GameActionProviderImpl extends AbstractActionProvider implements GameActionProvider {

    @Override
    public Action<Void, Void> initGame() {
        return new InitGameAction();
    }

    @Override
    public Action<Object, Void> showExpeditionLocation() {
        return new ShowExpeditionLocationAction();
    }

    @Override
    public Action<Object, Void> showCurrentMysteryCard(boolean moveCamera, boolean quick) {
        return new ShowCurrentMysteryCardAction(moveCamera, quick);
    }

    @Override
    public HookableAction<Object, Void> advanceActiveMystery() {
        return new AdvanceActiveMysteryAction();
    }

    @Override
    public Action<Object, Void> advanceCurrentMysteryCard(int amount) {
        return new AdvanceCurrentMysteryCardAction(amount);
    }

    @Override
    public Action<Object, Void> showAncientOneCard() {
        return new ShowAncientOneCardAction();
    }

    @Override
    public Action<Object, Void> spawnRedPins() {
        return new SpawnRedPinsAction();
    }

    @Override
    public Action<Object, Void> justAddRedPins() {
        return new JustAddRedPinsAction();
    }

    @Override
    public HookableAction<Object, GateInfo> spawnGate(boolean quick) {
        return new SpawnGateAction(quick);
    }

    @Override
    public HookableAction<SpawnMonsterData, SpawnMonsterData> spawnMonster(boolean quick) {
        return new SpawnMonsterAction(quick);
    }

    @Override
    public HookableAction<CollectAvailableActionsData, CollectAvailableActionsData> collectAvailableActions() {
        return new CollectAvailableActionsAction();
    }

    @Override
    public HookableAction<CollectAvailableActionsData, CollectAvailableActionsData> filterPerformedActions() {
        return new FilterPerformedActionsAction();
    }

    @Override
    public DirectEventAction<CollectAvailableActionsData> disableOrRemoveActions() {
        return new DisableOrRemoveActionsAction();
    }

    @Override
    public HookableAction<CollectAvailableActionsData, ActionPhaseAction> selectAction() {
        return new SelectActionAction();
    }

    @Override
    public HookableAction<ActionPhaseAction, Void> executeAction() {
        return new ExecuteActionAction();
    }

    @Override
    public HookableAction<Object, AfterActionPerformedData> afterActionPerformed() {
        return new AfterActionPerformedAction();
    }

    @Override
    public HookableAction<AfterActionPerformedData, AfterActionPerformedData> withAfterActionPerformedData() {
        return new WithAfterActionPerformedData();
    }

    @Override
    public Action<Object, Void> initInvestigators() {
        return new InitInvestigatorsAction();
    }

    @Override
    public Action<Object, Object> displayText(String text) {
        return new DisplayTextAction(text);
    }

    @Override
    public HookableAction<ShowCardRequest, ShowCardResponse> showCard() {
        return new ShowCardAction();
    }

    @Override
    public <RD, AD> HookableAction<Question<RD>, Answer<RD, AD>> ask() {
        return new AskAction<>();
    }

    @Override
    public HookableAction<GainCardData, GainedCardData> selectCardToGain() {
        return new SelectCardToGainAction();
    }

    @Override
    public HookableAction<GainedCardData, GainedCardData> registerGainedCard() {
        return new RegisterGainedCardAction();
    }

    @Override
    public Action<Object, PhaseType> changePhase() {
        return new ChangePhaseAction();
    }

    @Override
    public HookableAction<Object, Void> showPhase() {
        return new ShowPhaseAction();
    }

    @Override
    public HookableAction<Object, Void> initPhase() {
        return new InitPhaseAction();
    }

    @Override
    public HookableAction<SelectLocationData, LocationId> selectLocation() {
        return new SelectLocationAction();
    }

    @Override
    public Action<Object, Void> verifyNotDelayed() {
        return new VerifyNotDelayedAction();
    }

    @Override
    public Action<Void, Void> initActionButton() {
        return new AbstractDirectEventAction<>(DirectEvent.INIT_ACTION_BUTTON);
    }

    @Override
    public DirectEventAction<Object> performActionStart() {
        return new PerformActionStartAction();
    }

    @Override
    public Action<Object, Void> hasAnyFreeAction() {
        return new HasAnyFreeActionAction();
    }

    @Override
    public HookableAction<Object, Void> monsterSurge(int monstersCount) {
        return new MonsterSurgeAction(monstersCount);
    }

    @Override
    public HookableAction<SelectSingleGateData, GateInfo> selectSingleGate() {
        return new SelectSingleGateAction();
    }

    @Override
    public Action<Object, Object> checkDefeatedInActionPhase() {
        return new CheckDefeatedInActionPhaseAction();
    }

    @Override
    public Action<Object, Void> clearRedPins() {
        return new ClearRedPinsAction();
    }

    @Override
    public Action<Object, Void> spawnVortex(LocationId locationId) {
        return new SpawnVortexAction(locationId);
    }

    @Override
    public Action<Object, Void> removeVortex(LocationId currentLocationId) {
        return new RemoveVortexAction(currentLocationId);
    }

    @Override
    public Action<Object, Object> askToRateThisGame() {
        return new AskToRateThisGameAction();
    }

    @Override
    public Action<Object, Void> restartGame() {
        return new RestartGameAction();
    }

    @Override
    public Action<Object, Void> sewCard(CardInfo cardInfo) {
        return new SewCardAction(cardInfo);
    }
}
