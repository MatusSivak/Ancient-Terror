package sk.sivak.eldritchhorror.core.model;

import java.util.*;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.*;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.model.save.MysteryDeckSaveData;
import sk.sivak.eldritchhorror.core.model.util.*;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Yig.*;

public class YigModelTest {
    @Test public void setupAndAwakeningSurviveSaveLoad() {
        AncientOne one=new AncientOne();one.setAncientOneInfo(AncientOneHelper.createYig());
        assertEquals(10,one.getAncientOneInfo().getStartingDoom());
        assertEquals(16,one.getAncientOneInfo().getMythosCardCount());
        assertEquals(6,Collections.frequency(one.getAncientOneInfo().getRemovedMonsters(),NonEpicMonsterId.CULTIST));
        assertEquals(1,Collections.frequency(one.getAncientOneInfo().getRemovedMonsters(),NonEpicMonsterId.SERPENT_PEOPLE));
        AncientOne restored=new AncientOne();restored.load(one.save());
        assertFalse(restored.getAncientOneInfo().isAwaken());assertEquals(3,restored.getAncientOneInfo().getMysteriesRequired());
        one.awaken();one.increasePower(5);restored.load(one.save());
        assertTrue(restored.getAncientOneInfo().isAwaken());assertEquals(4,restored.getAncientOneInfo().getMysteriesRequired());
        assertEquals(5,restored.getAncientOneInfo().getPower());
    }
    @Test public void sixDistinctMysteriesAndFinalBattleScaleForEveryPlayerCount() {
        for(int players=1;players<=8;players++) {
            List<MysteryCardInfo> cards=YigMysteryDeck.create(players);
            assertEquals(7,cards.size());assertEquals(SERPENTS_NEST,cards.get(3).getMysteryCardId());
            Set<MysteryCardId> ids=new HashSet<>();
            for(MysteryCardInfo card:cards) {
                assertTrue(ids.add(card.getMysteryCardId()));
                if(card.getMysteryCardId()==RISE_OF_THE_SERPENT_PEOPLE || card.getMysteryCardId()==KN_YAN_UNEARTHED)
                    assertEquals((players+1)/2,(int)card.getMysteryComplexity());
                if(card.getMysteryCardId()==RISE_OF_THE_SERPENT_PEOPLE)
                    assertEquals((players+1)/2,card.getPinLocations().size());
                if(card.getMysteryCardId()==SERPENTS_NEST)assertEquals(players+3,(int)card.getMysteryComplexity());
            }
        }
    }
    @Test public void restoredSpecialDeckKeepsDrawOrderAndResearchPinsStayDynamic() {
        MysteryDeck deck=new MysteryDeck();deck.initMysteryDeck(AncientOneId.YIG,4);
        MysteryCardInfo card;
        do { card=deck.drawNewMystery(); } while(card.getMysteryCardId()!=KN_YAN_UNEARTHED);
        card.getSpecialEncounterDeck().addAll(Arrays.asList(8,2,5));card.setProgressSupplier(() -> 1);
        MysteryDeckSaveData save=deck.save();
        MysteryDeck restored=new MysteryDeck();restored.initMysteryDeck(AncientOneId.YIG,4);restored.load(save);
        MysteryCardInfo loaded=restored.drawNewMystery();
        assertEquals(KN_YAN_UNEARTHED,loaded.getMysteryCardId());
        assertEquals(Arrays.asList(8,2,5),loaded.getSpecialEncounterDeck());
        assertEquals(Collections.singletonList(LocationId.SPACE_6),loaded.getPinLocations());
        assertEquals(1,save.getProgress());
    }
    @Test public void removedEldritchTokensStayRemovedAfterLoad() {
        MysteryDeck deck=new MysteryDeck();deck.initMysteryDeck(AncientOneId.YIG,4);
        MysteryCardInfo card;do {card=deck.drawNewMystery();}while(card.getMysteryCardId()!=RISE_OF_THE_SERPENT_PEOPLE);
        card.getPinLocations().clear();card.setProgressSupplier(() -> 2);
        MysteryDeck restored=new MysteryDeck();restored.initMysteryDeck(AncientOneId.YIG,4);restored.load(deck.save());
        assertTrue(restored.drawNewMystery().getPinLocations().isEmpty());
    }
    @Test public void crownClueCreditSurvivesSaveLoad() {
        MysteryDeck deck=new MysteryDeck();deck.initMysteryDeck(AncientOneId.YIG,4);
        MysteryCardInfo card;do {card=deck.drawNewMystery();}while(card.getMysteryCardId()!=CROWN_OF_THE_SERPENT);
        card.setProgressSupplier(() -> 0);card.setPinLocationsSupplier(Collections::emptyList);card.setClueCredit(2);
        MysteryDeck restored=new MysteryDeck();restored.initMysteryDeck(AncientOneId.YIG,4);restored.load(deck.save());
        assertEquals(2,restored.drawNewMystery().getClueCredit());
    }
}
