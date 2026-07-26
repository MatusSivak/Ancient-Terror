package sk.sivak.eldritchhorror.core.service;

import java8.features.stream.Stream;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.provider.GameActionProvider;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.GateSpawnData;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class GameServiceImpl extends AbstractCommonService implements GameService {

    private GameActionProvider gameActionProvider;
    private InvestigatorService investigatorService;
    private TokenService tokenService;
    private InitGameService initGameService;

    public void setInitGameService(InitGameService initGameService) {
        this.initGameService = initGameService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void setGameActionProvider(GameActionProvider gameActionProvider) {
        this.gameActionProvider = gameActionProvider;
    }

    public void setInvestigatorService(InvestigatorService investigatorService) {
        this.investigatorService = investigatorService;
    }

    @Override
    public void startGame(int numPlayers) {
        execute(new ActionCommand<>(gameActionProvider.initGame(), "INIT_GAME"));
    }

    @Override
    public void performAction() {
        hold();
        execute(new ActionCommand<>(gameActionProvider.hasAnyFreeAction(), "HAS_ANY_FREE_ACTION"));
        execute(new ActionCommand<>(gameActionProvider.performActionStart(), "PERFORM_ACTION_START"));
        justPerformAction();
        afterActionPerformed();
        release();
    }

    @Override
    public void justPerformAction() {
        hold();
        convertToNull();
        execute(new ActionCommand<>(gameActionProvider.verifyNotDelayed(), "VERIFY_NOT_DELAYED"));
        execute(new ActionCommand<>(gameActionProvider.initActionButton(), "INIT_ACTION_BUTTON"));
        executeHookable(new CollectAvailableActionsData(), gameActionProvider.collectAvailableActions());
        executeHookable(gameActionProvider.filterPerformedActions());
        execute(new ActionCommand<>(gameActionProvider.disableOrRemoveActions(), "DISABLE_OR_REMOVE_ACTIONS"));
        executeHookable(gameActionProvider.selectAction());
        executeHookable(gameActionProvider.executeAction());
        execute(new ActionCommand<>(gameActionProvider.verifyNotDelayed(), "VERIFY_NOT_DELAYED"));
        checkIfSomeoneIsDefeated();
        release();
    }

    @Override
    public void checkIfSomeoneIsDefeated() {
        execute(new ActionCommand<>(gameActionProvider.checkDefeatedInActionPhase(), "CHECK_DEFEATED_IN_ACTION_PHASE"));
    }

    private void afterActionPerformed() {
        executeHookable(gameActionProvider.afterActionPerformed());
        executeHookable(gameActionProvider.withAfterActionPerformedData());
    }

    @Override
    public void monsterSurge(int monstersCount) {
        executeHookable(gameActionProvider.monsterSurge(monstersCount));
    }

    @Override
    public Single<List<GateSpawnData>> spawnGates(int amount, int monsterSurge) {
        return Single.create((onSub) -> spawnGatesLoop(onSub, new LinkedList<>(), monsterSurge, amount, false));
    }

    @Override
    public Single<List<GateSpawnData>> spawnGatesQuick(int amount, int monsterSurge) {
        return Single.create((onSub) -> spawnGatesLoop(onSub, new LinkedList<>(), monsterSurge, amount, true));
    }

    private void spawnGatesLoop(SingleSubscriber<? super List<GateSpawnData>> ss,
                                List<GateSpawnData> result, int monsterSurge, int amount, boolean quick) {
        if (amount == 0) {
            ss.onSuccess(result);
            return;
        }
        GateSpawnData gateSpawnData = new GateSpawnData();

        spawnGate(quick).subscribe(gate -> {
            if (gate == null) {
                result.add(gateSpawnData);
                spawnGatesLoop(ss, result, monsterSurge, amount-1, quick);
                return;
            }
            gateSpawnData.setGate(gate);
            spawnMonsters(monsterSurge, gate.getLocationId(), quick).subscribe(spawnMonsterDataList -> {
                gateSpawnData.setMonsters(new ArrayList<>(Stream.map(spawnMonsterDataList, SpawnMonsterData::getMonsterInfo)));
                result.add(gateSpawnData);
                spawnGatesLoop(ss, result, monsterSurge, amount - 1, quick);
            });
        });
    }

    private Single<GateInfo> spawnGate(boolean quick) {
        return Single.create(onSub -> executeHookable(gameActionProvider.spawnGate(quick), onSub));
    }

    @Override
    public Single<List<SpawnMonsterData>> spawnMonsters(int amount, LocationId locationId, boolean quick) {
        return Single.create((onSub) -> spawnMonsterLoop(onSub, new LinkedList<>(), locationId, amount, quick));
    }

    private void spawnMonsterLoop(SingleSubscriber<? super List<SpawnMonsterData>> ss,
                                  List<SpawnMonsterData> result,
                                  LocationId locationId,
                                  int amount, boolean quick) {
        if (amount == 0) {
            ss.onSuccess(result);
            return;
        }
        spawnMonster(locationId, quick).subscribe(monsterData -> {
            result.add(monsterData);
            spawnMonsterLoop(ss, result, locationId, amount - 1, quick);
        });
    }

    private Single<SpawnMonsterData> spawnMonster(LocationId locationId, boolean quick) {
        return Single.create(onSub -> {
            SpawnMonsterData data = new SpawnMonsterData();
            data.setLocationId(locationId);
            executeHookable(data, gameActionProvider.spawnMonster(quick), onSub);
        });
    }

    @Override
    public void spawnMonster(SpawnMonsterData spawnMonsterData) {
        executeHookable(spawnMonsterData, gameActionProvider.spawnMonster(false));
    }

    @Override
    public Completable showExpeditionLocation() {
        return Completable.create(onSub -> {
            execute(new ActionCommand<>(gameActionProvider.showExpeditionLocation(), "SHOW_EXPEDITION_LOCATION"));
            onSub.onCompleted();
        });
    }

    @Override
    public Completable initInvestigators() {
        return Completable.create(onSub -> {
            execute(new ActionCommand<>(gameActionProvider.initInvestigators(), "INIT_INVESTIGATORS"));
            onSub.onCompleted();
        });
    }

    @Override
    public void showCurrentMysteryCard(boolean moveCamera, boolean quick) {
        execute(new ActionCommand<>(gameActionProvider.showCurrentMysteryCard(moveCamera, quick), "SHOW_CURRENT_MYSTERY_CARD"));
    }

    @Override
    public void advanceActiveMystery() {
        executeHookable(gameActionProvider.advanceActiveMystery());
    }

    @Override
    public void advanceCurrentMysteryCard(int amount) {
        execute(new ActionCommand<>(gameActionProvider.advanceCurrentMysteryCard(amount), "ADVANCE_CURRENT_MYSTERY_CARD"));
    }

    @Override
    public void showAncientOneCard() {
        execute(new ActionCommand<>(gameActionProvider.showAncientOneCard(), "SHOW_ANCIENT_ONE_CARD"));
    }

    @Override
    public void spawnRedPins() {
        execute(new ActionCommand<>(gameActionProvider.spawnRedPins(), "SHOW_CURRENT_MYSTERY_LOCATION"));
    }

    @Override
    public void justAddRedPins() {
        execute(new ActionCommand<>(gameActionProvider.justAddRedPins(), "JUST_ADD_RED_PINS"));
    }

    @Override
    public void displayText(String text) {
        execute(new ActionCommand<>(gameActionProvider.displayText(text), "DISPLAY_TEXT"));
    }

    @Override
    public Single<ShowCardResponse> showCard(ShowCardRequest showCardRequest) {
        return Single.create(singleSubscriber -> executeHookable(showCardRequest,
                gameActionProvider.showCard(),
                singleSubscriber));
    }

    @Override
    public <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question) {
        return Single.create(singleSubscriber -> executeHookable(question,
                gameActionProvider.ask(),
                singleSubscriber));
    }

    @Override
    public void gainCondition(ConditionId conditionId) {
        gainCard(new GainCardData(conditionId));
    }

    @Override
    public void gainCondition(ConditionId conditionId, boolean confirmRequired) {
        GainCardData gainCardData = new GainCardData(conditionId);
        gainCardData.setConfirmRequired(confirmRequired);
        gainCard(gainCardData);
    }

    @Override
    public void gainCondition(ConditionTrait conditionTrait) {
        gainCard(new GainCardData(conditionTrait));
    }

    @Override
    public void gainSpell(SpellId spellId) {
        gainCard(new GainCardData(spellId));
    }

    @Override
    public void gainSpell(SpellTrait spellTrait) {
        gainCard(new GainCardData(spellTrait));
    }

    @Override
    public void gainSpell() {
        gainCard(new GainCardData(true));
    }

    @Override
    public void gainAsset(AssetInfo assetInfo) {
        if (assetInfo == null) {
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Asset not found anywhere.");
            ask(question).subscribe(ok -> {});
            return;
        }
        gainCard(new GainCardData(assetInfo));
    }

    @Override
    public void gainArtifact(AssetTrait assetTrait) {
        gainCard(new GainCardData(assetTrait, true));
    }

    @Override
    public void gainArtifact(ArtifactId artifactId) {
        gainCard(new GainCardData(artifactId));
    }

    @Override
    public void gainArtifact() {
        gainCard(new GainCardData(null, true));
    }

    private void gainCard(GainCardData gainCardData) {
        hold();
        executeHookable(gainCardData, gameActionProvider.selectCardToGain());
        executeHookable(gameActionProvider.registerGainedCard());
        release();
    }

    @Override
    public void changeAndShowPhase() {
        hold();
        execute(new ActionCommand<>(gameActionProvider.changePhase(), "CHANGE_PHASE"));
        showPhase();
        initGameService.saveGame();
        release();
    }

    @Override
    public void showPhase() {
        executeHookable(gameActionProvider.showPhase());
    }

    @Override
    public void initPhase() {
        executeHookable(gameActionProvider.initPhase());
    }

    @Override
    public Single<LocationId> selectLocation(SelectLocationData selectLocationData) {
        return Single.create(onSub -> executeHookable(selectLocationData, gameActionProvider.selectLocation(), onSub));
    }

    @Override
    public Single<GateInfo> selectSingleGate(SelectSingleGateData selectSingleGateData) {
        return Single.create(onSub -> executeHookable(selectSingleGateData, gameActionProvider.selectSingleGate(), onSub));
    }

    @Override
    public void clearRedPins() {
        execute(new ActionCommand<>(gameActionProvider.clearRedPins(), "CLEAR_RED_PINS"));
    }

    @Override
    public void spawnVortex(LocationId locationId) {
        execute(new ActionCommand<>(gameActionProvider.spawnVortex(locationId), "SPAWN_VORTEX"));
    }

    @Override
    public void removeVortex(LocationId currentLocationId) {
        execute(new ActionCommand<>(gameActionProvider.removeVortex(currentLocationId), "REMOVE_VORTEX"));
    }

    @Override
    public void askToRateThisGame() {
        execute(new ActionCommand<>(gameActionProvider.askToRateThisGame(), "ASK_TO_RATE_THIS_GAME"));
    }

    @Override
    public void restartGame() {
        execute(new ActionCommand<>(gameActionProvider.restartGame(), "RESTART_GAME"));
    }

    @Override
    public void sewCard(CardInfo cardInfo) {
        execute(new ActionCommand<>(gameActionProvider.sewCard(cardInfo), "SEW_CARD"));
    }
}
