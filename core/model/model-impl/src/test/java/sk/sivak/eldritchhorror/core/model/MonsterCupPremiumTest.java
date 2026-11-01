package sk.sivak.eldritchhorror.core.model;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.model.save.MonsterCupSaveData;
import sk.sivak.eldritchhorror.core.model.save.MonsterCupSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.MonsterCupHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class MonsterCupPremiumTest {

    @Test
    public void expansionMonstersArePremium() {
        assertEquals(26, NonEpicMonsterId.premiumMonsters().size());
        assertFalse(NonEpicMonsterId.CULTIST.isPremium());
        assertTrue(NonEpicMonsterId.ZOOG.isPremium());
    }

    @Test
    public void freeCupExcludesPremiumMonsters() {
        List<AbstractMonsterInfo> cup = new MonsterCupHelper().createMonsterCup(false);
        for (AbstractMonsterInfo monster : cup) {
            assertFalse(monster.getMonsterId().toString(), ((NonEpicMonsterId) monster.getMonsterId()).isPremium());
        }
    }

    @Test
    public void fullGameCupContainsEveryPremiumMonster() {
        List<AbstractMonsterInfo> cup = new MonsterCupHelper().createMonsterCup(true);
        for (NonEpicMonsterId id : NonEpicMonsterId.premiumMonsters()) {
            boolean found = false;
            for (AbstractMonsterInfo monster : cup) found |= monster.getMonsterId() == id;
            assertTrue(id.toString(), found);
        }
    }

    @Test
    public void monsterCupDefaultsToLocked() {
        MonsterCup cup = new MonsterCup();
        cup.createMonsterCup();
        for (AbstractMonsterInfo monster : cup.getMonstersInCup()) {
            assertFalse(((NonEpicMonsterId) monster.getMonsterId()).isPremium());
        }
    }

    @Test
    public void lockedPremiumMonsterFromOldSaveLoadsButDoesNotReturnToCup() {
        MonsterCup cup = new MonsterCup();
        cup.setPremiumMonstersAvailableSupplier(() -> false);
        cup.createMonsterCup();
        int cupSize = cup.getMonstersInCup().size();

        MonsterCupSaveData saveData = new MonsterCupSaveData();
        MonsterCupSaveData.MonsterSaveData monster = new MonsterCupSaveData.MonsterSaveData();
        monster.setMonsterId(NonEpicMonsterId.ZOOG);
        monster.setCurrentLocationId(LocationId.ARKHAM);
        monster.setCurrentHealth(1);
        LinkedList<MonsterCupSaveDataRead.MonsterSaveDataRead> monsters = new LinkedList<>(Collections.singletonList(monster));
        saveData.setMonsters(monsters);

        cup.load(saveData);
        List<MonsterInfo> onBoard = cup.getMonstersAtLocation(LocationId.ARKHAM);
        assertEquals(1, onBoard.size());
        assertEquals(NonEpicMonsterId.ZOOG, onBoard.get(0).getMonsterId());

        cup.defeatMonster(onBoard.get(0));
        assertTrue(cup.getMonstersAtLocation(LocationId.ARKHAM).isEmpty());
        assertEquals(cupSize, cup.getMonstersInCup().size());
    }
}
