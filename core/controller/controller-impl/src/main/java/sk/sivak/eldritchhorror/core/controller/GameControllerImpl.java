package sk.sivak.eldritchhorror.core.controller;

import java8.features.function.Supplier;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
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
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.model.AncientOneRead;
import sk.sivak.eldritchhorror.core.model.ArtifactsDeckRead;
import sk.sivak.eldritchhorror.core.model.AssetDeckRead;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;
import sk.sivak.eldritchhorror.core.model.CluePoolRead;
import sk.sivak.eldritchhorror.core.model.ConditionsDeckRead;
import sk.sivak.eldritchhorror.core.model.DoomTrackRead;
import sk.sivak.eldritchhorror.core.model.ExpeditionDeckRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.model.LocationMapRead;
import sk.sivak.eldritchhorror.core.model.MonsterCupRead;
import sk.sivak.eldritchhorror.core.model.MysteryDeckRead;
import sk.sivak.eldritchhorror.core.model.OmenTrackRead;
import sk.sivak.eldritchhorror.core.model.RumorsRead;
import sk.sivak.eldritchhorror.core.model.SpellsDeckRead;
import sk.sivak.eldritchhorror.core.view.EncounterView;
import sk.sivak.eldritchhorror.core.view.GameView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static java8.features.stream.Stream.map;

/**
 * @author msivak
 */
public class GameControllerImpl implements GameController {

    private GameView view;
    private ExpeditionDeckRead expeditionDeck;
    private MysteryDeckRead mysteryDeck;
    private AssetDeckRead assetDeck;
    private ConditionsDeckRead conditionsDeck;
    private SpellsDeckRead spellsDeck;
    private RumorsRead rumors;
    private ArtifactsDeckRead artifactsDeck;
    private OmenTrackRead omenTrack;
    private DoomTrackRead doomTrackRead;
    private BackgroundModelRead backgroundModel;
    private InvestigatorsRead investigators;
    private LocationMapRead locationMap;
    private CluePoolRead cluePoolRead;
    private AncientOneRead ancientOne;
    private EncounterView encounterView;
    private MonsterCupRead monsterCup;

    public void setMonsterCup(MonsterCupRead monsterCup) {
        this.monsterCup = monsterCup;
    }

    public void setBackgroundModel(BackgroundModelRead backgroundModel) {
        this.backgroundModel = backgroundModel;
    }

    public void setCluePoolRead(CluePoolRead cluePoolRead) {
        this.cluePoolRead = cluePoolRead;
    }

    public void setLocationMap(LocationMapRead locationMap) {
        this.locationMap = locationMap;
    }

    public void setInvestigators(InvestigatorsRead investigators) {
        this.investigators = investigators;
    }

    public void setOmenTrack(OmenTrackRead omenTrack) {
        this.omenTrack = omenTrack;
    }

    public void setDoomTrackRead(DoomTrackRead doomTrackRead) {
        this.doomTrackRead = doomTrackRead;
    }

    public void setAssetDeck(AssetDeckRead assetDeck) {
        this.assetDeck = assetDeck;
    }

    public void setConditionsDeck(ConditionsDeckRead conditionsDeck) {
        this.conditionsDeck = conditionsDeck;
    }

    public void setSpellsDeck(SpellsDeckRead spellsDeck) {
        this.spellsDeck = spellsDeck;
    }

    public void setRumors(RumorsRead rumors) {
        this.rumors = rumors;
    }

    public void setMysteryDeck(MysteryDeckRead mysteryDeck) {
        this.mysteryDeck = mysteryDeck;
    }

    public void setExpeditionDeck(ExpeditionDeckRead expeditionDeck) {
        this.expeditionDeck = expeditionDeck;
    }

    public void setAncientOne(AncientOneRead ancientOne) {
        this.ancientOne = ancientOne;
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public void setArtifactsDeck(ArtifactsDeckRead artifactsDeck) {
        this.artifactsDeck = artifactsDeck;
    }

    public void setEncounterView(EncounterView encounterView) {
        this.encounterView = encounterView;
    }

    @Override
    public Completable confirmExpeditionLocation() {
        return view.confirmExpeditionLocation(expeditionDeck.getExpeditionTokenLocation());
    }

    @Override
    public void justPlaceExpeditionToken() {
        view.justPlaceExpeditionToken(expeditionDeck.getExpeditionTokenLocation());
    }

    @Override
    public Completable discardExpeditionToken() {
        return view.discardExpeditionToken();
    }

    @Override
    public Completable initInvestigators() {
        List<InvestigatorBasics> investigatorBasicss = new LinkedList<>();
        for (InvestigatorRead investigatorRead : investigators.getOnBoardInvestigators()) {
            investigatorBasicss.add(new InvestigatorBasicsImpl(investigatorRead.getInfo().getInvestigatorId(),
                    investigatorRead.getCurrentLocationId()));
        }

        Iterator<InvestigatorBasics> iterator = investigatorBasicss.iterator();
        return Completable.create(onSub -> initSingleInvestigator(iterator, onSub));
    }

    @Override
    public InvestigatorBasics getInvestigatorBasics() {
        return getInvestigatorBasics(investigators.getActiveInvestigatorId());
    }

    private InvestigatorBasics getInvestigatorBasics(InvestigatorId investigatorId) {
        InvestigatorRead investigator;
        if (Stream.anyMatch(investigators.getDefeatedInvestigators(), di -> di.getInfo().getInvestigatorId() == investigatorId)) {
            investigator = Stream.findFirstOrException(investigators.getDefeatedInvestigators(), di -> di.getInfo().getInvestigatorId() == investigatorId);
        } else {
            investigator = investigators.getInvestigator(investigatorId);
        }

        InvestigatorBasicsImpl investigatorBasics = new InvestigatorBasicsImpl(investigatorId,
                investigator.getCurrentLocationId());
        investigatorBasics.setCurrentHealth(investigator.getCurrentHealth());
        investigatorBasics.setMaxHealth(investigator.getInfo().getMaxHealth());
        investigatorBasics.setCurrentSanity(investigator.getCurrentSanity());

        investigatorBasics.setClues(cluePoolRead.getClueCount(investigatorId));
        investigatorBasics.setFocusTokens(investigator.getFocusTokens());
        investigatorBasics.setMaxSanity(investigator.getInfo().getMaxSanity());
        investigatorBasics.setInvestigatorName(investigator.getInfo().getInvestigatorName());
        investigatorBasics.setActionText(investigator.getInfo().getActionText());
        investigatorBasics.setAbilityText(investigator.getInfo().getAbilityText());
        investigatorBasics.setLostInTimeAndSpace(investigator.isLostInTimeAndSpace());
        investigatorBasics.setBio(investigator.getInfo().getBio());
        fillBaseAndBonusStats(investigator, investigatorBasics);
        for (int i = 0; i < investigator.getTrainTickets(); i++) {
            investigatorBasics.getTickets().add(PathType.TRAIN);
        }
        for (int i = 0; i < investigator.getShipTickets(); i++) {
            investigatorBasics.getTickets().add(PathType.SHIP);
        }

        return investigatorBasics;
    }

    private void fillBaseAndBonusStats(InvestigatorRead active, InvestigatorBasicsImpl investigatorBasics) {
        for (Stat stat : Stat.values()) {
            investigatorBasics.setStatValue(stat, active.getInfo().getBaseStat(stat), active.getStatBonus(stat));
        }
    }

    private void initSingleInvestigator(Iterator<InvestigatorBasics> iterator, CompletableSubscriber subscriber) {
        if (iterator.hasNext()) {
            InvestigatorBasics next = iterator.next();
            view.initInvestigator(next).subscribe(() -> initSingleInvestigator(iterator, subscriber));
        } else {
            subscriber.onCompleted();
        }
    }

    @Override
    public Completable initReplacingInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return view.initInvestigator(new InvestigatorBasicsImpl(investigatorId, locationId));
    }

    @Override
    public Completable confirmClueLocation(LocationId spawnLocationId, LocationId currentLocationId) {
        return view.confirmClueLocation(spawnLocationId, currentLocationId);
    }

    @Override
    public void justSpawnClue(LocationId spawnLocationId, LocationId currentLocationId) {
        view.justSpawnClue(spawnLocationId, currentLocationId);
    }

    @Override
    public Completable showCurrentMysteryCard(boolean moveCamera) {
        return view.showCurrentMysteryCard(mysteryDeck.getCurrentMysteryCard(), moveCamera);
    }

    @Override
    public Completable showRumorCard(RumorCardInfo activeRumor) {
        return view.showRumorCard(activeRumor);
    }

    @Override
    public Completable showRumorCard() {
        if (rumors.getActiveRumors().size() == 0) {
            return Completable.complete();
        } else if (rumors.getActiveRumors().size() == 1) {
            return view.showRumorCard(rumors.getActiveRumors().get(0));
        } else {
            return view.showRumorCard(rumors.getActiveRumors());
        }
    }

    @Override
    public Completable justShowRumorCard(RumorCardInfo activeRumor) {
        return view.justShowRumorCard(activeRumor);
    }

    @Override
    public Completable justHideRumorCard() {
        return view.justHideRumorCard();
    }

    @Override
    public Completable countdownRumorCard(int amount) {
        return view.countdownRumorCard(amount);
    }

    @Override
    public Completable highlightRumorFailure() {
        return view.highlightRumorFailure();
    }

    @Override
    public Completable highlightRumorObjective() {
        return view.highlightRumorObjective();
    }

    @Override
    public Completable highlightRumorReckoning() {
        return view.highlightRumorReckoning();
    }

    @Override
    public Completable spawnVortex(LocationId locationId) {
        return view.spawnVortex(locationId);
    }

    @Override
    public void justAddVortex(LocationId locationId) {
        view.justAddVortex(locationId);
    }

    @Override
    public Completable removeVortex(LocationId locationId) {
        return view.removeVortex(locationId);
    }

    @Override
    public Completable advanceCurrentMysteryCard(int amount) {
        return view.advanceCurrentMysteryCard(mysteryDeck.getCurrentMysteryCard(), amount);
    }

    @Override
    public Completable showAncientOneCard() {
        if (ancientOne.getAncientOneInfo() != null &&
                ancientOne.getAncientOneInfo().getAncientOneId() == AncientOneId.SHUB_NIGGURATH &&
                !ancientOne.getAncientOneInfo().isAwaken()) {
            ancientOne.getAncientOneInfo().setPower(monsterCup.getMonsters().size());
        } else if (ancientOne.getAncientOneInfo() != null &&
                ancientOne.getAncientOneInfo().getAncientOneId() == AncientOneId.SHUB_NIGGURATH &&
                ancientOne.getAncientOneInfo().isAwaken()) {
            ancientOne.getAncientOneInfo().setPower(monsterCup.getMonstersAtLocation(LocationId.THE_HEART_OF_AFRICA).size()-1);
        }

        return view.showAncientOneCard(ancientOne.getAncientOneInfo());
    }

    @Override
    public Completable confirmGateSpawn(GateInfo gateInfo) {
        return view.confirmGateSpawn(gateInfo, omenTrack.getCurrentOmen().getOmenColor());
    }

    @Override
    public void justPlaceGate(GateInfo gateInfo) {
        view.justPlaceGate(gateInfo, omenTrack.getCurrentOmen().getOmenColor());
    }

    @Override
    public Completable confirmMonsterSpawn(MonsterInfo monsterInfo) {
        return view.confirmMonsterSpawn(monsterInfo);
    }


    @Override
    public void justPlaceMonster(MonsterInfo monster) {
        view.justPlaceMonster(monster);
    }

    @Override
    public void showReserve() {
        view.showReserve(assetDeck.getReserve());
    }

    @Override
    public void showDiscard() {
        LinkedList<CardInfo> discardedCards = new LinkedList<>();
        discardedCards.addAll(assetDeck.getDiscardPile());
        discardedCards.addAll(artifactsDeck.getDiscardPile());
        view.showDiscard(discardedCards);
    }

    @Override
    public void updateDoom() {
        view.updateDoom(doomTrackRead.getCurrentDoom());
    }

    @Override
    public void updateOmen() {
        view.updateOmen(omenTrack.getCurrentOmen());
    }

    @Override
    public Single<ActionPhaseAction> selectAction(List<ActionPhaseAction> actionPhaseActions) {
//        return Single.create(onSub -> {});
        return view.selectAction(actionPhaseActions);
    }

    @Override
    public Single<LocationInfo.Connection> selectTravelLocation(boolean optional, List<LocationInfo.Connection> connectionRestrictions) {
        LocationId currentLocationId = investigators.getActiveInvestigator().getCurrentLocationId();
        LocationInfo locationInfo = locationMap.getLocationInfo(currentLocationId);

        ArrayList<LocationInfo.Connection> connectionsCopy = new ArrayList<>(locationInfo.getConnections());
        if (connectionRestrictions != null) {
            IterableUtils.removeIf(connectionsCopy, connection -> !connectionRestrictions.contains(connection));
        }
        return view.selectTravelLocation(currentLocationId, connectionsCopy, optional);
    }

    @Override
    public Single<LocationInfo.Connection> selectLocation(List<LocationId> locations) {
        return view.selectLocation(locations);
    }

    @Override
    public Single<LocationInfo.Connection> selectTicketTravelLocation() {
        InvestigatorRead activeInvestigator = investigators.getActiveInvestigator();
        LocationId currentLocationId = activeInvestigator.getCurrentLocationId();
        LocationInfo locationInfo = locationMap.getLocationInfo(currentLocationId);

        List<LocationInfo.Connection> connections = new LinkedList<>();
        if (activeInvestigator.getShipTickets() > 0) {
            connections.addAll(locationInfo.getShipConnections());
        }
        if (activeInvestigator.getTrainTickets() > 0) {
            connections.addAll(locationInfo.getTrainConnections());
        }
        return view.selectTicketTravelLocation(currentLocationId, connections);
    }

    @Override
    public void loseTicket(PathType input) {
        view.loseTicket(input);
    }

    @Override
    public Single<PathType> selectTravelTicket() {
        InvestigatorRead activeInvestigator = investigators.getActiveInvestigator();
        LocationInfo locationInfo = locationMap.getLocationInfo(activeInvestigator.getCurrentLocationId());
        boolean trainConnection = locationInfo.getTrainConnections().size() > 0;
        boolean shipConnection = locationInfo.getShipConnections().size() > 0;
        if (trainConnection && shipConnection) {
            return view.selectTravelTicket();
        } else if (trainConnection) {
            return Single.just(PathType.TRAIN);
        } else if (shipConnection) {
            return Single.just(PathType.SHIP);
        } else {
            throw new IllegalArgumentException("This place has no train or ship connections");
        }
    }

    @Override
    public Completable gainTravelTicket(PathType ticketType) {
        return view.gainTravelTicket(ticketType);
    }

    @Override
    public Completable discardTicket(PathType pathType) {
        return view.discardTicket(pathType);
    }

    @Override
    public Completable travelToLocation(LocationId input) {
        LocationId locationFrom = investigators.getActiveInvestigator().getCurrentLocationId();
        return view.travelToLocation(locationFrom, input);
    }

    @Override
    public List<InvestigatorBasics> getInvestigatorsAtLocation(LocationId locationTo) {
        return collectToList(
                map(
                        collectToList(investigators.getOnBoardInvestigators(), in -> locationTo.equals(in.getCurrentLocationId())),
                        inv -> new InvestigatorBasicsImpl(inv.getInfo().getInvestigatorId(),
                                inv.getCurrentLocationId())
                ));
    }

    @Override
    public List<InvestigatorId> getDefeatedInvestigatorsAtLocation(LocationId locationTo) {
        return collectToList(
                map(
                        collectToList(investigators.getDefeatedInvestigators(),
                                defeatedInvestigator -> defeatedInvestigator.getCurrentLocationId() == locationTo),
                        in -> in.getInfo().getInvestigatorId()));
    }

    @Override
    public Completable gainHealth(int amount) {
        return view.gainHealth(amount);
    }

    @Override
    public Completable gainSanity(int amount) {
        return view.gainSanity(amount);
    }

    @Override
    public Completable justRest() {
        return view.justRest();
    }

    @Override
    public Completable justFocus() {
        return view.justFocus();
    }

    @Override
    public Completable gainFocus(int amount) {
        return view.gainFocus(amount);
    }

    @Override
    public Completable gainClueFromPool() {
        return view.gainClueFromPool();
    }

    @Override
    public Completable gainClueFromSpace(LocationId locationId) {
        return view.gainClueFromSpace(locationId);
    }

    @Override
    public Single<Stat> improveSkill(Stat skillRestriction) {
        return view.improveSkill(skillRestriction);
    }

    @Override
    public Single<Stat> loseImprovement(Stat stat, int amount) {
        return view.loseImprovement(stat, amount);
    }

    @Override
    public Completable showImproveSkillTable() {
        return view.showImproveSkillTable();
    }

    @Override
    public Completable hideImproveSkillTable() {
        return view.hideImproveSkillTable();
    }

    @Override
    public Single<ShowCardResponse> showCard(ShowCardRequest showCardRequest) {
        return view.showCard(showCardRequest);
    }

    @Override
    public Completable sewCard(CardInfo cardInfo) {
        return view.sewCard(cardInfo);
    }

    @Override
    public void displayText(String text) {
        view.displayText(text);
    }

    @Override
    public Completable loseFocus(Integer input) {
        return view.loseFocus(input);
    }

    @Override
    public Completable loseHealth(Integer input) {
        return view.loseHealth(input);
    }

    @Override
    public Completable loseSanity(Integer input) {
        return view.loseSanity(input);
    }

    @Override
    public Completable loseClue(Integer input) {
        return view.loseClue(input);
    }

    @Override
    public <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question) {
        return view.ask(question);
    }

    @Override
    public Completable gainCard(CardInfo cardInfo) {
        return view.gainCard(cardInfo);
    }

    @Override
    public Completable showPhase(PhaseType input) {
        return view.showPhase(input);
    }

    @Override
    public Completable showActiveInvestigator(boolean displayTransition, boolean lostInTimeAndSpace) {
        return view.showActiveInvestigator(displayTransition, lostInTimeAndSpace);
    }

    @Override
    public Completable updateHud() {
        return view.updateHud();
    }

    @Override
    public Single<InvestigatorId> selectInvestigator(List<InvestigatorId> list, String title) {
        return view.selectInvestigator(list, title);
    }

    @Override
    public Single<TradeData> trade(TradeData input) {
        return view.trade(input);
    }

    @Override
    public Completable discardClue(LocationId spawnLocationId, LocationId currentLocationId) {
        return view.discardClue(spawnLocationId, currentLocationId);
    }

    @Override
    public void displayInvestigatorPassport() {
        if (investigators.getActiveInvestigatorId() == null) {
            return;
        }
        displayInvestigatorPassport(investigators.getActiveInvestigatorId());
    }

    @Override
    public void displayInvestigatorPassport(InvestigatorId investigatorId) {
        List<CardInfo> cards = new ArrayList<>();
        cards.addAll(assetDeck.getAssets(investigatorId));
        cards.addAll(conditionsDeck.getConditions(investigatorId));
        cards.addAll(spellsDeck.getSpells(investigatorId));
        cards.addAll(artifactsDeck.getArtifacts(investigatorId));
        view.displayInvestigatorPassport(getInvestigatorBasics(investigatorId), cards);
    }

    @Override
    public void displayMonsterCard(MonsterInfo monsterInfo, Action0 onClickAction, Action0 onDisplayAction) {
        view.displayMonsterCard(monsterInfo, onClickAction, onDisplayAction);
    }

    @Override
    public void hideMonsterCard(MonsterInfo monsterInfo, Action0 onHideAction) {
        view.hideMonsterCard(monsterInfo, onHideAction);
    }

    @Override
    public Completable displayTrackWidget() {
        return view.displayTrackWidget();
    }

    @Override
    public void showHud() {
        view.showHud();
    }

    @Override
    public void hideCityInfoLabels() {
        view.hideCityInfoLabels();
    }

    @Override
    public void showCityInfoLabels() {
        view.showCityInfoLabels();
    }

    @Override
    public void enableDiscardButton() {
        view.enableDiscardButton();
    }

    @Override
    public void updateRumorButtonVisibility() {
        view.updateRumorButtonVisibility(rumors.getActiveRumors().size() > 0);
    }

    @Override
    public Completable spawnRedPins() {
        return view.showRedPinsLocation(mysteryDeck.getCurrentMysteryCard().getPinLocations());
    }

    @Override
    public Completable spawnStorm(String stormId, LocationId locationId) {
        return view.showStormLocation(stormId, locationId);
    }

    @Override
    public void justAddRedPins() {
        view.justAddRedPins(mysteryDeck.getCurrentMysteryCard().getPinLocations());
    }

    @Override
    public void justSpawnStorm(String stormId, LocationId locationId) {
        view.justSpawnStorm(stormId, locationId);
    }

    @Override
    public void clearRedPins() {
        view.clearRedPins(mysteryDeck.getCurrentMysteryCard().getPinLocations());
    }

    @Override
    public void clearStorm(String rumorId) {
        if (rumors.getActiveRumor(rumorId).isStormSpawned()) {
            view.clearStorm(rumorId);
        }
    }

    @Override
    public Single<String> selectEncounter(Collection<EncounterButtonData> encounterButtonDataList) {
        return encounterView.selectEncounter(encounterButtonDataList);
    }

    @Override
    public Completable showDelayedAnimation(InvestigatorId investigatorId) {
        return view.showDelayedAnimation(investigatorId);
    }

    @Override
    public void justAddDelayedAnimation(InvestigatorId investigatorId) {
        view.justAddDelayedAnimation(investigatorId);
    }

    @Override
    public Completable hideDelayedAnimation(InvestigatorId investigatorId) {
        return view.hideDelayedAnimation(investigatorId);
    }

    @Override
    public Completable hideSelectEncounterTable() {
        return view.hideSelectEncounterTable();
    }

    @Override
    public Completable moveCameraToLocation(LocationId locationId) {
        return view.moveCameraToLocation(locationId);
    }

    @Override
    public Completable moveClue(LocationId spawnLocationId, LocationId currentLocationId, LocationId targetLocationId) {
        return view.moveClue(spawnLocationId, currentLocationId, targetLocationId);
    }

    @Override
    public Completable removeInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return view.removeInvestigator(investigatorId, locationId);
    }

    @Override
    public Completable showInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId) {
        return view.showInvestigatorLostInTimeAndSpace(investigatorId);
    }

    @Override
    public Completable hideInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId) {
        return view.hideInvestigatorLostInTimeAndSpace(investigatorId);
    }

    @Override
    public Completable spawnInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return view.initInvestigator(new InvestigatorBasicsImpl(investigatorId, locationId));
    }

    @Override
    public void justPlaceInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        view.justPlaceInvestigator(new InvestigatorBasicsImpl(investigatorId, locationId));
    }

    @Override
    public Completable showWholeWorld() {
        return view.showWholeWorld();
    }

    @Override
    public Completable showLocationBackground(LocationId locationId) {
        LocationId currentLocationId = investigators.getActiveInvestigator().getCurrentLocationId();

        return view.showLocationBackground(locationId, getInvestigatorsAtLocation(currentLocationId));
    }

    @Override
    public Completable showCustomBackground(String backgroundId) {
        InvestigatorRead activeInvestigator = investigators.getActiveInvestigator();
        if (activeInvestigator != null) {
            LocationId currentLocationId = activeInvestigator.getCurrentLocationId();
            return view.showCustomBackground(backgroundId, getInvestigatorsAtLocation(currentLocationId));
        } else {
            return view.showCustomBackground(backgroundId, Collections.emptyList());
        }
    }

    @Override
    public Completable showLocationTypeBackground(LocationType locationType) {
        LocationId currentLocationId = investigators.getActiveInvestigator().getCurrentLocationId();
        return view.showLocationTypeBackground(locationType, getInvestigatorsAtLocation(currentLocationId));
    }

    @Override
    public Completable showResearchBackground(LocationType locationType) {
        LocationId currentLocationId = investigators.getActiveInvestigator().getCurrentLocationId();
        return view.showResearchBackground(locationType, getInvestigatorsAtLocation(currentLocationId));
    }

    @Override
    public Completable showCombatBackground() {
        LocationId currentLocationId = investigators.getActiveInvestigator().getCurrentLocationId();
        return view.showCombatBackground(getInvestigatorsAtLocation(currentLocationId));
    }

    @Override
    public Completable showWorldBackground() {
        return view.showWorldBackground();
    }

    @Override
    public Completable hideBackground() {
        return view.hideBackground();
    }

    @Override
    public Completable closeGate(LocationId location) {
        return view.closeGate(location);
    }

    @Override
    public void swapInvestigatorAndGateLayer() {
        view.swapInvestigatorAndGateLayer();
    }

    @Override
    public Single<GateInfo> selectSingleGate(List<GateInfo> gates, String titleText, String hideText) {
        return view.selectSingleGate(gates, titleText, hideText);
    }

    @Override
    public Completable devourInvestigator(InvestigatorId investigatorId) {
        return view.devourInvestigator(investigatorId);
    }

    @Override
    public Completable defeatInvestigator(InvestigatorId investigatorId, boolean health) {
        return view.defeatInvestigator(investigatorId, health);
    }

    @Override
    public Completable loadDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId, boolean defeatedByHealth) {
        return view.loadDefeatedInvestigator(investigatorId, locationId, defeatedByHealth);
    }

    @Override
    public Completable removeDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return view.removeDefeatedInvestigator(investigatorId, locationId);
    }

    @Override
    public Single<InvestigatorInfo[]> selectReplacingInvestigator(InvestigatorId removedInvestigator, Supplier<List<InvestigatorInfo>> initInvestigatorsAction) {
        return view.selectReplacingInvestigator(removedInvestigator, investigators.getAvailableInvestigators(), initInvestigatorsAction);
    }

    @Override
    public String getPreviousBackgroundName() {
        InvestigatorId activeInvestigatorId = investigators.getActiveInvestigatorId();
        if (backgroundModel.peekBackground(activeInvestigatorId) == null) {
            return null;
        }
        return backgroundModel.peekBackground(activeInvestigatorId).getBackgroundName();
    }

    @Override
    public void hideButtonsAndInvestigatorHud(boolean hideDoomOmenTrack) {
        view.hideButtonsAndInvestigatorHud(hideDoomOmenTrack);
    }

    @Override
    public void unsetActiveInvestigator() {
        view.unsetActiveInvestigator();
    }

    @Override
    public void startGame() {
        view.startGame();
    }

    @Override
    public void setClearQueueAction(Runnable input) {
        view.setClearQueueAction(input);
    }

    @Override
    public void restartGame() {
        view.restartGame();
    }

    @Override
    public void postRunnable(Runnable runnable) {
        view.postRunnable(runnable);
    }

    @Override
    public Completable increaseAncientOnePower(int increment) {
        return view.increaseAncientOnePower(increment);
    }

    @Override
    public AncientOneInfo getAncientOneInfo() {
        return ancientOne.getAncientOneInfo();
    }
}
