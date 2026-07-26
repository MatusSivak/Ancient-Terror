package sk.sivak.eldritchhorror.core.eventtype;

import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.data.*;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainHealthData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainSanityData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DealDamageToMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.*;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainFocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.MoveTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.List;

/**
 * @author msivak
 */
public enum BeforeAfterEvent {

    SPAWN_CLUES(Integer.class, List.class),
    SPAWN_GATES(Object.class, GateInfo.class),
    SPAWN_MONSTER(SpawnMonsterData.class, SpawnMonsterData.class),

    FIND_ACTIONS(CollectAvailableActionsData.class, CollectAvailableActionsData.class),
    FILTER_PERFORMED_ACTIONS(CollectAvailableActionsData.class, CollectAvailableActionsData.class),
    SELECT_ACTION(CollectAvailableActionsData.class, ActionPhaseAction.class),
    EXECUTE_ACTION(ActionPhaseAction.class, Void.class),
    AFTER_ACTION_PERFORMED(Object.class, AfterActionPerformedData.class),
    WITH_AFTER_ACTION_PERFORMED_DATA(Object.class, AfterActionPerformedData.class),

    CONFIRM_FOUND_ASSETS(TestRequestData.class, Boolean.class),
    TEST_REQUEST_TO_TEST_RESPONSE(TestRequestData.class, TestResponseData.class),

    GAIN_ASSET_FROM_DECK(GainAssetFromDeckData.class, GainAssetFromDeckData.class),
    GAIN_ARTIFACT_FROM_DECK(GainArtifactFromDeckData.class, GainArtifactFromDeckData.class),
    GAIN_SPELL_FROM_DECK(GainSpellFromDeckData.class, GainSpellFromDeckData.class),
    GAIN_CONDITION_FROM_DECK(GainConditionFromDeckData.class, GainConditionFromDeckData.class),
    DISCARD_ASSET_FROM_INVESTIGATOR(DiscardAssetFromInvestigatorData.class, DiscardAssetFromInvestigatorData.class),
    DISCARD_ARTIFACT_FROM_INVESTIGATOR(DiscardArtifactFromInvestigatorData.class, DiscardArtifactFromInvestigatorData.class),
    DISABLE_CARD(DisableCardData.class, DisableCardData.class),
    ENABLE_CARD(EnableCardData.class, EnableCardData.class),
    DISCARD_CONDITION_FROM_INVESTIGATOR(DiscardConditionFromInvestigatorData.class, DiscardConditionFromInvestigatorData.class),
    DISCARD_SPELL_FROM_INVESTIGATOR(DiscardSpellFromInvestigatorData.class, DiscardSpellFromInvestigatorData.class),
    SELECT_TRAVEL_LOCATION(TravelData.class, LocationInfo.Connection.class),
    SELECT_TRAVEL_TICKET_LOCATION(Object.class, LocationInfo.Connection.class),
    LOSE_TICKET(LocationInfo.Connection.class, LocationInfo.Connection.class),
    TRAVEL_TO_LOCATION(LocationInfo.Connection.class, InvestigatorId.class),

    // TRAVEL
    SELECT_TRAVEL_TICKET(Void.class, PathType.class),
    GAIN_TRAVEL_TICKET(PathType.class, Void.class),
    DISCARD_TRAVEL_TICKET(PathType.class, PathType.class),

    // REST
    CREATE_REST_DATA(Object.class, RestData.class),
    REST(RestData.class, RestData.class),

    // FOCUS
    CREATE_FOCUS_DATA(Object.class, FocusData.class),
    FOCUS(FocusData.class, FocusData.class),

    // INVESTIGATOR ACTIONS
    GAIN_CLUE(GainClueData.class, GainClueData.class),
    GAIN_FOCUS(GainFocusData.class, GainFocusData.class),

    //TEST
    READ_STAT(TestData.class, TestData.class),
    CONFIRM_TEST(TestData.class, TestData.class),
    CALCULATE_DICE_POOL(TestData.class, TestData.class),
    ROLL_DICES(TestData.class, TestData.class),
    FIND_MIN_SUCCESS_DICE_VALUE(PrimitiveWrapper.class, PrimitiveWrapper.class),
    EVALUATE_ROLLED_DICES(TestData.class, TestData.class),
    SHOW_ROLLED_DICES(TestData.class, TestData.class),
    CONFIRM_TEST_RESULT(TestData.class, TestData.class),
    REROLL_DIE(CountRerollDiceData.class, TestData.class),

    COUNT_REROLL_DICE(CountRerollDiceData.class, CountRerollDiceData.class),

    REROLL_USING_FOCUS(RerollUsingData.class, RerollData.class),
    REROLL_USING_CLUE(RerollUsingData.class, RerollData.class),

    ADD_ONE_TO_DIE_RESULT(AddOneToDieResultData.class, AddOneToDieResultData.class),

    ROLL_AND_SHOW_DIE(RollData.class, Integer.class),

    // GAIN HEALTH & SANITY
    GAIN_SANITY(GainSanityData.class, GainSanityData.class),
    GAIN_HEALTH(GainHealthData.class, GainHealthData.class),

    LOSE_HEALTH(LoseTokenData.class, LoseTokenData.class),
    LOSE_SANITY(LoseTokenData.class, LoseTokenData.class),
    SPEND_HEALTH(LoseTokenData.class, LoseTokenData.class),
    SPEND_SANITY(LoseTokenData.class, LoseTokenData.class),
    LOSE_FOCUS(LoseTokenData.class, LoseTokenData.class),
    LOSE_CLUE(LoseTokenData.class, LoseTokenData.class),
    DISCARD_CLUE(LocationId.class, LocationId.class),

    SHOW_CARD(ShowCardRequest.class, ShowCardResponse.class),
    SHOW_RESERVE_DRAG_AND_DROP(ShowReserveDragAndDropInput.class, ShowReserveDragAndDropOutput.class),
    REGISTER_SELECTED_ASSETS(ShowReserveDragAndDropOutput.class, ShowReserveDragAndDropOutput.class),
    REPLACE_CARD_IN_RESERVE(ShowReserveDragAndDropOutput.class, ShowReserveDragAndDropOutput.class),
    REFILL_RESERVE(Object.class, Void.class),
    ASK(Question.class, Object.class),

    SELECT_CARD_TO_GAIN(GainCardData.class, GainedCardData.class),
    REGISTER_GAINED_CARD(GainedCardData.class, GainedCardData.class),
    RETREAT_DOOM(Object.class, Object.class),
    ADVANCE_DOOM(Object.class, Void.class),
    ADVANCE_OMEN(Object.class, OmenInfo.class),
    SPEND(SpendData.class, SpendData.class),
    CHANGE_PHASE(Object.class, PhaseType.class),
    SHOW_PHASE(Object.class, Void.class),
    INIT_PHASE(Object.class, Void.class),
    SELECT_INVESTIGATOR(InvestigatorRestriction.class, InvestigatorId.class),
    ADD_FREE_ACTION(Object.class, Void.class),
    REMOVE_PERFORMED_ACTION(ActionPhaseAction.class, ActionPhaseAction.class),
    IMPROVE_SKILL(Stat.class, Stat.class),
    LOSE_IMPROVEMENT(LoseImprovementData.class, LoseImprovementData.class),
    BECOME_DELAYED(DelayedData.class, DelayedData.class),
    END_DELAYED(InvestigatorId.class, InvestigatorId.class),

    FIND_INVESTIGATORS_FOR_TRADE(Object.class, List.class),
    SELECT_INVESTIGATOR_FOR_TRADE(List.class, InvestigatorId.class),
    COLLECT_ITEMS_FOR_TRADE(InvestigatorId.class, TradeData.class),
    CONFIRM_TRADE(TradeData.class, TradeData.class),
    TRANSFER_TRADED_ITEMS(TradeData.class, TradeData.class),
    SELECT_SINGLE_CARD(SelectCardData.class, CardInfo.class),
    SELECT_SINGLE_MONSTER(SelectMonsterData.class, MonsterInfo.class),
    MOVE_MONSTER(MoveMonsterData.class, MoveMonsterData.class),
    MOVE_CLUE(MoveTokenData.class, MoveTokenData.class),

    DEAL_DAMAGE_TO_MONSTER(DealDamageToMonsterData.class, DealDamageToMonsterData.class),
    DEFEAT_MONSTER(DefeatMonsterData.class, DefeatMonsterData.class),
    DISCARD_MONSTER(MonsterInfo.class, MonsterInfo.class),
    SELECT_LOCATION(SelectLocationData.class, LocationId.class),
    SELECT_SINGLE_GATE(SelectSingleGateData.class, GateInfo.class),

    DISABLE_PERFORMED_ENCOUNTERS(AvailableEncounters.class, AvailableEncounters.class),
    DISABLE_EPIC_COMBAT_ENCOUNTERS(AvailableEncounters.class, AvailableEncounters.class),
    DISABLE_NON_COMBAT_ENCOUNTERS(AvailableEncounters.class, AvailableEncounters.class),

    TW_DISPLAY_BUTTONS(Object.class, Integer.class),

    LOST_IN_TIME_AND_SPACE(InvestigatorId.class, InvestigatorId.class),
    SHOW_LOST_IN_TIME_AND_SPACE(InvestigatorId.class, InvestigatorId.class),
    HIDE_LOST_IN_TIME_AND_SPACE(InvestigatorId.class, InvestigatorId.class),

    SHOW_COMBAT_OVERVIEW(CombatData.class, CombatData.class ),
    HIDE_COMBAT_TABLE(Object.class, Void.class),
    SHOW_COMBAT_TABLE(CombatData.class, CombatData.class),
    AFTER_DAMAGE_CHECK(CombatData.class, CombatData.class),
    AFTER_HORROR_CHECK(CombatData.class, CombatData.class),
    UPDATE_MONSTER_HORROR(CombatData.class, CombatData.class),
    UPDATE_MONSTER_DAMAGE(CombatData.class, CombatData.class),

    SHOW_ACTIVE_INVESTIGATOR(InvestigatorId.class, Void.class),

    END_OF_COMBAT_EVENT(Object.class, CombatData.class),
    CLOSE_GATE(Object.class, CloseGateData.class),
    DISCARD_GATE(Object.class, DiscardGateData.class),
    DESTROY_INVESTIGATOR_HEALTH(CombatData.class, CombatData.class),
    DESTROY_MONSTER_HEALTH(CombatData.class, CombatData.class),
    END_OF_MOVE_SPACES(Object.class, Void.class),
    ADVANCE_DOOM_BY_CURRENT_OMEN(OmenInfo.class, Void.class),
    MONSTER_SURGE(Object.class, Void.class),
    SPEND_CLUES_AS_GROUP(Integer.class, Boolean.class),
    DEVOUR_INVESTIGATOR(Object.class, Void.class),
    DEFEAT_INVESTIGATOR(Object.class, Void.class),
    END_OF_ENCOUNTER(Object.class, Object.class),
    DRAW_MYTHOS_CARD(Object.class, Void.class),
    RESOLVE_CURRENT_MYSTERY(Object.class, Boolean.class),
    PREPARE_NEW_ROUND(Object.class, Void.class),
    ADVANCE_ACTIVE_MYSTERY(Object.class,Void.class ),
    SPAWN_INVESTIGATOR(Object.class, InvestigatorId.class),
    REPLACE_INVESTIGATORS(Object.class, Void.class),
    ENCOUNTER_READ_INPUT(Object.class, String.class);

    private Class<?> inputClass;
    private Class<?> outputClass;

    BeforeAfterEvent(Class<?> inputClass, Class<?> outputClass) {
        this.inputClass = inputClass;
        this.outputClass = outputClass;
    }

    public Class<?> getInputClass() {
        return inputClass;
    }

    public Class<?> getOutputClass() {
        return outputClass;
    }
}
