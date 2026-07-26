package sk.sivak.eldritchhorror.core.service;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
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
import sk.sivak.eldritchhorror.core.eventtype.data.GateSpawnData;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.List;

/**
 * @author msivak
 */
public interface GameService extends CommonService {

    void startGame(int numPlayers);

    void performAction();

    void justPerformAction();

    void checkIfSomeoneIsDefeated();

    void monsterSurge(int monstersCount);

    Single<List<GateSpawnData>> spawnGates(int amount, int monsterSurge);

    Single<List<GateSpawnData>> spawnGatesQuick(int amount, int monsterSurge);

    Single<List<SpawnMonsterData>> spawnMonsters(int amount, LocationId locationId, boolean quick);

    void spawnMonster(SpawnMonsterData spawnMonsterData);

    Completable showExpeditionLocation();

    void spawnRedPins();

    void justAddRedPins();

    void showCurrentMysteryCard(boolean moveCamera, boolean quick);

    void advanceActiveMystery();

    void advanceCurrentMysteryCard(int amount);

    void showAncientOneCard();

    Completable initInvestigators();

    void displayText(String text);

    Single<ShowCardResponse> showCard(ShowCardRequest showCardRequest);

    <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question);

    void gainCondition(ConditionId debt);

    void gainCondition(ConditionId conditionId, boolean confirmRequired);

    void gainCondition(ConditionTrait conditionTrait);

    void gainSpell(SpellId spellId);

    void gainSpell(SpellTrait spellTrait);

    void gainSpell();

    void gainAsset(AssetInfo assetInfo);

    void gainArtifact(AssetTrait assetTrait);

    void gainArtifact(ArtifactId artifactId);

    void gainArtifact();

    void changeAndShowPhase();

    void showPhase();

    void initPhase();

    Single<LocationId> selectLocation(SelectLocationData selectLocationData);

    Single<GateInfo> selectSingleGate(SelectSingleGateData selectSingleGateData);

    void clearRedPins();

    void spawnVortex(LocationId locationId);

    void removeVortex(LocationId currentLocationId);

    void askToRateThisGame();

    void restartGame();

    void sewCard(CardInfo cardInfo);
}
