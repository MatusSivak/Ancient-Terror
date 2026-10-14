package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import java.lang.reflect.Proxy;
import java.util.*;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.model.*;
import static org.junit.Assert.*;

public class HallOfFameInvestigatorsTest {
    @Test public void recordsAllLostInvestigatorsButKeepsLivingOffBoardSurvivors() {
        List<InvestigatorId> played = Arrays.asList(InvestigatorId.THE_NUN, InvestigatorId.THE_PRIEST,
                InvestigatorId.THE_SAILOR, InvestigatorId.THE_SPY);
        InvestigatorInfo info = (InvestigatorInfo) Proxy.newProxyInstance(InvestigatorInfo.class.getClassLoader(),
                new Class[]{InvestigatorInfo.class}, (p,m,a) -> InvestigatorId.THE_SPY);
        InvestigatorRead survivor = (InvestigatorRead) Proxy.newProxyInstance(InvestigatorRead.class.getClassLoader(),
                new Class[]{InvestigatorRead.class}, (p,m,a) -> m.getName().equals("getInfo") ? info : true);
        InvestigatorsRead roster = (InvestigatorsRead) Proxy.newProxyInstance(InvestigatorsRead.class.getClassLoader(),
                new Class[]{InvestigatorsRead.class}, (p,m,a) -> {
                    if (m.getName().equals("getPlayedInvestigators")) return played;
                    if (m.getName().equals("getSelectedInvestigators")) return Collections.singletonList(survivor);
                    throw new AssertionError("Must not rely on on-board investigators or remaining defeated tokens: " + m);
                });
        HallOfFameData record = new HallOfFameData();
        assertNull(record.getDefeatedInvestigators()); // Old records have unknown outcomes.
        DisplayVictoryPaperAction.recordInvestigators(record, roster);
        assertEquals(Arrays.asList("Nun", "Priest", "Sailor", "Spy"), record.getInvestigators());
        assertEquals(Arrays.asList("Nun", "Priest", "Sailor"), record.getDefeatedInvestigators());
    }
}
