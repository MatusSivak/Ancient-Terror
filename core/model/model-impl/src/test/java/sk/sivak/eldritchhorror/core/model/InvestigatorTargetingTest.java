package sk.sivak.eldritchhorror.core.model;

import java.util.*;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveData;
import static org.junit.Assert.*;

public class InvestigatorTargetingTest {
    private static final InvestigatorId[] TEAM = {InvestigatorId.THE_SPY, InvestigatorId.THE_SAILOR,
            InvestigatorId.THE_POLITICIAN, InvestigatorId.THE_NUN};

    private Investigators team() {
        Investigators investigators = new Investigators();
        investigators.initAvailableInvestigators(true);
        investigators.initWithPlayers(TEAM.length);
        InvestigatorInfo[] selected = new InvestigatorInfo[TEAM.length];
        for (int i = 0; i < TEAM.length; i++) selected[i] = find(investigators, TEAM[i]);
        investigators.initSelectedInvestigators(selected);
        return investigators;
    }

    private InvestigatorInfo find(Investigators investigators, InvestigatorId id) {
        return investigators.getAvailableInvestigators().stream()
                .filter(info -> info.getInvestigatorId() == id).findFirst().get();
    }

    @Test public void eachEffectDrawsAgainAndCanRepeatWithoutChangingTurnOrder() {
        Investigators investigators = team();
        investigators.changeActiveInvestigator(TEAM[2]);
        int[] choices = {3, 0, 0, 2, 1};
        Random random = new Random() {
            private int call;
            @Override public int nextInt(int bound) {
                assertEquals(TEAM.length, bound);
                return choices[call++];
            }
        };
        for (int choice : choices) assertEquals(TEAM[choice],
                investigators.getRandomInvestigator(random).getInfo().getInvestigatorId());
        assertEquals(TEAM[2], investigators.getActiveInvestigatorId());
        assertEquals(Arrays.asList(TEAM), investigators.getInvestigatorIdsInTurnOrder());
    }

    @Test public void eligibilityIsReevaluatedForEveryDraw() {
        Investigators investigators = team();
        investigators.defeatInvestigator(TEAM[0]);
        investigators.devourInvestigator(TEAM[1]);
        investigators.lostInTimeAndSpace(TEAM[2]);
        for (int i = 0; i < 20; i++) assertEquals(TEAM[3],
                investigators.getRandomInvestigator().getInfo().getInvestigatorId());
        investigators.lostInTimeAndSpace(TEAM[3]);
        assertNull(investigators.getRandomInvestigator());
        investigators.getInvestigator(TEAM[2]).setLostInTimeAndSpace(false);
        assertEquals(TEAM[2], investigators.getRandomInvestigator().getInfo().getInvestigatorId());
    }

    @Test public void roundStartsAndSaveRestoreKeepTurnOrderIncludingReplacementSlots() {
        Investigators investigators = team();
        for (int i = 0; i < 3; i++) {
            investigators.changeActiveInvestigator(TEAM[3]);
            investigators.initActiveInvestigator();
            assertEquals(TEAM[0], investigators.getActiveInvestigatorId());
        }
        investigators.defeatInvestigator(TEAM[0]);
        investigators.initActiveInvestigator();
        assertEquals(TEAM[1], investigators.getActiveInvestigatorId());
        investigators.initReplacingInvestigator(TEAM[0], find(investigators, InvestigatorId.THE_SOLDIER));
        investigators.lostInTimeAndSpace(InvestigatorId.THE_SOLDIER);
        Investigators restored = new Investigators();
        restored.load(investigators.save(), true);
        restored.initActiveInvestigator();
        // Lost investigators still have a turn to return to the board.
        assertEquals(InvestigatorId.THE_SOLDIER, restored.getActiveInvestigatorId());
        assertEquals(Arrays.asList(InvestigatorId.THE_SOLDIER, TEAM[1], TEAM[2], TEAM[3]),
                restored.getInvestigatorIdsInTurnOrder());
    }

    @Test public void noRemainingInvestigatorsClearsActiveTurn() {
        Investigators investigators = team();
        investigators.initActiveInvestigator();
        for (InvestigatorId id : TEAM) investigators.devourInvestigator(id);
        investigators.initActiveInvestigator();
        assertNull(investigators.getActiveInvestigator());
        assertNull(investigators.getRandomInvestigator());
    }
}
