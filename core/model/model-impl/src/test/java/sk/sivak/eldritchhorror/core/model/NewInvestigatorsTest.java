package sk.sivak.eldritchhorror.core.model;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.model.util.InvestigatorsHelper;
import java.util.*;
import static org.junit.Assert.*;

public class NewInvestigatorsTest {
    private final InvestigatorId[] added = {InvestigatorId.THE_BUTLER, InvestigatorId.THE_PRIEST, InvestigatorId.THE_NUN, InvestigatorId.THE_EXPLORER};
    private final InvestigatorId[] addedSecondWave = {InvestigatorId.THE_REPORTER, InvestigatorId.THE_PROFESSOR, InvestigatorId.THE_DILETTANTE, InvestigatorId.THE_SALESMAN};

    @Test public void allFourRequireTheExistingBonusUnlock() {
        List<InvestigatorInfo> free = InvestigatorsHelper.initAvailableInvestigators(false);
        List<InvestigatorInfo> unlocked = InvestigatorsHelper.initAvailableInvestigators(true);
        List<? extends InvestigatorInfo> locked = InvestigatorsHelper.initLockedInvestigators();
        assertEquals(16, free.size());
        assertEquals(28, unlocked.size());
        assertEquals(12, locked.size());
        List<InvestigatorId> premium = new ArrayList<>(Arrays.asList(added));
        premium.addAll(Arrays.asList(addedSecondWave));
        for (InvestigatorId id : premium) {
            assertNull(find(free, id));
            assertNotNull(find(unlocked, id));
            assertNotNull(find(locked, id));
        }
    }
    @Test public void newCharactersAndExhaustedAbilitySurviveSaveAndLoad() {
        Investigators original = new Investigators();
        original.initAvailableInvestigators(true);
        original.initWithPlayers(4);
        InvestigatorInfo[] selected = new InvestigatorInfo[4];
        for (int i=0;i<4;i++) selected[i]=find(original.getAvailableInvestigators(), added[i]);
        original.initSelectedInvestigators(selected);
        original.getInvestigator(InvestigatorId.THE_EXPLORER).setPassiveAbilityDisabled(true);
        assertEquals(1, original.getInvestigator(InvestigatorId.THE_NUN).getShipTickets());
        Investigators restored = new Investigators();
        restored.load(original.save(), true);
        for (InvestigatorId id : added) {
            InvestigatorRead investigator=restored.getInvestigator(id);
            assertEquals(id, investigator.getInfo().getInvestigatorId());
            assertEquals(13, Arrays.stream(Stat.values()).mapToInt(investigator.getInfo()::getBaseStat).sum());
            assertFalse(investigator.getInfo().getBio().isEmpty());
        }
        assertTrue(restored.getInvestigator(InvestigatorId.THE_EXPLORER).isPassiveAbilityDisabled());
        assertEquals(1, restored.getInvestigator(InvestigatorId.THE_NUN).getShipTickets());
    }
    @Test public void secondWaveSurvivesSaveAndLoad() {
        Investigators original = new Investigators();
        original.initAvailableInvestigators(true);
        original.initWithPlayers(4);
        InvestigatorInfo[] selected = new InvestigatorInfo[4];
        for (int i=0;i<4;i++) selected[i]=find(original.getAvailableInvestigators(), addedSecondWave[i]);
        original.initSelectedInvestigators(selected);
        assertEquals(1, original.getInvestigator(InvestigatorId.THE_DILETTANTE).getShipTickets());
        Investigators restored = new Investigators();
        restored.load(original.save(), true);
        for (InvestigatorId id : addedSecondWave) {
            InvestigatorRead investigator=restored.getInvestigator(id);
            assertEquals(id, investigator.getInfo().getInvestigatorId());
            assertEquals(13, Arrays.stream(Stat.values()).mapToInt(investigator.getInfo()::getBaseStat).sum());
            assertFalse(investigator.getInfo().getBio().isEmpty());
            assertFalse(investigator.getInfo().getActionText().isEmpty());
            assertFalse(investigator.getInfo().getAbilityText().isEmpty());
        }
        assertEquals(1, restored.getInvestigator(InvestigatorId.THE_DILETTANTE).getShipTickets());
    }
    @Test public void transferPreservesTheCardAndRejectsDuplicates() {
        ConditionsDeck deck = new ConditionsDeck();
        deck.createDeck();
        ConditionInfo boon = deck.gainCondition(ConditionId.BLESSED, InvestigatorId.THE_PRIEST);
        assertTrue(deck.transfer(InvestigatorId.THE_PRIEST, InvestigatorId.THE_NUN, boon));
        assertSame(boon, deck.getCondition(InvestigatorId.THE_NUN, ConditionId.BLESSED));
        assertFalse(deck.hasCondition(InvestigatorId.THE_PRIEST, ConditionId.BLESSED));
        ConditionInfo second = deck.gainCondition(ConditionId.BLESSED, InvestigatorId.THE_PRIEST);
        assertFalse(deck.transfer(InvestigatorId.THE_PRIEST, InvestigatorId.THE_NUN, second));
        assertSame(second, deck.getCondition(InvestigatorId.THE_PRIEST, ConditionId.BLESSED));
        ConditionsDeck restored = new ConditionsDeck();
        restored.load(deck.save());
        assertEquals(boon.getConditionBack().getConditionBackId(), restored.getCondition(InvestigatorId.THE_NUN, ConditionId.BLESSED).getConditionBack().getConditionBackId());
    }
    private InvestigatorInfo find(List<? extends InvestigatorInfo> list, InvestigatorId id) {
        for (InvestigatorInfo info:list) if(info.getInvestigatorId()==id)return info;
        return null;
    }
}

