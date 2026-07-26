package sk.sivak.eldritchhorror.core.controller;

import java8.features.function.Supplier;
import rx.Completable;
import rx.Single;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
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
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;

import java.util.Collection;
import java.util.List;

/**
 * @author msivak
 */
public interface GameController {

    Completable confirmExpeditionLocation();

    void justPlaceExpeditionToken();

    Completable discardExpeditionToken();

    Completable initReplacingInvestigator(InvestigatorId investigatorId, LocationId locationId);

    Completable confirmClueLocation(LocationId spawnLocationId, LocationId currentLocationId);

    void justSpawnClue(LocationId spawnLocationId, LocationId currentLocationId);

    Completable showCurrentMysteryCard(boolean moveCamera);

    void updateRumorButtonVisibility();

    Completable showRumorCard(RumorCardInfo activeRumor);

    Completable showRumorCard();

    Completable justShowRumorCard(RumorCardInfo activeRumor);

    Completable justHideRumorCard();

    Completable countdownRumorCard(int amount);

    Completable advanceCurrentMysteryCard(int amount);

    Completable showAncientOneCard();

    Completable confirmGateSpawn(GateInfo gateInfo);

    void justPlaceGate(GateInfo gateInfo);

    Completable confirmMonsterSpawn(MonsterInfo monsterInfo);

    void justPlaceMonster(MonsterInfo monster);

    void showReserve();

    void showDiscard();

    void updateDoom();

    void updateOmen();

    Single<ActionPhaseAction> selectAction(List<ActionPhaseAction> actionPhaseActions);

    Completable travelToLocation(LocationId input);

    Completable initInvestigators();

    InvestigatorBasics getInvestigatorBasics();

    List<InvestigatorBasics> getInvestigatorsAtLocation(LocationId locationTo);

    List<InvestigatorId> getDefeatedInvestigatorsAtLocation(LocationId locationTo);

    Single<LocationInfo.Connection> selectTravelLocation(boolean optional, List<LocationInfo.Connection> connectionRestrictions);

    Single<LocationInfo.Connection> selectLocation(List<LocationId> locations);

    Single<LocationInfo.Connection> selectTicketTravelLocation();

    void loseTicket(PathType input);

    Single<PathType> selectTravelTicket();

    Completable gainTravelTicket(PathType ticketType);

    Completable discardTicket(PathType train);

    Completable gainHealth(int amount);

    Completable gainSanity(int amount);

    Completable justRest();

    Completable justFocus();

    Completable gainFocus(int amount);

    Completable gainClueFromPool();

    Completable gainClueFromSpace(LocationId locationId);

    Single<Stat> improveSkill(Stat skillRestriction);

    Single<Stat> loseImprovement(Stat stat, int amount);

    Completable showImproveSkillTable();

    Completable hideImproveSkillTable();

    Single<ShowCardResponse> showCard(ShowCardRequest showCardRequest);

    Completable sewCard(CardInfo cardInfo);

    void displayText(String text);

    Completable loseFocus(Integer input);

    Completable loseHealth(Integer input);

    Completable loseSanity(Integer input);

    Completable loseClue(Integer input);

    <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question);

    Completable gainCard(CardInfo cardInfo);

    Completable showPhase(PhaseType input);

    Completable showActiveInvestigator(boolean displayTransition, boolean lostInTimeAndSpace);

    Completable updateHud();

    Single<InvestigatorId> selectInvestigator(List<InvestigatorId> list, String title);

    Single<TradeData> trade(TradeData input);

    Completable discardClue(LocationId spawnLocationId, LocationId currentLocationId);

    void displayInvestigatorPassport();

    void displayInvestigatorPassport(InvestigatorId investigatorId);

    Completable spawnRedPins();

    Completable spawnStorm(String stormId, LocationId locationId);

    void justAddRedPins();

    void justSpawnStorm(String stormId, LocationId locationId);

    void clearRedPins();

    void clearStorm(String rumorId);

    void displayMonsterCard(MonsterInfo monsterInfo, Action0 onClickAction, Action0 onDisplayAction);

    void hideMonsterCard(MonsterInfo monsterInfo, Action0 onHideAction);

    Completable displayTrackWidget();

    void showHud();

    void hideCityInfoLabels();

    void showCityInfoLabels();

    void enableDiscardButton();

    Single<String> selectEncounter(Collection<EncounterButtonData> encounterButtonDataList);

    Completable showDelayedAnimation(InvestigatorId investigatorId);

    void justAddDelayedAnimation(InvestigatorId investigatorId);

    Completable hideDelayedAnimation(InvestigatorId input);

    Completable hideSelectEncounterTable();

    Completable moveCameraToLocation(LocationId locationId);

    Completable moveClue(LocationId spawnLocationId, LocationId currentLocationId, LocationId targetLocationId);

    Completable removeInvestigator(InvestigatorId investigatorId, LocationId locationId);

    Completable showInvestigatorLostInTimeAndSpace(InvestigatorId input);

    Completable hideInvestigatorLostInTimeAndSpace(InvestigatorId input);

    Completable spawnInvestigator(InvestigatorId investigatorId, LocationId locationId);

    void justPlaceInvestigator(InvestigatorId investigatorId, LocationId currentLocationId);

    Completable showWholeWorld();

    Completable showLocationBackground(LocationId locationId);

    Completable showLocationTypeBackground(LocationType locationType);

    Completable showResearchBackground(LocationType locationType);

    Completable showCombatBackground();

    Completable showWorldBackground();

    Completable hideBackground();

    Completable showCustomBackground(String backgroundId);

    Completable closeGate(LocationId location);

    void swapInvestigatorAndGateLayer();

    Single<GateInfo> selectSingleGate(List<GateInfo> gates, String titleText, String hideText);

    Completable devourInvestigator(InvestigatorId investigatorId);

    Single<InvestigatorInfo[]> selectReplacingInvestigator(InvestigatorId removedInvestigator, Supplier<List<InvestigatorInfo>> initInvestigatorsAction);

    Completable defeatInvestigator(InvestigatorId investigatorId, boolean health);

    Completable loadDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId, boolean defeatedByHealth);

    Completable removeDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId);

    String getPreviousBackgroundName();

    void hideButtonsAndInvestigatorHud(boolean hideDoomOmenTrack);

    void unsetActiveInvestigator();

    void startGame();

    void setClearQueueAction(Runnable input);

    void restartGame();

    Completable highlightRumorFailure();

    Completable highlightRumorObjective();

    Completable highlightRumorReckoning();

    Completable spawnVortex(LocationId locationId);

    Completable removeVortex(LocationId locationId);

    void justAddVortex(LocationId locationId);

    void postRunnable(Runnable runnable);

    Completable increaseAncientOnePower(int increment);

    AncientOneInfo getAncientOneInfo();
}
