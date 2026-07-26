package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

import java.util.List;

public interface MonsterController {
    Completable loseHealth(int amount);

    Completable tearMonsterCardApart(MonsterInfo monsterInfo);

    Completable tearMysteryCardApart();

    Completable tearRumorCardApart();

    Completable defeatMonster(MonsterInfo monsterInfo);

    Single<MonsterInfo> selectSingleMonster(List<? extends MonsterInfo> availableMonsters, String titleText, String hideText);

    Completable discardMonster(MonsterInfo input);

    Completable highlightSpecialText(MonsterInfo monsterInfo);

    Completable highlightReckoningText(MonsterInfo monsterInfo);

    Completable highlightSpawnText(MonsterInfo monsterInfo);

    Completable moveMonster(MonsterInfo monsterInfo, LocationId targetLocationId);

    Completable restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText);


}
