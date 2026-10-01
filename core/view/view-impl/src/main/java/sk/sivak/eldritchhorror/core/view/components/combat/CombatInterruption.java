package sk.sivak.eldritchhorror.core.view.components.combat;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.action.PrePlayedTokenLoss;
import sk.sivak.eldritchhorror.core.view.game.MapStage;

/**
 * Something steps into a running combat (a card prompt such as Flesh Ward, another investigator taking over,
 * a spell test): the monster fades away until the fight itself continues.
 */
public final class CombatInterruption {

    private static MonsterCombatTable table;
    private static InvestigatorId fighter;
    private static boolean suspended;

    private CombatInterruption() {
    }

    public static synchronized void start(MonsterCombatTable combatTable, InvestigatorId fighterId) {
        table = combatTable;
        fighter = fighterId;
        suspended = false;
    }

    /** Combat is over; nothing to bring back. */
    public static synchronized void finish() {
        table = null;
        fighter = null;
        suspended = false;
    }

    public static synchronized void suspend() {
        if (table == null || suspended) {
            return;
        }
        suspended = true;
        // The interruption may change or prevent the loss; tokens torn by fireballs go back and are lost the usual way.
        PrePlayedTokenLoss.undo();
        table.setInterrupted(true);
    }

    public static synchronized void onActiveInvestigatorShown(InvestigatorId investigatorId) {
        if (table != null && fighter != null && investigatorId != fighter) {
            suspend();
        }
    }

    /** The fight continues: darken the world again and bring the monster back. */
    public static synchronized void resume() {
        if (table == null || !suspended) {
            return;
        }
        suspended = false;
        if (!MapStage.isWorldDarkened()) {
            MapStage.darkenWorld();
        }
        table.setInterrupted(false);
    }

    public static synchronized boolean isSuspended() {
        return suspended;
    }
}
