package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.model.MysteryDeckRead;
import sk.sivak.eldritchhorror.core.view.MonsterView;

import java.util.List;

public class MonsterControllerImpl implements MonsterController {

    private MonsterView monsterView;

    private MysteryDeckRead mysteryDeck;

    public void setMysteryDeck(MysteryDeckRead mysteryDeck) {
        this.mysteryDeck = mysteryDeck;
    }

    public void setMonsterView(MonsterView monsterView) {
        this.monsterView = monsterView;
    }

    @Override
    public Completable loseHealth(int amount) {
        return monsterView.loseHealth(amount);
    }

    @Override
    public Completable tearMonsterCardApart(MonsterInfo monsterInfo) {
        return monsterView.tearMonsterCardApart(monsterInfo);
    }

    @Override
    public Completable tearMysteryCardApart() {
        return monsterView.tearMysteryCardApart(mysteryDeck.getCurrentMysteryCard());
    }

    @Override
    public Completable tearRumorCardApart() {
        return monsterView.tearRumorCardApart();
    }

    @Override
    public Completable defeatMonster(MonsterInfo monsterInfo) {
        return monsterView.defeatMonster(monsterInfo);
    }

    @Override
    public Single<MonsterInfo> selectSingleMonster(List<? extends MonsterInfo> availableMonsters, String titleText, String hideText) {
        return monsterView.selectSingleMonster(availableMonsters, titleText, hideText);
    }

    @Override
    public Completable discardMonster(MonsterInfo input) {
        return monsterView.discardMonster(input);
    }

    @Override
    public Completable highlightSpecialText(MonsterInfo monsterInfo) {
        return monsterView.highlightSpecialText(monsterInfo);
    }

    @Override
    public Completable highlightReckoningText(MonsterInfo monsterInfo) {
        return monsterView.highlightReckoningText(monsterInfo);
    }

    @Override
    public Completable highlightSpawnText(MonsterInfo monsterInfo) {
        return monsterView.highlightSpawnText(monsterInfo);
    }

    @Override
    public Completable moveMonster(MonsterInfo monsterInfo, LocationId targetLocationId) {
        return monsterView.moveMonster(monsterInfo, targetLocationId);
    }

    @Override
    public Completable restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText) {
        return monsterView.restoreHealth(monsterInfo, amount, highlightSpecialText, highlightReckoningText);
    }
}
