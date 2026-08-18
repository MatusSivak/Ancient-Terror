package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NextTokenSpawnAnimationControllerTest {
    @Test
    public void newTokenStartsSpawnAnimation() {
        NextTokenSpawnAnimationController controller = new NextTokenSpawnAnimationController();

        assertTrue(controller.setNextToken(SymbolType.ONE));

        assertEquals(SymbolType.ONE, controller.getCurrentNextToken());
        assertEquals(NextTokenSpawnAnimationController.Phase.SPAWNING, controller.getPhase());
    }

    @Test
    public void unchangedTokenDoesNotRestartAnimation() {
        NextTokenSpawnAnimationController controller = new NextTokenSpawnAnimationController();
        controller.setNextToken(SymbolType.ONE);
        controller.update(0.2f);
        float alphaBefore = controller.getTokenAlpha();

        assertFalse(controller.setNextToken(SymbolType.ONE));

        assertEquals(alphaBefore, controller.getTokenAlpha(), 0f);
    }

    @Test
    public void newlyGeneratedTokenAnimatesWhenItsValueMatchesConsumedToken() {
        NextTokenSpawnAnimationController controller = new NextTokenSpawnAnimationController();
        controller.setNextToken(SymbolType.ONE);
        controller.setNextToken(null);

        assertTrue(controller.setNextToken(SymbolType.ONE));

        assertEquals(NextTokenSpawnAnimationController.Phase.SPAWNING, controller.getPhase());
        assertEquals(0f, controller.getTokenAlpha(), 0f);
    }

    @Test
    public void rapidUpdateRestartsForNewestToken() {
        NextTokenSpawnAnimationController controller = new NextTokenSpawnAnimationController();
        controller.setNextToken(SymbolType.ONE);
        controller.update(0.2f);

        assertTrue(controller.setNextToken(SymbolType.SIX));

        assertEquals(SymbolType.ONE, controller.getPreviousNextToken());
        assertEquals(SymbolType.SIX, controller.getCurrentNextToken());
        assertEquals(0f, controller.getTokenAlpha(), 0f);
        assertEquals(NextTokenSpawnAnimationController.Phase.SPAWNING, controller.getPhase());
    }

    @Test
    public void nullTokenClearsAnimationSafely() {
        NextTokenSpawnAnimationController controller = new NextTokenSpawnAnimationController();
        controller.setNextToken(SymbolType.THREE);

        assertTrue(controller.setNextToken(null));

        assertEquals(SymbolType.THREE, controller.getPreviousNextToken());
        assertEquals(null, controller.getCurrentNextToken());
        assertEquals(NextTokenSpawnAnimationController.Phase.IDLE, controller.getPhase());
        assertEquals(0f, controller.getTokenAlpha(), 0f);
    }

    @Test
    public void animationSettlesToIdleState() {
        NextTokenSpawnAnimationController controller = new NextTokenSpawnAnimationController();
        controller.setNextToken(SymbolType.FIVE);

        controller.update(NextTokenSpawnAnimationController.SPAWN_DURATION);
        assertEquals(NextTokenSpawnAnimationController.Phase.SETTLING, controller.getPhase());

        controller.update(NextTokenSpawnAnimationController.SETTLE_DURATION);
        assertEquals(NextTokenSpawnAnimationController.Phase.IDLE, controller.getPhase());
        assertEquals(1f, controller.getTokenAlpha(), 0f);
        assertEquals(1f, controller.getTokenScale(), 0f);
        assertEquals(0f, controller.getEffectAlpha(), 0f);
    }
}
