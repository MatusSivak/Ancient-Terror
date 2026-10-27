package sk.sivak.eldritchhorror.core.view.map.monster;

import org.junit.Test;
import static org.junit.Assert.*;

public class MonsterFacingTest {
    @Test public void turnsThroughAnEdgeWithoutZeroScaleOrRestartingEachFrame() {
        MonsterFacing facing = new MonsterFacing(true);
        facing.face(false, true);
        assertEquals(1f, facing.scaleX(), .001f);
        facing.update(.06f);
        assertTrue(facing.scaleX() > 0f && facing.scaleX() < 1f);
        facing.face(false, true);
        facing.update(.06f);
        assertEquals(.04f, Math.abs(facing.scaleX()), .001f);
        facing.update(.12f);
        assertEquals(-1f, facing.scaleX(), .001f);
    }

    @Test public void retargetingMidTurnIsContinuousAndLargeStepsFinish() {
        MonsterFacing facing = new MonsterFacing(true);
        facing.followMovement(-2f); facing.update(.08f);
        float before = facing.scaleX();
        facing.followMovement(2f);
        assertEquals(before, facing.scaleX(), .001f);
        facing.update(1f);
        assertEquals(1f, facing.scaleX(), .001f);
    }

    @Test public void leftFacingArtworkIsNotMirroredWhenLookingAtInvestigator() {
        MonsterFacing facing = new MonsterFacing(false);
        facing.face(false, false); facing.update(0f);
        assertEquals(1f, facing.scaleX(), .001f);
        facing.followMovement(2f); facing.update(.3f);
        assertEquals(-1f, facing.scaleX(), .001f);
        facing.followMovement(0f); facing.followMovement(-.01f); facing.update(.3f);
        assertEquals(-1f, facing.scaleX(), .001f);
    }
}
