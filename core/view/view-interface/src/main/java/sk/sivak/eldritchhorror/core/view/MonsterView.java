package sk.sivak.eldritchhorror.core.view;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

import java.util.List;

public interface MonsterView {
    Completable restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText);

    Completable loseHealth(int amount);

    Completable tearMonsterCardApart(MonsterInfo monsterInfo);

    Completable tearRumorCardApart();

    Completable defeatMonster(MonsterInfo monsterInfo);

    Single<MonsterInfo> selectSingleMonster(List<? extends MonsterInfo> availableMonsters, String titleText, String hideText);

    Completable discardMonster(MonsterInfo input);

    Completable highlightSpecialText(MonsterInfo monsterInfo);

    Completable highlightReckoningText(MonsterInfo monsterInfo);

    Completable highlightSpawnText(MonsterInfo monsterInfo);

    Completable tearMysteryCardApart(MysteryCardInfo currentMysteryCard);

    Completable moveMonster(MonsterInfo monsterInfo, LocationId targetLocationId);


}
