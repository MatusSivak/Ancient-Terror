package sk.sivak.eldritchhorror.core.view;

import java8.features.function.Supplier;
import rx.Completable;
import rx.Single;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;

import java.util.List;

/**
 * @author msivak
 */
public interface GameView {

    Completable confirmExpeditionLocation(LocationId expeditionTokenLocation);

    void justPlaceExpeditionToken(LocationId expeditionTokenLocation);

    Completable discardExpeditionToken();

    Completable confirmClueLocation(LocationId spawnLocationId, LocationId currentLocationId);

    void justSpawnClue(LocationId spawnLocationId, LocationId currentLocationId);

    Completable confirmGateSpawn(GateInfo gateInfo, OmenColor omenColor);

    void justPlaceGate(GateInfo gateInfo, OmenColor omenColor);

    void showDiscard(List<CardInfo> discardedCards);

    Completable showCurrentMysteryCard(MysteryCardInfo currentMysteryCard, boolean moveCamera);

    Completable showRumorCard(RumorCardInfo rumorCardInfo);

    Completable justShowRumorCard(RumorCardInfo activeRumor);

    Completable justHideRumorCard();

    Completable countdownRumorCard(int amount);

    Completable highlightRumorFailure();

    Completable highlightRumorObjective();

    Completable highlightRumorReckoning();

    Completable advanceCurrentMysteryCard(MysteryCardInfo currentMysteryCard, int amount);

    Completable confirmMonsterSpawn(MonsterInfo monsterInfo);

    void justPlaceMonster(MonsterInfo monsterInfo);

    void showReserve(List<AssetInfo> reserve);

    Completable showAncientOneCard(AncientOneInfo ancientOneInfo);

    void updateDoom(int doom);

    void updateOmen(OmenInfo omenInfo);

    Single<ActionPhaseAction> selectAction(List<ActionPhaseAction> actionPhaseActions);

    Single<LocationInfo.Connection> selectTravelLocation(LocationId currentLocationId, List<LocationInfo.Connection> connections, boolean optional);

    Completable travelToLocation(LocationId locationFrom, LocationId locationTo);

    Completable initInvestigator(InvestigatorBasics selectedInvestigators);

    void justPlaceInvestigator(InvestigatorBasics investigatorBasics);

    Single<LocationInfo.Connection> selectTicketTravelLocation(LocationId currentLocationId, List<LocationInfo.Connection> connections);

    void loseTicket(PathType input);

    Single<PathType> selectTravelTicket();

    Completable gainTravelTicket(PathType ticketType);

    Completable discardTicket(PathType pathType);

    Completable gainHealth(int amount);

    Completable gainSanity(int amount);

    Completable justRest();

    Completable justFocus();

    Completable gainFocus(int amount);

    Completable gainClueFromPool();

    Completable gainClueFromSpace(LocationId locationId);

    Single<Stat> improveSkill(Stat skillRestriction);

    Completable showImproveSkillTable();

    Completable hideImproveSkillTable();

    Completable updateHud();

    void displayText(String text);

    Single<ShowCardResponse> showCard(ShowCardRequest showCardRequest);

    Completable sewCard(CardInfo cardInfo);

    Completable loseFocus(Integer input);

    Completable loseHealth(Integer input);

    Completable loseSanity(Integer input);

    Completable loseClue(Integer input);

    Completable gainCard(CardInfo cardInfo);

    <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question);

    Completable showPhase(PhaseType input);

    Completable showActiveInvestigator(boolean displayTransition, boolean lostInTimeAndSpace);

    Single<InvestigatorId> selectInvestigator(List<InvestigatorId> disabledInvestigators, String title);

    Single<TradeData> trade(TradeData input);

    Completable discardClue(LocationId spawnLocationId, LocationId currentLocationId);

    void displayInvestigatorPassport(InvestigatorBasics investigatorBasics, List<CardInfo> cards);

    Completable showRedPinsLocation(List<LocationId> pinLocations);

    Completable showStormLocation(String stormId, LocationId locationId);

    void justAddRedPins(List<LocationId> pinLocations);

    void justSpawnStorm(String stormId, LocationId locationId);

    void clearRedPins(List<LocationId> pinLocations);

    void clearStorm(String rumorId);

    Completable displayTrackWidget();

    Single<LocationInfo.Connection> selectLocation(List<LocationId> locations);

    void showHud();

    void hideCityInfoLabels();

    void showCityInfoLabels();

    void enableDiscardButton();


    Completable showDelayedAnimation(InvestigatorId investigatorId);

    void justAddDelayedAnimation(InvestigatorId investigatorId);

    Completable hideDelayedAnimation(InvestigatorId investigatorId);

    Completable hideSelectEncounterTable();

    Completable moveCameraToLocation(LocationId locationId);

    Completable moveClue(LocationId spawnLocationId, LocationId currentLocationId, LocationId targetLocationId);

    Completable removeInvestigator(InvestigatorId investigatorId, LocationId locationId);

    Completable showInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId);

    Completable hideInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId);

    Completable showWholeWorld();

    Completable showLocationBackground(LocationId locationId, List<InvestigatorBasics> investigatorsAtLocation);

    Completable showLocationTypeBackground(LocationType locationType, List<InvestigatorBasics> investigatorsAtLocation);

    Completable showResearchBackground(LocationType locationType, List<InvestigatorBasics> investigatorsAtLocation);

    Completable showCombatBackground(List<InvestigatorBasics> investigatorsAtLocation);

    Completable hideBackground();


    Completable showWorldBackground();

    Completable showCustomBackground(String backgroundId, List<InvestigatorBasics> investigatorsAtLocation);

    Completable closeGate(LocationId location);

    void swapInvestigatorAndGateLayer();

    void updateNoiseColor(OmenColor omenColor);

    void updateNoiseIntensity(int doom);

    void updateGatesTransparency(OmenColor omenColor);

    Single<GateInfo> selectSingleGate(List<GateInfo> gates, String titleText, String hideText);

    Single<Stat> loseImprovement(Stat stat, int amount);

    Completable devourInvestigator(InvestigatorId investigatorId);

    Completable defeatInvestigator(InvestigatorId investigatorId, boolean health);

    Completable loadDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId, boolean defeatedByHealth);

    Completable removeDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId);

    Single<InvestigatorInfo[]> selectReplacingInvestigator(InvestigatorId removedInvestigator,
                                                           List<InvestigatorInfo> availableInvestigators,
                                                           Supplier<List<InvestigatorInfo>> initInvestigatorsAction);

    void hideButtonsAndInvestigatorHud(boolean hideDoomOmenTrack);

    void unsetActiveInvestigator();

    void displayMonsterCard(MonsterInfo monsterInfo, Action0 onClickAction, Action0 onDisplayAction);

    void hideMonsterCard(MonsterInfo monsterInfo, Action0 onHideAction);

    void startGame();

    void setClearQueueAction(Runnable input);

    void restartGame();


    void updateRumorButtonVisibility(boolean visible);

    Completable showRumorCard(List<RumorCardInfo> activeRumors);

    Completable spawnVortex(LocationId locationId);

    void justAddVortex(LocationId locationId);

    Completable removeVortex(LocationId locationId);


    void postRunnable(Runnable runnable);

    Completable increaseAncientOnePower(int increment);
}
