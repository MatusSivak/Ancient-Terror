package sk.sivak.eldritchhorror.core.model;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveData;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

public class InvestigatorsParticipationTest {
    private Investigators start(boolean bonus, InvestigatorId id) {
        Investigators investigators = new Investigators();
        investigators.initAvailableInvestigators(bonus);
        investigators.initWithPlayers(1);
        investigators.initSelectedInvestigators(new InvestigatorInfo[]{find(investigators, id)});
        return investigators;
    }

    private InvestigatorInfo find(Investigators investigators, InvestigatorId id) {
        for (InvestigatorInfo info : investigators.getAvailableInvestigators()) {
            if (info.getInvestigatorId() == id) return info;
        }
        throw new AssertionError(id);
    }

    @Test public void excludesLockedAndUnselectedBonusInvestigators() {
        for (boolean bonus : new boolean[]{false, true}) {
            Investigators investigators = start(bonus, InvestigatorId.THE_SPY);
            assertEquals(Collections.singletonList(InvestigatorId.THE_SPY), investigators.getPlayedInvestigators());
        }
        assertEquals(Collections.singletonList(InvestigatorId.THE_VIOLINIST),
                start(true, InvestigatorId.THE_VIOLINIST).getPlayedInvestigators());
    }

    @Test public void retainsDefeatedAndDevouredParticipantsAcrossSaveAndLoad() {
        Investigators investigators = start(true, InvestigatorId.THE_SPY);
        investigators.defeatInvestigator(InvestigatorId.THE_SPY);
        investigators.initReplacingInvestigator(InvestigatorId.THE_SPY, find(investigators, InvestigatorId.THE_VIOLINIST));
        investigators.removeDefeatedInvestigator(InvestigatorId.THE_SPY);
        investigators.devourInvestigator(InvestigatorId.THE_VIOLINIST);
        investigators.initReplacingInvestigator(InvestigatorId.THE_VIOLINIST, find(investigators, InvestigatorId.THE_SAILOR));
        investigators.lostInTimeAndSpace(InvestigatorId.THE_SAILOR);
        Investigators restored = new Investigators();
        restored.load(investigators.save(), true);
        assertEquals(Arrays.asList(InvestigatorId.THE_SPY, InvestigatorId.THE_VIOLINIST, InvestigatorId.THE_SAILOR),
                restored.getPlayedInvestigators());
    }

    @Test public void legacySaveExcludesLockedBonusInvestigators() {
        InvestigatorsSaveData saved = start(false, InvestigatorId.THE_SPY).save();
        saved.setPlayedInvestigators(null);
        Investigators restored = new Investigators();
        restored.load(saved, false);
        assertEquals(Collections.singletonList(InvestigatorId.THE_SPY), restored.getPlayedInvestigators());
    }

    @Test public void newGameClearsParticipation() {
        Investigators investigators = start(true, InvestigatorId.THE_VIOLINIST);
        investigators.initAvailableInvestigators(false);
        assertTrue(investigators.getPlayedInvestigators().isEmpty());
    }
}
